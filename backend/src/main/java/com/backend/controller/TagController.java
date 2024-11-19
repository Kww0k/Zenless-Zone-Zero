package com.backend.controller;

import com.backend.domain.RestBean;
import com.backend.domain.entity.Tag;
import com.backend.domain.vo.ListVO;
import com.backend.service.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tag")
@RequiredArgsConstructor
public class TagController {

    private final TagService tagService;

    @GetMapping("/listTag")
    public RestBean<List<Tag>> listTag() {
        return RestBean.success(tagService.list());
    }

    @GetMapping("/list")
    public RestBean<ListVO<Tag>> listTag(Integer pageNum, Integer pageSize, String name) {
        return tagService.listTag(pageNum, pageSize, name);
    }

    @PostMapping("/save")
    public RestBean<Void> saveTag(@RequestBody Tag tag) {
        return tagService.saveTag(tag);
    }

    @DeleteMapping("/delete")
    public RestBean<Void> deleteTag(Integer id) {
        return tagService.deleteTag(id);
    }
}
