package com.backend.service.impl;

import com.backend.domain.RestBean;
import com.backend.domain.entity.Account;
import com.backend.domain.entity.Commit;
import com.backend.domain.entity.Event;
import com.backend.domain.vo.ListVO;
import com.backend.mapper.AccountMapper;
import com.backend.mapper.CommitMapper;
import com.backend.mapper.EventMapper;
import com.backend.service.CommitService;
import com.backend.utils.MapperUtil;
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

/**
 * (Commit)表服务实现类
 *
 * @author makejava
 * @since 2024-09-19 15:02:58
 */
@Service("commitService")
@RequiredArgsConstructor
public class CommitServiceImpl extends ServiceImpl<CommitMapper, Commit> implements CommitService {

    private final AccountMapper accountMapper;

    private final EventMapper eventMapper;

    private final MapperUtil mapperUtil;

    @Override
    public RestBean<ListVO<Commit>> listCommit(Integer pageNum, Integer pageSize, String title) {
        Page<Commit> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Commit> queryWrapper = new LambdaQueryWrapper<Commit>().like(StringUtils.hasText(title), Commit::getContent, title);
        Page<Commit> coursePage = page(page, queryWrapper);
        List<Commit> records = coursePage.getRecords();

        Set<Integer> eventSet = records.stream().map(Commit::getEventId).collect(Collectors.toSet());
        Set<Integer> userIdSet = records.stream().map(Commit::getCreateBy).collect(Collectors.toSet());

        Map<Integer, Event> eventMap = mapperUtil.mapToIdMap(eventMapper,eventSet, Event::getId);
        Map<Integer, Account> accountMap = mapperUtil.mapToIdMap(accountMapper, userIdSet, Account::getId);

        List<Commit> list = coursePage.getRecords()
                .stream().peek(event -> {
                    event.setEvent(eventMap.get(event.getEventId()));
                    event.setCreator(accountMap.get(event.getCreateBy()));
                }).toList();

        return RestBean.success(new ListVO<>(coursePage.getTotal(), list));
    }

    @Override
    public RestBean<List<Commit>> getCommitsByEventId(Integer id) {
        LambdaQueryWrapper<Commit> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Commit::getEventId, id);
        List<Commit> commits = this.list(wrapper);
        if (!commits.isEmpty()) {
            Set<Integer> userIdSet = commits.stream().map(Commit::getCreateBy).collect(Collectors.toSet());

            Map<Integer, Account> accountMap = mapperUtil.mapToIdMap(accountMapper, userIdSet, Account::getId);

            return RestBean.success(commits
                    .stream()
                    .peek(commit -> commit.setCreator(accountMap.get(commit.getCreateBy())))
                    .toList());
        } else
            return RestBean.success(commits);
    }

    @Override
    @Transactional
    public RestBean<Commit> saveCommit(Commit commit) {
        this.saveOrUpdate(commit);
        commit.setCreator(accountMapper.selectById(commit.getCreateBy()));
        return RestBean.success(commit);
    }

    @Override
    public RestBean<Void> deleteCommit(Integer id) {
        if (removeById(id))
            return RestBean.success();
        else
            return RestBean.failure(400, "出现异常");
    }
}
