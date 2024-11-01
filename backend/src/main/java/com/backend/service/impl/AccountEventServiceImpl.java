package com.backend.service.impl;

import com.backend.domain.entity.AccountEvent;
import com.backend.mapper.AccountEventMapper;
import com.backend.service.AccountEventService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * (AccountEvent)表服务实现类
 *
 * @author makejava
 * @since 2024-09-19 16:26:08
 */
@Service("accountEventService")
public class AccountEventServiceImpl extends ServiceImpl<AccountEventMapper, AccountEvent> implements AccountEventService {

}
