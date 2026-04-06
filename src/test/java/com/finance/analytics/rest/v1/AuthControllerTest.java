package com.finance.analytics.rest.v1;

import com.finance.analytics.BaseControllerTest;
import com.finance.analytics.model.dto.LoginDTO;
import com.finance.analytics.model.vo.AuthResponseVO;
import com.finance.analytics.model.vo.SuccessResponseVO;
import com.finance.analytics.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AuthController.class)
@ExtendWith(SpringExtension.class)
public class AuthControllerTest extends BaseControllerTest {

    private static final String LOGIN_URL = "/api/v1/auth/login";

    @MockBean
    private UserService userService;

    @Test
    void loginSuccessTest() throws Exception {
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setEmail("admin@test.com");
        loginDTO.setPassword("password");

        AuthResponseVO authResponseVO = new AuthResponseVO("jwt-token", null);
        SuccessResponseVO<AuthResponseVO> response = SuccessResponseVO.of(200, "Login successful", authResponseVO);

        when(userService.login(any(LoginDTO.class))).thenReturn(response);

        mockMvc.perform(post(LOGIN_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginDTO))
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Login successful"))
                .andExpect(jsonPath("$.data.token").value("jwt-token"));
    }

    @Test
    void loginFailureTest() throws Exception {
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setEmail("admin@test.com");
        loginDTO.setPassword("wrong-password");

        when(userService.login(any(LoginDTO.class))).thenThrow(new RuntimeException("Invalid credentials"));

        mockMvc.perform(post(LOGIN_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginDTO))
                .with(csrf()))
                .andExpect(status().isInternalServerError());
    }
}
