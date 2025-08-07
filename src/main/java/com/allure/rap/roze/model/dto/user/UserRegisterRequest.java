package com.allure.rap.roze.model.dto.user;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRegisterRequest {

    private String userId;

    private String password;

    private String checkPassword;

    private String phoneNumber;

    private String email;

}
