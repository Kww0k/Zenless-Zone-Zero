package com.backend.service.impl;

import com.backend.domain.RestBean;
import com.backend.domain.entity.Tag;
import com.backend.domain.vo.ListVO;
import com.backend.mapper.TagMapper;
import com.backend.service.TagService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service("tagServiceImpl")
public class TagServiceImpl extends ServiceImpl<TagMapper, Tag> implements TagService {
    @Override
    public RestBean<ListVO<Tag>> listTag(Integer pageNum, Integer pageSize, String name) {
        Page<Tag> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Tag> queryWrapper = new LambdaQueryWrapper<Tag>().like(StringUtils.hasText(name), Tag::getName, name);
        Page<Tag> coursePage = page(page, queryWrapper);
        return RestBean.success(new ListVO<>(coursePage.getTotal(), coursePage.getRecords()));
    }

    @Override
    @Transactional
    public RestBean<Void> saveTag(Tag tag) {
        saveOrUpdate(tag);
        return RestBean.success();
    }

    @Override
    public RestBean<Void> deleteTag(Integer id) {
        if (removeById(id))
            return RestBean.success();
        else
            return RestBean.failure(400, "出现异常");
    }
}
