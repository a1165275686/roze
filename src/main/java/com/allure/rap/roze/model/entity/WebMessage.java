package com.allure.rap.roze.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

@Data
public class WebMessage {

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    private String content;

    private String memoryId;

}
