package com.finance.analytics.service.impl;

import com.finance.analytics.model.vo.UserResponseVO;
import com.finance.analytics.service.DashboardService;

import java.util.List;
import java.util.UUID;

public class DashboardServiceImpl implements DashboardService {
    @Override
    public List<UserResponseVO> getUsers() {
        return List.of();
    }

    @Override
    public UserResponseVO getUserById(UUID userId) {
        return null;
    }
}
