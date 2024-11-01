package com.backend.config;

import com.backend.utils.SecurityUtil;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.RequiredArgsConstructor;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@RequiredArgsConstructor
public class MyMetaObjectHandler implements MetaObjectHandler {

    private final SecurityUtil securityUtil;

    @Override
    public void insertFill(MetaObject metaObject) {

        this.setFieldValByName("createTime", new Date(), metaObject);
        this.setFieldValByName("createBy", getUserId() , metaObject);
        this.setFieldValByName("updateTime", new Date(), metaObject);
        this.setFieldValByName("updateBy", getUserId(), metaObject);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        this.setFieldValByName("updateTime", new Date(), metaObject);
        this.setFieldValByName("updateBy", getUserId(), metaObject);
    }

    private Integer getUserId() {
        Integer userId;
        try {
            // 这里会有一个用户不存在异常,需要捕获
            userId = securityUtil.getUserId();
        } catch (Exception e) {
            // 这是插入或者更新时自动插入，所以不用返回和处理这个异常，如果有这个异常，则将id设置为-1
            userId = -1;
        }
        return userId;
    }

}