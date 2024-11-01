package com.backend.controller;

import com.backend.service.FilesService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author Silvery
 * @since 2023/10/10 13:47
 */
@RestController
@RequestMapping("/file")
@RequiredArgsConstructor
public class FilesController {

    private final FilesService filesService;

    @PostMapping("/upload")
    public String upload(@RequestParam MultipartFile file) {
        return filesService.upload(file);
    }

    @GetMapping("/download/{uuid}")
    public void download(@PathVariable String uuid, HttpServletResponse response) {
        filesService.download(uuid, response);
    }

}