package com.backend.controller;

import com.backend.domain.RestBean;
import com.backend.domain.entity.Commit;
import com.backend.domain.vo.ListVO;
import com.backend.service.CommitService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/commit")
@RequiredArgsConstructor
public class CommitController {

    private final CommitService commitService;

    @GetMapping("/list")
    public RestBean<ListVO<Commit>> listCommit(Integer pageNum, Integer pageSize, String title) {
        return commitService.listCommit(pageNum, pageSize, title);
    }


    @GetMapping("/getCommitsByEventId/{id}")
    public RestBean<List<Commit>> getCommitsByEventId(@PathVariable Integer id) {
        return commitService.getCommitsByEventId(id);
    }

    @PostMapping("/save")
    public RestBean<Commit> save(@RequestBody Commit commit) {
        return commitService.saveCommit(commit);
    }

    @DeleteMapping("/delete")
    public RestBean<Void> deleteCommit(Integer id) {
        return commitService.deleteCommit(id);
    }
}
