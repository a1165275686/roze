package com.allure.rap.roze.service;

import com.allure.rap.roze.model.dto.user.PasswordUpdateRequest;
import com.allure.rap.roze.model.dto.user.UserCreateRequest;
import com.allure.rap.roze.model.dto.user.UserDeleteRequest;
import com.allure.rap.roze.model.dto.user.UserUpdateRequest;
import com.allure.rap.roze.model.entity.User;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

public interface IUserService extends IService<User> {
    String createuser(UserCreateRequest userCreateRequest);

    Boolean deleteUser(UserDeleteRequest userDeleteRequest);

    Boolean updateUser(UserUpdateRequest userUpdateRequest);

    Boolean updateUserPassword(PasswordUpdateRequest passwordUpdateRequest);

    IPage<User> getUserList(Integer pageNum, Integer pageSize);

}
