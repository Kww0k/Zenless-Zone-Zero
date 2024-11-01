package com.backend.domain.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * (Files)表实体类
 *
 * @author makejava
 * @since 2023-05-03 00:36:12
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("files")
public class Files {
    @TableId
    private Integer id;
    //文件名称
    private String name;
    //文件类型
    private String type;
    //文件大小
    private Long size;
    //链接
    private String url;
}