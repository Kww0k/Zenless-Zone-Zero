package com.backend.service;

import com.backend.domain.RestBean;
import com.backend.domain.entity.Tag;
import com.backend.domain.vo.ListVO;
import com.baomidou.mybatisplus.extension.service.IService;

public interface TagService extends IService<Tag> {
    RestBean<ListVO<Tag>> listTag(Integer pageNum, Integer pageSize, String name);

    RestBean<Void> saveTag(Tag tag);

    RestBean<Void> deleteTag(Integer id);
}
