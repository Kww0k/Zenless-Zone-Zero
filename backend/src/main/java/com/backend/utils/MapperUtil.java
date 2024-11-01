package com.backend.utils;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class MapperUtil {

    public <K, V> Map<K, V> mapToIdMap(BaseMapper<V> mapper, Set<K> idSet, SFunction<V, K> idFunction) {
        List<V> entityList = mapper.selectList(new LambdaQueryWrapper<V>().in(idFunction, idSet));
        return entityList.stream().collect(Collectors.toMap(idFunction, entity -> entity));
    }


}
