package com.backend.controller;

import com.backend.domain.RestBean;
import com.backend.domain.entity.Account;
import com.backend.domain.entity.Event;
import com.backend.domain.vo.ListVO;
import com.backend.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/event")
public class EventController {

    private final EventService eventService;

    @GetMapping("/list")
    public RestBean<ListVO<Event>> listEvent(Integer pageNum, Integer pageSize, String title, Integer tagId) {
        return eventService.listEvent(pageNum, pageSize, title, tagId);
    }

    @GetMapping("/controlList")
    public RestBean<ListVO<Event>> controlListEvent(Integer pageNum, Integer pageSize, String title, Integer tagId) {
        return eventService.controlListEvent(pageNum, pageSize, title, tagId);
    }

    @GetMapping("/myList")
    public RestBean<List<Event>> myList() {
        return eventService.myList();
    }

    @GetMapping("/myJoinList")
    public RestBean<List<Event>> myJoinList() {
        return eventService.myJoinList();
    }

    @PostMapping("/save")
    public RestBean<Void> saveEvent(@RequestBody Event event) {
        return eventService.saveEvent(event);
    }

    @DeleteMapping("/delete")
    public RestBean<Void> deleteEvent(Integer id) {
        return eventService.deleteEvent(id);
    }

    @PostMapping("/addReview")
    public RestBean<Void> addReview(@RequestBody Event event) {
        eventService.updateById(event);
        return RestBean.success();
    }

    @GetMapping("/isJoinEvent")
    public RestBean<Boolean> isJoinEvent(Integer eventId) {
        return eventService.isJoinEvent(eventId);
    }

    @GetMapping("/joinEvent")
    public RestBean<Void> joinEvent(Integer eventId) {
        return eventService.joinEvent(eventId);
    }

    @GetMapping("/outEvent")
    public RestBean<Void> outEvent(Integer eventId) {
        return eventService.outEvent(eventId);
    }

    @GetMapping("/listPlayerByEventId")
    public RestBean<List<Account>> listPlayerByEventId(Integer eventId) {
        return eventService.listPlayerByEventId(eventId);
    }

    @GetMapping("/listEventForPlayer")
    public RestBean<List<Event>> listEventForPlayer() {
        return eventService.listEventForPlayer();
    }
}
