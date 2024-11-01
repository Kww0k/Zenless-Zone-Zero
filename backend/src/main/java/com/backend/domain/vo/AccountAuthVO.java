package com.backend.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.Date;

@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class AccountAuthVO {
    private Integer id;

    private String username;
    /**
     * 头像地址
     */
    private String avatarUrl;

    private String nickname;


    private Date createTime;
}