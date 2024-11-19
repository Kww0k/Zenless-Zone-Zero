package com.backend.service.impl;

import com.backend.domain.RestBean;
import com.backend.domain.entity.Account;
import com.backend.domain.entity.Message;
import com.backend.domain.vo.ListVO;
import com.backend.mapper.AccountMapper;
import com.backend.mapper.MessageMapper;
import com.backend.service.MessageService;
import com.backend.utils.MapperUtil;
import com.backend.utils.SecurityUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    private final AccountMapper accountMapper;

    private final MapperUtil mapperUtil;

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

    @Override
    public RestBean<ListVO<Message>> list(Integer pageNum, Integer pageSize, String content) {
        // 创建分页对象
        Page<Message> page = new Page<>(pageNum, pageSize);
        // 创建查询条件
        LambdaQueryWrapper<Message> queryWrapper = new LambdaQueryWrapper<Message>().like(StringUtils.hasText(content), Message::getContent, content);
        // 执行分页查询
        Page<Message> messagePage = page(page, queryWrapper);
        List<Message> records = messagePage.getRecords();
        Set<Integer> userIdSet = records.stream()
                .flatMap(message -> Stream.of(message.getCreateBy(), message.getToUserId()))
                .collect(Collectors.toSet());

        Map<Integer, Account> accountMap = mapperUtil.mapToIdMap(accountMapper, userIdSet, Account::getId);

        List<Message> messageList = records.stream()
                .peek(message -> message
                        .setToUserName(accountMap.get(message.getToUserId()).getNickname())
                        .setFromUserName(accountMap.get(message.getCreateBy()).getNickname()))
                .toList();

        return RestBean.success(new ListVO<>(messagePage.getTotal(), messageList));
    }

    @Override
    public RestBean<Void> deleteMessage(Integer id) {
        if (removeById(id))
            return RestBean.success();
        else
            return RestBean.failure(400, "出现异常");
    }

}
