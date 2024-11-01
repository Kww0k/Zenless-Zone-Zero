package com.backend.service;

import com.backend.domain.entity.Files;
import com.baomidou.mybatisplus.extension.service.IService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.multipart.MultipartFile;


/**
 * (Files)表服务接口
 *
 * @author makejava
 * @since 2024-08-16 09:44:25
 */
public interface FilesService extends IService<Files> {

    String upload(MultipartFile file);

    void download(String uuid, HttpServletResponse response);
}
