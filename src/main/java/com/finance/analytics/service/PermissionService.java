package com.finance.analytics.service;

import com.finance.analytics.model.dto.PermissionRequestDTO;
import com.finance.analytics.model.vo.PermissionResponseVO;

import java.util.List;
import java.util.UUID;

public interface PermissionService {
    PermissionResponseVO createPermission(PermissionRequestDTO requestDTO);
    PermissionResponseVO updatePermission(UUID id, PermissionRequestDTO requestDTO);
    void deletePermission(UUID id);
    PermissionResponseVO getPermissionById(UUID id);
    List<PermissionResponseVO> getAllPermissions();
}
