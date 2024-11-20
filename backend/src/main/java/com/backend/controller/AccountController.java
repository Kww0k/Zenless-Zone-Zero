package com.backend.controller;

import com.backend.domain.RestBean;
import com.backend.domain.entity.Account;
import com.backend.domain.entity.AccountEvent;
import com.backend.domain.vo.AccountAuthVO;
import com.backend.domain.vo.ListVO;
import com.backend.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/account")
public class AccountController {

    private final AccountService accountService;

    @PostMapping("/changeChooseEvent")
    public RestBean<Void> changeChooseEvent(@RequestBody AccountEvent accountEvent) {
        return accountService.changeChooseEvent(accountEvent);
    }

    @GetMapping("/list")
    public RestBean<ListVO<Account>> listAccount(Integer pageNum, Integer pageSize, String name) {
        return accountService.listAccount(pageNum, pageSize, name);
    }

    @PostMapping("/save")
    public RestBean<AccountAuthVO> saveAccount(@RequestBody Account account) {
        return accountService.saveAccount(account);
    }

    @DeleteMapping("/delete")
    public RestBean<Void> deleteAccount(Integer id) {
        return accountService.deleteAccount(id);
    }
}
