package com.backend.service;

import com.backend.domain.RestBean;
import com.backend.domain.entity.Message;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;


/**
 * (Message)表服务接口
 *
 * @author makejava
 * @since 2024-10-30 09:24:42
 */
public interface MessageService extends IService<Message> {

    RestBean<List<Message>> listMessage(Integer roomId);

}
