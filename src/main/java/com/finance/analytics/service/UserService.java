package com.finance.analytics.service;

import com.finance.analytics.model.dto.CreateUserDTO;
import com.finance.analytics.model.dto.LoginDTO;
import com.finance.analytics.model.vo.AuthResponseVO;
import com.finance.analytics.model.vo.SuccessResponseVO;
import com.finance.analytics.model.vo.UserResponseVO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface UserService {
    SuccessResponseVO<AuthResponseVO> login(LoginDTO loginDTO);
    SuccessResponseVO<UserResponseVO> createUser(CreateUserDTO createUserDTO);
    SuccessResponseVO<UserResponseVO> updateUser(UUID userId, CreateUserDTO createUserDTO);
    void deleteUser(UUID userId);
    SuccessResponseVO<UserResponseVO> getUserById(UUID userId);
    SuccessResponseVO<UserResponseVO> assignRoles(UUID userId, List<UUID> roleIds);
}
