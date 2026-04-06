package com.finance.analytics.rest.v1;

import com.finance.analytics.BaseControllerTest;
import com.finance.analytics.model.dto.RoleRequestDTO;
import com.finance.analytics.model.enums.RoleEnum;
import com.finance.analytics.model.vo.RoleResponseVO;
import com.finance.analytics.service.RoleService;
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

@WebMvcTest(controllers = RoleController.class)
@ExtendWith(SpringExtension.class)
public class RoleControllerTest extends BaseControllerTest {

    private static final String BASE_URL = "/api/v1/roles";

    @MockBean
    private RoleService roleService;

    @Test
    @WithMockUser(authorities = "ROLE_WRITE")
    void createRoleSuccessTest() throws Exception {
        RoleRequestDTO dto = new RoleRequestDTO();
        dto.setRoleName(RoleEnum.ANALYST);

        RoleResponseVO responseVO = new RoleResponseVO(UUID.randomUUID(), RoleEnum.ANALYST);
        when(roleService.createRole(any(RoleRequestDTO.class))).thenReturn(responseVO);

        mockMvc.perform(post(BASE_URL + "/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto))
                .with(csrf()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("Role created successfully"));
    }

    @Test
    @WithMockUser(authorities = "ROLE_READ")
    void getRoleByIdSuccessTest() throws Exception {
        UUID roleId = UUID.randomUUID();
        RoleResponseVO responseVO = new RoleResponseVO(roleId, RoleEnum.VIEWER);
        when(roleService.getRoleById(roleId)).thenReturn(responseVO);

        mockMvc.perform(get(BASE_URL + "/getRole/" + roleId)
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Role fetched successfully"));
    }
}
