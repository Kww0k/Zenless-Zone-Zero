package com.backend.domain;

import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.JSONWriter;
import com.backend.enums.HttpMessage;

public record RestBean<T>(Integer code, String message, T data) {

    public static <T> RestBean<T> success(T data) {
        return new RestBean<>(HttpMessage.SUCCESS.getCode(), HttpMessage.SUCCESS.getMessage(), data);
    }

    public static <T> RestBean<T> success(Integer code, T data) {
        return new RestBean<>(code, HttpMessage.SUCCESS.getMessage(), data);
    }


    public static <T> RestBean<T> success() {
        return new RestBean<>(HttpMessage.SUCCESS.getCode(), HttpMessage.SUCCESS.getMessage(), null);
    }

    public static <T> RestBean<T> failure(Integer code, String message) {
        return new RestBean<>(code, message, null);
    }

    public static <T> RestBean<T> failure(HttpMessage httpMessage) {
        return new RestBean<>(httpMessage.getCode(), httpMessage.getMessage(), null);
    }

    public String toJsonString() {
        return JSONObject.toJSONString(this, JSONWriter.Feature.WriteNulls);
    }
}