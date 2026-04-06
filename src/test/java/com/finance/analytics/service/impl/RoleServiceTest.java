package com.finance.analytics.service.impl;

import com.finance.analytics.AbstractTest;
import com.finance.analytics.entity.PermissionsEntity;
import com.finance.analytics.exception.DuplicateResourceException;
import com.finance.analytics.exception.ResourceNotFoundException;
import com.finance.analytics.model.dto.RoleRequestDTO;
import com.finance.analytics.model.enums.RoleEnum;
import com.finance.analytics.model.vo.RoleResponseVO;
import com.finance.analytics.repository.PermissionRepository;
import com.finance.analytics.repository.RoleRepository;
import com.finance.analytics.service.RoleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class RoleServiceTest extends AbstractTest {

    @Autowired
    private RoleService roleService;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PermissionRepository permissionRepository;

    private PermissionsEntity testPermission;

    @BeforeEach
    void setUp() {
        testPermission = new PermissionsEntity();
        testPermission.setName("TEST_PERMISSION_" + UUID.randomUUID());
        testPermission = permissionRepository.save(testPermission);
    }

    @Test
    void createRoleSuccessTest() {
        RoleRequestDTO dto = new RoleRequestDTO();
        dto.setRoleName(RoleEnum.ANALYST);
        dto.setPermissionIds(List.of(testPermission.getId()));

        RoleResponseVO response = roleService.createRole(dto);

        assertNotNull(response, ASSERTION_ERROR_MESSAGE);
        assertEquals(RoleEnum.ANALYST, response.roleName(), ASSERTION_ERROR_MESSAGE);
    }

    @Test
    void createRoleDuplicateFailureTest() {
        RoleRequestDTO dto = new RoleRequestDTO();
        dto.setRoleName(RoleEnum.ADMIN);
        
        // Ensure ADMIN exists
        if (!roleRepository.existsByRoleName(RoleEnum.ADMIN)) {
            roleService.createRole(dto);
        }

        assertThrows(DuplicateResourceException.class, () -> roleService.createRole(dto));
    }

    @Test
    void getRoleByIdSuccessTest() {
        RoleRequestDTO dto = new RoleRequestDTO();
        dto.setRoleName(RoleEnum.VIEWER);
        RoleResponseVO created = roleService.createRole(dto);

        RoleResponseVO response = roleService.getRoleById(created.id());

        assertNotNull(response, ASSERTION_ERROR_MESSAGE);
        assertEquals(RoleEnum.VIEWER, response.roleName(), ASSERTION_ERROR_MESSAGE);
    }

    @Test
    void deleteRoleSuccessTest() {
        RoleRequestDTO dto = new RoleRequestDTO();
        dto.setRoleName(RoleEnum.ANALYST);
        RoleResponseVO created = roleService.createRole(dto);

        roleService.deleteRole(created.id());

        assertThrows(ResourceNotFoundException.class, () -> roleService.getRoleById(created.id()));
    }
}
