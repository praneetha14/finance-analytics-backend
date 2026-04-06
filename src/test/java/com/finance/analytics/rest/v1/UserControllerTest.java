package com.finance.analytics.rest.v1;

import com.finance.analytics.BaseControllerTest;
import com.finance.analytics.model.dto.CreateUserDTO;
import com.finance.analytics.model.vo.SuccessResponseVO;
import com.finance.analytics.model.vo.UserResponseVO;
import com.finance.analytics.service.UserService;
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

@WebMvcTest(controllers = UserController.class)
@ExtendWith(SpringExtension.class)
public class UserControllerTest extends BaseControllerTest {

    private static final String BASE_URL = "/api/v1/users";

    @MockBean
    private UserService userService;

    @Test
    @WithMockUser(authorities = "USER_WRITE")
    void createUserSuccessTest() throws Exception {
        CreateUserDTO dto = new CreateUserDTO();
        dto.setFirstName("John");
        dto.setLastName("Doe");
        dto.setMobile("9876543210");
        dto.setEmail("john@gmail.com");
        dto.setPassword("Password@123");

        UserResponseVO responseVO = new UserResponseVO(UUID.randomUUID(), "John", "Doe", "9876543210", "john@example.com", true, null);
        when(userService.createUser(any(CreateUserDTO.class))).thenReturn(SuccessResponseVO.of(201, "User created successfully", responseVO));

        mockMvc.perform(post(BASE_URL + "/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto))
                .with(csrf()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("User created successfully"));
    }

    @Test
    @WithMockUser(authorities = "USER_READ")
    void getUserByIdSuccessTest() throws Exception {
        UUID userId = UUID.randomUUID();
        UserResponseVO responseVO = new UserResponseVO(userId, "John", "Doe", "1234567890", "john@example.com", true, null);
        when(userService.getUserById(userId)).thenReturn(SuccessResponseVO.of(200, "User fetched successfully", responseVO));

        mockMvc.perform(get(BASE_URL + "/" + userId)
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User fetched successfully"));
    }

    @Test
    @WithMockUser(authorities = "USER_DELETE")
    void deleteUserSuccessTest() throws Exception {
        UUID userId = UUID.randomUUID();

        mockMvc.perform(delete(BASE_URL + "/delete/" + userId)
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User deleted successfully"));
    }
}
