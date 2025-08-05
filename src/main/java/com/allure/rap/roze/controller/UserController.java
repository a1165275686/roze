package com.allure.rap.roze.controller;

import cn.hutool.core.bean.BeanUtil;
import com.allure.rap.roze.common.BaseResponse;
import com.allure.rap.roze.common.ErrorCode;
import com.allure.rap.roze.common.ResultUtils;
import com.allure.rap.roze.model.dto.user.PasswordUpdateRequest;
import com.allure.rap.roze.model.dto.user.UserCreateRequest;
import com.allure.rap.roze.model.dto.user.UserDeleteRequest;
import com.allure.rap.roze.model.dto.user.UserUpdateRequest;
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

    @PostMapping("/create")
    public BaseResponse<String> createUser(@RequestBody UserCreateRequest userCreateRequest) {
        if (BeanUtil.isEmpty(userCreateRequest)) {
            return ResultUtils.error(ErrorCode.NOT_FOUND_ERROR);
        }

        String userId = userService.createuser(userCreateRequest);
        return ResultUtils.success(userId);
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
