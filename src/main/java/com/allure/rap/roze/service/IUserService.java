package com.allure.rap.roze.service;

import com.allure.rap.roze.model.dto.user.*;
import com.allure.rap.roze.model.entity.User;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

public interface IUserService extends IService<User> {

    String registerUser(UserRegisterRequest userRegisterRequest);

    Boolean deleteUser(UserDeleteRequest userDeleteRequest);

    Boolean updateUser(UserUpdateRequest userUpdateRequest);

    Boolean updateUserPassword(PasswordUpdateRequest passwordUpdateRequest);

    IPage<User> getUserList(Integer pageNum, Integer pageSize);

    Boolean userLogin(UserLoginRequest userLoginRequest);

}
