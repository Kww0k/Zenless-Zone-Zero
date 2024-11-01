package com.backend.netty;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.backend.domain.LoginUser;
import com.backend.domain.RestBean;
import com.backend.domain.entity.Account;
import com.backend.domain.entity.Message;
import com.backend.domain.entity.Room;
import com.backend.service.MessageService;
import com.backend.utils.JwtUtils;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
public class SocketServer {

    private final JwtUtils jwtUtils;

    private final MessageService messageService;

    @Autowired
    public SocketServer(JwtUtils jwtUtils, MessageService messageService) {
        this.jwtUtils = jwtUtils;
        this.messageService = messageService;
    }

    private static final int PORT = 8848;
    private static final Map<Room, Map<Integer, Channel>> userChannels = new ConcurrentHashMap<>();

    public void start() throws InterruptedException {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) {
                            ch.pipeline().addLast(new HttpServerCodec());
                            ch.pipeline().addLast(new ChunkedWriteHandler());
                            ch.pipeline().addLast(new HttpObjectAggregator(1024 * 64));
                            ch.pipeline().addLast(new WebSocketServerProtocolHandler("/ws"));
                            ch.pipeline().addLast(new ChatHandler(userChannels, jwtUtils, messageService));
                        }
                    });

            ChannelFuture f = b.bind(PORT).sync();
            log.info("聊天服务启动，等待用户连接...");
            f.channel().closeFuture().sync();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }

    }


    private static class ChatHandler extends ChannelInboundHandlerAdapter {
        private static Map<Room, Map<Integer, Channel>> userChannels = new ConcurrentHashMap<>();
        private static JwtUtils jwtUtils;
        private static MessageService messageService;

        public ChatHandler(Map<Room, Map<Integer, Channel>> userChannels, JwtUtils jwtUtils, MessageService messageService) {
            ChatHandler.userChannels = userChannels;
            ChatHandler.jwtUtils = jwtUtils;
            ChatHandler.messageService = messageService;
        }

        @Override
        public void channelActive(ChannelHandlerContext ctx) {
            log.info("用户连接，当前用户数量为：{}", getUniqueChannelCount() + 1);
        }

        @Override
        @Transactional
        public void channelRead(ChannelHandlerContext ctx, Object msg) {
            // 默认返回错误信息
            String backMessage = RestBean.failure(500, "系统错误").toJsonString();
            if (msg instanceof TextWebSocketFrame frame) {
                // 解析信息，获取token
                String message = frame.text();
                JSONObject jsonMessage = (JSONObject) JSON.parse(message);
                DecodedJWT jwt = jwtUtils.resolveJwt((String) jsonMessage.get("token"));

                if (jwt != null) {
                    // 获取用户信息和房间信息
                    LoginUser loginUser = jwtUtils.toUser(jwt);
                    Account account = loginUser.getAccount();
                    Room room = jsonMessage.getObject("room", Room.class);

                    Map<Integer, Channel> roomChannelMap = userChannels.get(room);
                    if (roomChannelMap == null) {
                        roomChannelMap = new ConcurrentHashMap<>();
                        roomChannelMap.put(account.getId(), ctx.channel());
                    } else {
                        roomChannelMap.put(account.getId(), ctx.channel());
                    }
                    userChannels.put(room, roomChannelMap);
                    log.info("id为：{}的用户在房间：{}中。", account.getId(), room.getId());

                    if (jsonMessage.get("type").equals("open")) { // 连接时发起请求，确保连接成功
                        backMessage = RestBean.success("用户" + account.getNickname() + "连接成功").toJsonString();
                        log.info("用户{}连接成功，id为：{}， 当前用户数量为：{}", account.getNickname(), account.getId(), getUniqueChannelCount());
                    }
                    else if (jsonMessage.get("type").equals("message")) { // 发送信息
                        String content = (String) jsonMessage.get("message");
                        if (StringUtils.hasText(content)) {
                            Integer toUserId = (Integer) jsonMessage.get("toUserId");
                            if (toUserId != null) {
                                if (!toUserId.equals(account.getId())) { // 检查发起人和接收人是否一致
                                    // 寻找接收人的channel，如果在线则放入房间
                                    for (Map<Integer, Channel> channelMap : userChannels.values()) {
                                        Channel channel = channelMap.get(toUserId);
                                        if (channel != null) {
                                            roomChannelMap.put(toUserId, channel);
                                        }
                                    }

                                    Channel toUserchannel = null;

                                    for (Room allRoom : userChannels.keySet()) {
                                        for (Integer i : userChannels.get(allRoom).keySet()) {
                                            if (i.equals(toUserId) && userChannels.get(allRoom).get(i) != null) {
                                                toUserchannel = userChannels.get(allRoom).get(i);
                                                break;
                                            }
                                        }
                                    }

                                    Message saveMessage = new Message()
                                            .setContent(content)
                                            .setToUserId(toUserId)
                                            .setRoomId(room.getId())
                                            .setCreateBy(account.getId())
                                            .setCreateTime(new Date((Long) jsonMessage.get("timestamp")));

                                    if (toUserchannel != null) {
                                        saveMessage.setReadOne(0);
                                        saveMessage.setReadTwo(0);
                                        if (messageService.save(saveMessage)) {
                                            String toUserMessage = RestBean.success(201, saveMessage).toJsonString();
                                            toUserchannel.writeAndFlush(new TextWebSocketFrame(toUserMessage));
                                            log.info("id为：{}的用户发送消息：{}。发送给id为：{}的用户，消息接收方在线", account.getId(), content, toUserId);
                                            backMessage = RestBean.success(202, saveMessage).toJsonString();
                                        }
                                    } else {
                                        if (Objects.equals(room.getUserOne(), account.getId())) {
                                            saveMessage.setReadOne(0);
                                            saveMessage.setReadTwo(toUserId);
                                        } else {
                                            saveMessage.setReadOne(toUserId);
                                            saveMessage.setReadTwo(0);
                                        }
                                        if (messageService.save(saveMessage)) {
                                            log.info("id为：{}的用户发送消息：{}。发送给id为：{}的用户，消息接收方离线", account.getId(), content, toUserId);
                                            backMessage = RestBean.success(202, saveMessage).toJsonString();
                                        }
                                    }
                                }
                            }
                        } else {
                            log.warn("id为：{}的用户发送了一条空消息", account.getId());
                            backMessage = RestBean.failure(404, "消息不能为空").toJsonString();
                        }
                    }
                } else {
                    log.error("用户未登录，当前用户数量为：{}", getUniqueChannelCount());
                    backMessage = RestBean.failure(401, "没有登录").toJsonString();
                }
            }
            ctx.channel().writeAndFlush(new TextWebSocketFrame(backMessage));
        }

        @Override
        public void channelInactive(ChannelHandlerContext ctx) {
            Integer userId = null; // 假设你已经有了用户的ID
            for (Room room : userChannels.keySet()) {
                for (Integer i : userChannels.get(room).keySet()) {
                    if (userChannels.get(room).get(i).equals(ctx.channel())) {
                        userChannels.get(room).remove(i);
                        userId = i; // 记录用户ID
                    }
                }
            }

            if (userId != null) {
                log.info("用户id为：{} 的用户断开了连接， 当前用户数量为 {}", userId, getUniqueChannelCount());
            }

        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
            log.error(cause.getMessage());
            ctx.close();
        }

        public int getUniqueChannelCount() {
            return userChannels.values().stream()
                    .flatMap(map -> map.values().stream())
                    .distinct()
                    .toList()
                    .size();
        }
    }
}
