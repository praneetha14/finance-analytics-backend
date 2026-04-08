package com.finance.analytics.service.impl;

import com.finance.analytics.AbstractTest;
import com.finance.analytics.entity.RoleEntity;
import com.finance.analytics.entity.UserEntity;
import com.finance.analytics.exception.DuplicateResourceException;
import com.finance.analytics.exception.ResourceNotFoundException;
import com.finance.analytics.model.dto.CreateUserDTO;
import com.finance.analytics.model.dto.LoginDTO;
import com.finance.analytics.model.enums.RoleEnum;
import com.finance.analytics.model.vo.AuthResponseVO;
import com.finance.analytics.model.vo.SuccessResponseVO;
import com.finance.analytics.model.vo.UserResponseVO;
import com.finance.analytics.repository.RoleRepository;
import com.finance.analytics.repository.UserRepository;
import com.finance.analytics.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class UserServiceTest extends AbstractTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private RoleEntity adminRole;
    private RoleEntity viewerRole;

    @BeforeEach
    void setUp() {
        adminRole = roleRepository.findByRoleName(RoleEnum.ADMIN)
                .orElseGet(() -> {
                    RoleEntity role = new RoleEntity();
                    role.setRoleName(RoleEnum.ADMIN);
                    return roleRepository.save(role);
                });

        viewerRole = roleRepository.findByRoleName(RoleEnum.VIEWER)
                .orElseGet(() -> {
                    RoleEntity role = new RoleEntity();
                    role.setRoleName(RoleEnum.VIEWER);
                    return roleRepository.save(role);
                });
    }

    @Test
    void createUserSuccessTest() {
        CreateUserDTO dto = new CreateUserDTO();
        dto.setFirstName("John");
        dto.setLastName("Doe");
        dto.setEmail("john" + UUID.randomUUID() + "@example.com");
        dto.setMobile("9876593210");
        dto.setPassword("password123");
        dto.setRoles(List.of(viewerRole.getId()));

        SuccessResponseVO<UserResponseVO> response = userService.createUser(dto);

        assertNotNull(response, ASSERTION_ERROR_MESSAGE);
        assertEquals(201, response.getCode(), ASSERTION_ERROR_MESSAGE);
        assertEquals("John", response.getData().firstName(), ASSERTION_ERROR_MESSAGE);
        assertNotNull(response.getData().id(), ASSERTION_ERROR_MESSAGE);
    }

    @Test
    void createUserDuplicateEmailFailureTest() {
        String email = "duplicate@example.com";
        CreateUserDTO dto = new CreateUserDTO();
        dto.setFirstName("First");
        dto.setLastName("Last");
        dto.setEmail(email);
        dto.setMobile("1111111111");
        dto.setPassword("pass");
        userService.createUser(dto);

        CreateUserDTO dto2 = new CreateUserDTO();
        dto2.setFirstName("Second");
        dto2.setLastName("Last");
        dto2.setEmail(email);
        dto2.setMobile("2222222222");
        dto2.setPassword("pass");

        assertThrows(DuplicateResourceException.class, () -> userService.createUser(dto2));
    }

    @Test
    void getUserByIdSuccessTest() {
        CreateUserDTO dto = new CreateUserDTO();
        dto.setFirstName("Jane");
        dto.setLastName("Doe");
        dto.setEmail("jane" + UUID.randomUUID() + "@example.com");
        dto.setMobile("8888888888");
        dto.setPassword("pass");
        SuccessResponseVO<UserResponseVO> created = userService.createUser(dto);

        SuccessResponseVO<UserResponseVO> response = userService.getUserById(created.getData().id());

        assertNotNull(response, ASSERTION_ERROR_MESSAGE);
        assertEquals(200, response.getCode(), ASSERTION_ERROR_MESSAGE);
        assertEquals("Jane", response.getData().firstName(), ASSERTION_ERROR_MESSAGE);
    }

    @Test
    void loginSuccessTest() {
        String email = "login" + UUID.randomUUID() + "@example.com";
        String password = "password123";
        CreateUserDTO dto = new CreateUserDTO();
        dto.setFirstName("Login");
        dto.setLastName("User");
        dto.setEmail(email);
        dto.setMobile("7777777777");
        dto.setPassword(password);
        userService.createUser(dto);

        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setEmail(email);
        loginDTO.setPassword(password);

        SuccessResponseVO<AuthResponseVO> response = userService.login(loginDTO);

        assertNotNull(response, ASSERTION_ERROR_MESSAGE);
        assertEquals(200, response.getCode(), ASSERTION_ERROR_MESSAGE);
        assertNotNull(response.getData().getToken(), ASSERTION_ERROR_MESSAGE);
    }

    @Test
    void deleteUserSuccessTest() {
        CreateUserDTO dto = new CreateUserDTO();
        dto.setFirstName("Delete");
        dto.setLastName("Me");
        dto.setEmail("delete" + UUID.randomUUID() + "@example.com");
        dto.setMobile("6666666666");
        dto.setPassword("pass");
        SuccessResponseVO<UserResponseVO> created = userService.createUser(dto);

        userService.deleteUser(created.getData().id());

        UserEntity user = userRepository.findById(created.getData().id()).orElseThrow();
        assertFalse(user.getIsActive(), ASSERTION_ERROR_MESSAGE);
    }
}
