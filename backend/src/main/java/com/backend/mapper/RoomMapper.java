package com.backend.mapper;

import com.backend.domain.entity.Room;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * (Room)表数据库访问层
 *
 * @author makejava
 * @since 2024-10-31 08:59:37
 */
@Mapper
public interface RoomMapper extends BaseMapper<Room> {

}
