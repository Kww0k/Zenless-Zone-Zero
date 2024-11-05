package com.backend.service.impl;

import com.backend.domain.RestBean;
import com.backend.domain.entity.Account;
import com.backend.domain.entity.Message;
import com.backend.domain.entity.Room;
import com.backend.domain.vo.RoomVO;
import com.backend.mapper.AccountMapper;
import com.backend.mapper.MessageMapper;
import com.backend.mapper.RoomMapper;
import com.backend.service.RoomService;
import com.backend.utils.SecurityUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * (Room)表服务实现类
 *
 * @author makejava
 * @since 2024-10-31 08:59:39
 */
@Service("roomService")
@RequiredArgsConstructor
public class RoomServiceImpl extends ServiceImpl<RoomMapper, Room> implements RoomService {

    private final SecurityUtil securityUtil;

    private final AccountMapper accountMapper;

    private final MessageMapper messageMapper;

    @Override
    public RestBean<List<RoomVO>> getRoomList() {
        Integer userId = securityUtil.getUserId();
        LambdaQueryWrapper<Room> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Room::getUserOne, userId)
                .or()
                .eq(Room::getUserTwo, userId);
        List<Room> rooms = this.list(wrapper);

        return RestBean.success(
                rooms.stream()
                        .map(room -> getRoomVO(room, userId))
                        .filter(Objects::nonNull)
                        .toList()
        );
    }

    @Override
    public RestBean<RoomVO> getRoomInfoById(Integer id) {
        Integer userId = securityUtil.getUserId();
        Room room = this.getById(id);
        RoomVO roomVO = getRoomVO(room, userId);
        if (roomVO != null)
            return RestBean.success(roomVO);
        else
            return RestBean.failure(404, "房间信息未找到");
    }

    @Override
    public RestBean<Room> saveRoom(Room room) {
        LambdaQueryWrapper<Room> wrapper = new LambdaQueryWrapper<>();
        wrapper
                .eq(Room::getUserOne, room.getUserOne())
                .eq(Room::getUserTwo, room.getUserTwo())
                .or()
                .eq(Room::getUserOne, room.getUserTwo())
                .eq(Room::getUserTwo, room.getUserOne());
        Room room1 = this.getOne(wrapper);
        if (room1 != null) {
            return RestBean.success(room1);
        } else {
            save(room);
            return RestBean.success(room);
        }
    }

    private RoomVO getRoomVO(Room room, Integer userId) {
        Account account;
        if (room.getUserOne().equals(userId))
            account = accountMapper.selectById(room.getUserTwo());
        else
            account = accountMapper.selectById(room.getUserOne());

        LambdaQueryWrapper<Message> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper
                .eq(Message::getRoomId, room.getId())
                .and(wq -> wq
                        .eq(Message::getCreateBy, userId)
                        .or()
                        .eq(Message::getToUserId, userId))
                .orderByDesc(Message::getCreateTime)
                .last("limit 1");
        Message message = messageMapper.selectOne(lambdaQueryWrapper);
        if (message == null)
            return null;

        LambdaQueryWrapper<Message> countWrapper = new LambdaQueryWrapper<>();
        countWrapper
                .eq(Message::getRoomId, room.getId())
                .and(wq -> wq
                        .eq(Message::getCreateBy, userId)
                        .or()
                        .eq(Message::getToUserId, userId))
                .and(wq -> wq
                        .eq(Message::getReadOne, userId))
                .or()
                .eq(Message::getReadTwo, userId);
        return new RoomVO(room, account, message, messageMapper.selectCount(countWrapper));
    }
}
