package com.backend.domain.entity;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
/**
 * (Message)表实体类
 *
 * @author makejava
 * @since 2024-10-30 09:24:41
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@TableName("message")
public class Message  {
    @TableId
    private Integer id;

    private String content;

    private Integer toUserId;

    private Integer roomId;

    // 0为已读
    private Integer readOne;

    // 0为已读
    private Integer readTwo;

    private Integer createBy;

    private Date createTime;

    @TableField(exist = false)
    private String fromUserName;
    @TableField(exist = false)
    private String toUserName;
}
