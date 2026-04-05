package com.finance.analytics.service;

import com.finance.analytics.model.dto.RoleRequestDTO;
import com.finance.analytics.model.vo.RoleResponseVO;

import java.util.List;
import java.util.UUID;

public interface RoleService {
    RoleResponseVO createRole(RoleRequestDTO roleRequestDTO);
    RoleResponseVO updateRole(UUID id, RoleRequestDTO roleRequestDTO);
    void deleteRole(UUID id);
    RoleResponseVO getRoleById(UUID id);
    List<RoleResponseVO> getAllRoles();
}
