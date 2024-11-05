package com.backend.service;

import com.backend.domain.RestBean;
import com.backend.domain.entity.Room;
import com.backend.domain.vo.RoomVO;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;


/**
 * (Room)表服务接口
 *
 * @author makejava
 * @since 2024-10-31 08:59:39
 */
public interface RoomService extends IService<Room> {

    RestBean<List<RoomVO>> getRoomList();

    RestBean<RoomVO> getRoomInfoById(Integer id);
}
