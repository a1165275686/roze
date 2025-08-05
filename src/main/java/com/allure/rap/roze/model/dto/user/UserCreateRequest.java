package com.allure.rap.roze.model.dto.user;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserCreateRequest {

    private String userId;

    private String password;

    private String checkPassword;

}
