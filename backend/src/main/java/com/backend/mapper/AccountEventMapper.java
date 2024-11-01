package com.backend.mapper;

import com.backend.domain.entity.Account;
import com.backend.domain.entity.AccountEvent;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * (AccountEvent)表数据库访问层
 *
 * @author makejava
 * @since 2024-09-19 16:26:06
 */
@Mapper
public interface AccountEventMapper extends BaseMapper<AccountEvent> {

    @Select("select a.* from account_event ae left join account a on ae.account_id = a.id where ae.event_id = #{eventId}")
    List<Account> listPlayerByEventId(Integer eventId);
}
