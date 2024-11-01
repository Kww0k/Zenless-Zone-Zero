package com.backend.service;

import com.backend.domain.RestBean;
import com.backend.domain.entity.Account;
import com.backend.domain.entity.Event;
import com.backend.domain.vo.ListVO;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;


/**
 * (Event)表服务接口
 *
 * @author makejava
 * @since 2024-09-06 10:21:50
 */
public interface EventService extends IService<Event> {

    RestBean<ListVO<Event>> listEvent(Integer pageNum, Integer pageSize, String title);

    RestBean<Void> saveEvent(Event event);

    RestBean<Void> deleteEvent(Integer id);

    RestBean<Boolean> isJoinEvent(Integer eventId);

    RestBean<Void> joinEvent(Integer eventId);

    RestBean<Void> outEvent(Integer eventId);

    RestBean<List<Account>> listPlayerByEventId(Integer eventId);

    RestBean<List<Event>> listEventForPlayer();
}
