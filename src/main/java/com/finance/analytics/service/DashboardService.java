package com.finance.analytics.service;

import com.finance.analytics.entity.UserEntity;
import com.finance.analytics.model.vo.UserResponseVO;

import java.util.List;
import java.util.UUID;

public interface DashboardService {

    List<UserResponseVO> getUsers();
    UserResponseVO getUserById(UUID userId);
}
