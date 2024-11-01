package com.backend.mapper;

import com.backend.domain.entity.Message;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * (Message)表数据库访问层
 *
 * @author makejava
 * @since 2024-10-30 09:24:41
 */
@Mapper
public interface MessageMapper extends BaseMapper<Message> {

}
