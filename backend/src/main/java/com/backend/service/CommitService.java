package com.backend.service;

import com.backend.domain.RestBean;
import com.backend.domain.entity.Commit;
import com.backend.domain.vo.ListVO;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;


/**
 * (Commit)表服务接口
 *
 * @author makejava
 * @since 2024-09-19 15:02:58
 */
public interface CommitService extends IService<Commit> {

    RestBean<ListVO<Commit>> listCommit(Integer pageNum, Integer pageSize, String title);

    RestBean<List<Commit>> getCommitsByEventId(Integer id);

    RestBean<Commit> saveCommit(Commit commit);

    RestBean<Void> deleteCommit(Integer id);
}
