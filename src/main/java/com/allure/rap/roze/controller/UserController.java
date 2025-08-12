package com.allure.rap.roze.controller;

import cn.hutool.core.bean.BeanUtil;
import com.allure.rap.roze.common.BaseResponse;
import com.allure.rap.roze.common.ErrorCode;
import com.allure.rap.roze.common.ResultUtils;
import com.allure.rap.roze.model.dto.user.*;
import com.allure.rap.roze.model.entity.User;
import com.allure.rap.roze.service.IUserService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequestMapping("/user")
public class UserController {

    @Resource
    private IUserService userService;

    @PostMapping("/register")
    public BaseResponse<String> userRegister(@RequestBody UserRegisterRequest userRegisterRequest) {

        if (BeanUtil.isEmpty(userRegisterRequest)) {
            return ResultUtils.error(ErrorCode.NOT_FOUND_ERROR);
        }
        String userId = userService.registerUser(userRegisterRequest);
        return ResultUtils.success(userId);
    }

    @PostMapping("/login")
    public BaseResponse<Boolean> userLogin(@RequestBody UserLoginRequest userLoginRequest) {
        if (BeanUtil.isEmpty(userLoginRequest)) {
            return ResultUtils.error(ErrorCode.NOT_FOUND_ERROR);
        }
        Boolean b = userService.userLogin(userLoginRequest);
        return ResultUtils.success(b);
    }


    @PostMapping("/update")
    public BaseResponse<Boolean> updateUser(@RequestBody UserUpdateRequest userUpdateRequest) {
        if (BeanUtil.isEmpty(userUpdateRequest)) {
            return ResultUtils.error(ErrorCode.NOT_FOUND_ERROR);
        }

        Boolean flag = userService.updateUser(userUpdateRequest);
        return ResultUtils.success(flag);
    }

    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteUser(@RequestBody UserDeleteRequest userDeleteRequest) {
        if (BeanUtil.isEmpty(userDeleteRequest)) {
            return ResultUtils.error(ErrorCode.NOT_FOUND_ERROR);
        }

        Boolean flag = userService.deleteUser(userDeleteRequest);
        return ResultUtils.success(flag);
    }

    @PostMapping("/password")
    public BaseResponse<Boolean> updateUserPassword(@RequestBody PasswordUpdateRequest passwordUpdateRequest) {
        if (BeanUtil.isEmpty(passwordUpdateRequest)) {
            return ResultUtils.error(ErrorCode.NOT_FOUND_ERROR);
        }

        Boolean flag = userService.updateUserPassword(passwordUpdateRequest);
        return ResultUtils.success(flag);
    }

    @PostMapping("/list")
    public BaseResponse<IPage<User>> getUserList(
            @RequestParam Integer pageNum,
            @RequestParam Integer pageSize
    ) {
        IPage<User> userList = userService.getUserList(pageNum, pageSize);
        return ResultUtils.success(userList);
    }


}
