package com.backend.domain.entity;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
/**
 * (AccountEvent)表实体类
 *
 * @author makejava
 * @since 2024-09-19 16:26:07
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@TableName("account_event")
public class AccountEvent  {

    private Integer accountId;
    private Integer eventId;



}
