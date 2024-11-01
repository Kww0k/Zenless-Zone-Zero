package com.backend.service.impl;

import com.backend.domain.RestBean;
import com.backend.domain.entity.Message;
import com.backend.mapper.MessageMapper;
import com.backend.service.MessageService;
import com.backend.utils.SecurityUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * (Message)表服务实现类
 *
 * @author makejava
 * @since 2024-10-30 09:24:42
 */
@Service("messageService")
@RequiredArgsConstructor
public class MessageServiceImpl extends ServiceImpl<MessageMapper, Message> implements MessageService {

    private final SecurityUtil securityUtil;

    @Override
    @Transactional
    public RestBean<List<Message>> listMessage(Integer roomId) {
        Integer userId = securityUtil.getUserId();
        LambdaQueryWrapper<Message> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Message::getRoomId, roomId);

        List<Message> messageList = this.list(wrapper).stream()
                .peek(message -> {
                    if (message.getReadOne().equals(userId))
                        message.setReadOne(0);
                    if (message.getReadTwo().equals(userId))
                        message.setReadTwo(0);
                }).toList();

        this.updateBatchById(messageList);
        return RestBean.success(messageList);
    }

}
