package com.allure.rap.roze.model.dto.user;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserUpdateRequest {

    private String userId;
    private String username;
    private String icon;
}
