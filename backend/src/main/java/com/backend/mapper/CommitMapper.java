package com.backend.mapper;

import com.backend.domain.entity.Commit;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * (Commit)表数据库访问层
 *
 * @author makejava
 * @since 2024-09-19 15:02:56
 */
@Mapper
public interface CommitMapper extends BaseMapper<Commit> {

}
