package com.finance.analytics.rest.v1;

import com.finance.analytics.BaseControllerTest;
import com.finance.analytics.model.dto.PermissionRequestDTO;
import com.finance.analytics.model.vo.PermissionResponseVO;
import com.finance.analytics.service.PermissionService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = PermissionController.class)
@ExtendWith(SpringExtension.class)
public class PermissionControllerTest extends BaseControllerTest {

    private static final String BASE_URL = "/api/v1/permissions";

    @MockBean
    private PermissionService permissionService;

    @Test
    @WithMockUser(authorities = "PERMISSION_WRITE")
    void createPermissionSuccessTest() throws Exception {
        PermissionRequestDTO dto = new PermissionRequestDTO("NEW_PERM");

        PermissionResponseVO responseVO = new PermissionResponseVO(UUID.randomUUID(), "NEW_PERM");
        when(permissionService.createPermission(any(PermissionRequestDTO.class))).thenReturn(responseVO);

        mockMvc.perform(post(BASE_URL + "/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto))
                .with(csrf()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("Permission created successfully"));
    }

    @Test
    @WithMockUser(authorities = "PERMISSION_READ")
    void getPermissionByIdSuccessTest() throws Exception {
        UUID permId = UUID.randomUUID();
        PermissionResponseVO responseVO = new PermissionResponseVO(permId, "SOME_PERM");
        when(permissionService.getPermissionById(permId)).thenReturn(responseVO);

        mockMvc.perform(get(BASE_URL + "/get/" + permId)
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Permission fetched successfully"));
    }
}
