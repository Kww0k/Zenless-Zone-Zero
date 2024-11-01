package com.backend.controller;

import com.backend.domain.RestBean;
import com.backend.domain.entity.Account;
import com.backend.domain.entity.Message;
import com.backend.domain.vo.ListVO;
import com.backend.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/message")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    @GetMapping("/listMessage/{roomId}")
    private RestBean<List<Message>> listMessage(@PathVariable Integer roomId) {
        return messageService.listMessage(roomId);
    }

    @GetMapping("/list")
    private RestBean<ListVO<Message>> list(Integer pageNum, Integer pageSize, String content) {
        return messageService.list(pageNum, pageSize, content);
    }


    @DeleteMapping("/delete")
    public RestBean<Void> deleteMessage(Integer id) {
        return messageService.deleteMessage(id);
    }

}
