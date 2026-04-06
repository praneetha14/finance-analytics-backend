package com.finance.analytics.service.impl;

import com.finance.analytics.AbstractTest;
import com.finance.analytics.exception.DuplicateResourceException;
import com.finance.analytics.exception.ResourceNotFoundException;
import com.finance.analytics.model.dto.PermissionRequestDTO;
import com.finance.analytics.model.vo.PermissionResponseVO;
import com.finance.analytics.service.PermissionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class PermissionServiceTest extends AbstractTest {

    @Autowired
    private PermissionService permissionService;

    @Test
    void createPermissionSuccessTest() {
        PermissionRequestDTO dto = new PermissionRequestDTO("NEW_PERM_" + UUID.randomUUID());

        PermissionResponseVO response = permissionService.createPermission(dto);

        assertNotNull(response, ASSERTION_ERROR_MESSAGE);
        assertEquals(dto.getName(), response.name(), ASSERTION_ERROR_MESSAGE);
    }

    @Test
    void createPermissionDuplicateFailureTest() {
        String name = "DUPLICATE_PERM";
        PermissionRequestDTO dto = new PermissionRequestDTO(name);
        permissionService.createPermission(dto);

        assertThrows(DuplicateResourceException.class, () -> permissionService.createPermission(dto));
    }

    @Test
    void getPermissionByIdSuccessTest() {
        PermissionRequestDTO dto = new PermissionRequestDTO("GET_PERM_" + UUID.randomUUID());
        PermissionResponseVO created = permissionService.createPermission(dto);

        PermissionResponseVO response = permissionService.getPermissionById(created.id());

        assertNotNull(response, ASSERTION_ERROR_MESSAGE);
        assertEquals(dto.getName(), response.name(), ASSERTION_ERROR_MESSAGE);
    }

    @Test
    void deletePermissionSuccessTest() {
        PermissionRequestDTO dto = new PermissionRequestDTO("DELETE_PERM_" + UUID.randomUUID());
        PermissionResponseVO created = permissionService.createPermission(dto);

        permissionService.deletePermission(created.id());

        assertThrows(ResourceNotFoundException.class, () -> permissionService.getPermissionById(created.id()));
    }
}
