package com.backend.mapper;

import com.backend.domain.entity.Files;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * (Files)表数据库访问层
 *
 * @author makejava
 * @since 2024-08-16 09:44:23
 */
@Mapper
public interface FilesMapper extends BaseMapper<Files> {

}
