package com.backend.service.impl;

import com.backend.domain.RestBean;
import com.backend.domain.entity.Account;
import com.backend.domain.entity.AccountEvent;
import com.backend.domain.entity.Event;
import com.backend.domain.entity.Tag;
import com.backend.domain.vo.ListVO;
import com.backend.mapper.AccountEventMapper;
import com.backend.mapper.AccountMapper;
import com.backend.mapper.EventMapper;
import com.backend.mapper.TagMapper;
import com.backend.service.EventService;
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
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * (Event)表服务实现类
 *
 * @author makejava
 * @since 2024-09-06 10:21:50
 */
@Service("eventService")
@RequiredArgsConstructor
public class EventServiceImpl extends ServiceImpl<EventMapper, Event> implements EventService {

    private final AccountMapper accountMapper;

    private final AccountEventMapper accountEventMapper;

    private final TagMapper tagMapper;

    private final SecurityUtil securityUtil;

    private final MapperUtil mapperUtil;

    @Override
    public RestBean<ListVO<Event>> listEvent(Integer pageNum, Integer pageSize, String title, Integer tagId) {
        // 创建分页对象
        Page<Event> page = new Page<>(pageNum, pageSize);
        // 创建查询条件
        LambdaQueryWrapper<Event> queryWrapper = new LambdaQueryWrapper<Event>().like(StringUtils.hasText(title), Event::getTitle, title)
                .eq(Objects.nonNull(tagId), Event::getTagId, tagId);
        // 执行分页查询
        Page<Event> coursePage = page(page, queryWrapper);

        List<Event> records = coursePage.getRecords();

        Set<Integer> userIdSet = records.stream().map(Event::getCreateBy).collect(Collectors.toSet());
        Map<Integer, Account> accountMap = mapperUtil.mapToIdMap(accountMapper, userIdSet, Account::getId);

        Set<Integer> tagIdSet = records.stream().map(Event::getTagId).collect(Collectors.toSet());
        Map<Integer, Tag> tagMap = mapperUtil.mapToIdMap(tagMapper, tagIdSet, Tag::getId);

        List<Event> list = coursePage.getRecords()
                .stream().peek(event ->
                        event
                                .setCreator(accountMap.get(event.getCreateBy()))
                                .setTag(Objects.equals(0, event.getTagId()) ? "学生个人发起" : tagMap.get(event.getTagId()).getName())
                ).toList();

        return RestBean.success(new ListVO<>(coursePage.getTotal(), list));
    }

    @Override
    public RestBean<List<Event>> myList() {
        Integer userId = securityUtil.getUserId();
        LambdaQueryWrapper<Event> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Event::getCreateBy, userId);
        List<Event> records = list(wrapper);

        Set<Integer> userIdSet = records.stream().map(Event::getCreateBy).collect(Collectors.toSet());
        Map<Integer, Account> accountMap = mapperUtil.mapToIdMap(accountMapper, userIdSet, Account::getId);

        Set<Integer> tagIdSet = records.stream().map(Event::getTagId).collect(Collectors.toSet());
        Map<Integer, Tag> tagMap = mapperUtil.mapToIdMap(tagMapper, tagIdSet, Tag::getId);

        return RestBean.success(records
                .stream().peek(event ->
                        event
                                .setCreator(accountMap.get(event.getCreateBy()))
                                .setTag(Objects.equals(0, event.getTagId()) ? "学生个人发起" : tagMap.get(event.getTagId()).getName()))
                .toList());
    }

    @Override
    @Transactional
    public RestBean<Void> saveEvent(Event event) {
        if (saveOrUpdate(event))
            return RestBean.success();
        else
            return RestBean.failure(400, "出现异常");
    }

    @Override
    public RestBean<Void> deleteEvent(Integer id) {
        if (removeById(id))
            return RestBean.success();
        else
            return RestBean.failure(400, "出现异常");
    }

    @Override
    public RestBean<Boolean> isJoinEvent(Integer eventId) {
        return RestBean.success(
                accountEventMapper.selectCount(new LambdaQueryWrapper<AccountEvent>().eq(AccountEvent::getEventId, eventId)
                        .eq(AccountEvent::getAccountId, securityUtil.getUserId())) > 0
        );
    }


    @Override
    public RestBean<Void> joinEvent(Integer eventId) {
        Integer userId = securityUtil.getUserId();
        accountEventMapper.insert(new AccountEvent(userId, eventId));
        return RestBean.success();
    }

    @Override
    public RestBean<Void> outEvent(Integer eventId) {
        accountEventMapper.delete(new LambdaQueryWrapper<AccountEvent>().eq(AccountEvent::getEventId, eventId)
                .eq(AccountEvent::getAccountId, securityUtil.getUserId()));
        return RestBean.success();
    }

    @Override
    public RestBean<List<Account>> listPlayerByEventId(Integer eventId) {
        return RestBean.success(accountEventMapper.listPlayerByEventId(eventId));
    }

    @Override
    public RestBean<List<Event>> listEventForPlayer() {
        return RestBean.success(baseMapper.listEventByUserId(securityUtil.getUserId()));
    }
}
