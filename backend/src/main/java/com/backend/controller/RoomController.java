package com.backend.controller;

import com.backend.domain.RestBean;
import com.backend.domain.entity.Room;
import com.backend.domain.vo.RoomVO;
import com.backend.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/room")
@RequiredArgsConstructor
public class RoomController {

    private final RoomService roomService;

    @GetMapping("/getRoomById")
    public RestBean<Room> getRoomById(@RequestParam int id) {
        return RestBean.success(roomService.getById(id));
    }

    @GetMapping("/getRoomList")
    public RestBean<List<RoomVO>> getRoomList() {
        return roomService.getRoomList();
    }

    @GetMapping("/getRoomInfoById/{id}")
    public RestBean<RoomVO> getRoomInfoById(@PathVariable Integer id) {
        return roomService.getRoomInfoById(id);
    }

    @PostMapping("/save")
    public RestBean<Room> save(@RequestBody Room room) {
        return roomService.saveRoom(room);
    }
}
