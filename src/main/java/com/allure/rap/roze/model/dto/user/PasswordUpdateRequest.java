package com.allure.rap.roze.model.dto.user;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PasswordUpdateRequest {

    private String userId;

    private String newPassword;

    private String checkPassword;

}
