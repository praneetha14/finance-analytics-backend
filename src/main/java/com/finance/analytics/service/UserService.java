package com.finance.analytics.service;

import com.finance.analytics.model.dto.CreateUserDTO;
import com.finance.analytics.model.vo.SuccessResponseVO;
import com.finance.analytics.model.vo.UserResponseVO;

import java.util.UUID;

public interface UserService {
    SuccessResponseVO<UserResponseVO> createUser(CreateUserDTO createUserDTO);
    SuccessResponseVO<UserResponseVO> updateUser(UUID userId, CreateUserDTO createUserDTO);
    void deleteUser(UUID userId);
}
