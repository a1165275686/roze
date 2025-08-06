package com.allure.rap.roze.service.Impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.crypto.digest.BCrypt;
import com.allure.rap.roze.common.ErrorCode;
import com.allure.rap.roze.exception.ThrowUtils;
import com.allure.rap.roze.model.dto.user.*;
import com.allure.rap.roze.model.entity.User;
import com.allure.rap.roze.mapper.UserMapper;
import com.allure.rap.roze.service.IUserService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    private static final String ADMIN = "admin";
    @Resource
    private UserMapper userMapper;

    @Override
    public String createuser(UserCreateRequest userCreateRequest) {

        ThrowUtils.throwIf(userCreateRequest.getUserId() == null,
                ErrorCode.PARAMS_ERROR, "用户ID不能为空");
        ThrowUtils.throwIf(userCreateRequest.getPassword() == null,
                ErrorCode.PARAMS_ERROR, "密码不能为空");
        ThrowUtils.throwIf(!userCreateRequest.getPassword().equals(userCreateRequest.getCheckPassword()),
                ErrorCode.PARAMS_ERROR, "两次密码不一致");

        // 密码加密
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String rawPassword = userCreateRequest.getPassword();
        String encryptedPassword = encoder.encode(rawPassword);

        userCreateRequest.setPassword(encryptedPassword); // 覆盖原明文密码

        User newUser = new User();
        BeanUtil.copyProperties(userCreateRequest, newUser);
        newUser.setCreateTime(LocalDateTime.now());
        newUser.setCreateUser(ADMIN);
        newUser.setUpdateTime(LocalDateTime.now());
        newUser.setIsDelete(0);

        int insert = userMapper.insert(newUser);
        ThrowUtils.throwIf(insert <= 0, ErrorCode.OPERATION_ERROR, "创建用户失败");

        return newUser.getUserId();
    }

    @Override
    public Boolean deleteUser(UserDeleteRequest userDeleteRequest) {
        String userID = userDeleteRequest.getUserId();
        if (!StringUtils.hasText(userID)) {
            ThrowUtils.throwIf(true, ErrorCode.PARAMS_ERROR, "用户ID不能为空");
        }
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .eq(User::getUserId, userID);
        User deleteUser = new User();
        deleteUser.setUserId(userID);
        deleteUser.setIsDelete(1);
        return userMapper.update(deleteUser, queryWrapper) > 0;
    }

    @Override
    public Boolean updateUser(UserUpdateRequest userUpdateRequest) {
        String userID = userUpdateRequest.getUserId();
        if (!StringUtils.hasText(userID)) {
            ThrowUtils.throwIf(true, ErrorCode.PARAMS_ERROR, "用户ID不能为空");
        }
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .eq(User::getUserId, userID);
        User user = new User();
        BeanUtil.copyProperties(userUpdateRequest, user);
        return userMapper.update(user, queryWrapper) > 0;
    }

    @Override
    public Boolean updateUserPassword(PasswordUpdateRequest passwordUpdateRequest) {
        String userID = passwordUpdateRequest.getUserId();
        if (!StringUtils.hasText(userID)) {
            ThrowUtils.throwIf(true, ErrorCode.PARAMS_ERROR, "用户ID不能为空");
        }
        String password = passwordUpdateRequest.getNewPassword();
        if (!StringUtils.hasText(password)) {
            ThrowUtils.throwIf(true, ErrorCode.PARAMS_ERROR, "密码不能为空");
        }
        String checkPassword = passwordUpdateRequest.getCheckPassword();
        if (!StringUtils.hasText(checkPassword)) {
            ThrowUtils.throwIf(true, ErrorCode.PARAMS_ERROR, "确认密码不能为空");
        }
        if (!password.equals(checkPassword)) {
            ThrowUtils.throwIf(true, ErrorCode.PARAMS_ERROR, "两次密码不一致");
        }
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .eq(User::getUserId, userID);
        User user = userMapper.selectOne(queryWrapper);
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        if (encoder.matches(password, user.getPassword())) {
            ThrowUtils.throwIf(true, ErrorCode.PARAMS_ERROR, "新密码与原密码一致");
        }
        String encryptedPassword = encoder.encode(password);
        user.setPassword(encryptedPassword);
        return userMapper.update(user, queryWrapper) > 0;
    }

    @Override
    public IPage<User> getUserList(Integer pageNum, Integer pageSize) {

        IPage<User> page = new Page<>(pageNum, pageSize);
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .eq(User::getIsDelete, 0);
        IPage<User> userPage = userMapper.selectPage(page, queryWrapper);
        return userPage;
    }

    @Override
    public Boolean userLogin(UserLoginRequest userLoginRequest){
        String userID = userLoginRequest.getUserId();
        if (!StringUtils.hasText(userID)) {
            ThrowUtils.throwIf(true,ErrorCode.NOT_FOUND_ERROR,"用户为空");
        }
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .eq(User::getUserId, userID);
        User user = userMapper.selectOne(queryWrapper);
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String rawPassword = user.getPassword();
        String encryptedPassword = encoder.encode(rawPassword);
        String checkPassword = encoder.encode(userLoginRequest.getPassword());
        if(!encryptedPassword.equals(checkPassword)) {
            ThrowUtils.throwIf(true,ErrorCode.PARAMS_ERROR,"密码不正确");
        }
        return true;
    }



}
