package com.backend.service.impl;

import com.backend.domain.LoginUser;
import com.backend.domain.RestBean;
import com.backend.domain.entity.Account;
import com.backend.domain.entity.AccountEvent;
import com.backend.domain.vo.ListVO;
import com.backend.mapper.AccountEventMapper;
import com.backend.mapper.AccountMapper;
import com.backend.service.AccountService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl extends ServiceImpl<AccountMapper, Account> implements AccountService {

    private final AccountEventMapper accountEventMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Account account = baseMapper.selectOne(new LambdaQueryWrapper<Account>()
                .eq(Account::getUsername, username));
        if (account == null)
            throw new UsernameNotFoundException("用户名或密码错误");
        return new LoginUser(account);
    }

    @Override
    public RestBean<Void> changeChooseEvent(AccountEvent accountEvent) {
        LambdaQueryWrapper<AccountEvent> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AccountEvent::getAccountId, accountEvent.getAccountId());
        if (accountEventMapper.selectCount(wrapper) > 0)
            accountEventMapper.delete(wrapper);
        else
            accountEventMapper.insert(accountEvent);

        return RestBean.success();
    }

    @Override
    public RestBean<ListVO<Account>> listAccount(Integer pageNum, Integer pageSize, String name) {
        // 创建分页对象
        Page<Account> page = new Page<>(pageNum, pageSize);
        // 创建查询条件
        LambdaQueryWrapper<Account> queryWrapper = new LambdaQueryWrapper<Account>().like(StringUtils.hasText(name), Account::getNickname, name);
        // 执行分页查询
        Page<Account> accountPage = page(page, queryWrapper);
        return RestBean.success(new ListVO<>(accountPage.getTotal(), accountPage.getRecords()));
    }

    @Override
    @Transactional
    public RestBean<Void> saveAccount(Account account) {
        if (StringUtils.hasText(account.getPassword())) {
            account.setPassword(new BCryptPasswordEncoder().encode(account.getPassword()));
        } else {
            account.setPassword(null);
        }
        if (saveOrUpdate(account))
            return RestBean.success();
        else
            return RestBean.failure(400, "出现异常");
    }

    @Override
    public RestBean<Void> deleteAccount(Integer id) {
        if (removeById(id))
            return RestBean.success();
        else
            return RestBean.failure(400, "出现异常");
    }
}