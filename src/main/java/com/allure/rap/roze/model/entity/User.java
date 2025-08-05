package com.allure.rap.roze.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.Value;

import java.time.LocalDateTime;

@TableName("user")
@Data
public class User {

    @TableId(value = "id",type = IdType.ASSIGN_ID)
    private Long id;

    private String userId;

    private String password;

    private String username;

    private String icon;

    private String createUser;

    private String updateUser;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    // 逻辑删除 0:未删除 1:删除
    private Integer isDelete = 0;
}
