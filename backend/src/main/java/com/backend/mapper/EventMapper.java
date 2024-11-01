package com.backend.mapper;

import com.backend.domain.entity.Event;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * (Event)表数据库访问层
 *
 * @author makejava
 * @since 2024-09-06 10:21:48
 */
@Mapper
public interface EventMapper extends BaseMapper<Event> {

    @Select("""
            SELECT
                *,
                CASE
                    WHEN DATE(event.start_time) < DATE(event.end_time) THEN '全天'
                    WHEN TIME(event.start_time) >= '06:00:00' AND TIME(event.end_time) < '12:00:00' THEN 'morning'
                    WHEN TIME(event.start_time) >= '12:00:00' AND TIME(event.end_time) < '18:00:00' THEN 'afternoon'
                    WHEN TIME(event.start_time) >= '18:00:00' AND TIME(event.end_time) < '24:00:00' THEN 'evening'
                    WHEN TIME(event.start_time) >= '00:00:00' AND TIME(event.end_time) < '06:00:00' THEN 'night'
                    WHEN TIME(event.start_time) >= '06:00:00' AND TIME(event.end_time) < '18:00:00' THEN 'morning,afternoon'
                    WHEN TIME(event.start_time) >= '12:00:00' AND TIME(event.end_time) < '24:00:00' THEN 'afternoon,evening'
                    WHEN TIME(event.start_time) >= '06:00:00' AND TIME(event.end_time) < '24:00:00' THEN 'morning,afternoon,evening'
                    ELSE '全天'
                END AS time_period
            FROM
                event
            LEFT JOIN
                event.account_event ae ON event.id = ae.event_id
            WHERE
                ae.account_id = #{userId} AND
                CURDATE() BETWEEN DATE(event.start_time) AND DATE(event.end_time);
            """)
    List<Event> listEventByUserId(Integer userId);
}
