package com.backend.service;

import com.backend.domain.RestBean;
import com.backend.domain.entity.Account;
import com.backend.domain.entity.AccountEvent;
import com.backend.domain.entity.Event;
import com.backend.domain.vo.ListVO;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface AccountService  extends IService<Account>, UserDetailsService {

    RestBean<Void> changeChooseEvent(AccountEvent accountEvent);

    RestBean<ListVO<Account>> listAccount(Integer pageNum, Integer pageSize, String name);

    RestBean<Void> saveAccount(Account account);

    RestBean<Void> deleteAccount(Integer id);
}