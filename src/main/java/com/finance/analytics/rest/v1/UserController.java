package com.finance.analytics.rest.v1;

import com.finance.analytics.model.dto.CreateUserDTO;
import com.finance.analytics.model.vo.SuccessResponseVO;
import com.finance.analytics.model.vo.UserResponseVO;
import com.finance.analytics.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/create")
    @PreAuthorize("hasAuthority('USER_WRITE')")
    public ResponseEntity<SuccessResponseVO<UserResponseVO>> createUser(@Valid @RequestBody CreateUserDTO createUserDTO) {
        return new ResponseEntity<>(userService.createUser(createUserDTO), HttpStatus.CREATED);
    }

    @PutMapping("/update/{userId}")
    @PreAuthorize("hasAuthority('USER_WRITE')")
    public ResponseEntity<SuccessResponseVO<UserResponseVO>> updateUser(@PathVariable UUID userId,
                                                                        @Valid @RequestBody CreateUserDTO createUserDTO) {
        return ResponseEntity.ok(userService.updateUser(userId, createUserDTO));
    }

    @DeleteMapping("/delete/{userId}")
    @PreAuthorize("hasAuthority('USER_DELETE')")
    public ResponseEntity<SuccessResponseVO<Void>> deleteUser(@PathVariable UUID userId) {
        userService.deleteUser(userId);
        return ResponseEntity.ok(SuccessResponseVO.of(200, "User deleted successfully", null));
    }

    @GetMapping("/{userId}")
    @PreAuthorize("hasAuthority('USER_READ')")
    public ResponseEntity<SuccessResponseVO<UserResponseVO>> getUserById(@PathVariable UUID userId) {
        return ResponseEntity.ok(userService.getUserById(userId));
    }

    @PostMapping("/{userId}/assign-roles")
    @PreAuthorize("hasAuthority('USER_WRITE')")
    public ResponseEntity<SuccessResponseVO<UserResponseVO>> assignRoles(@PathVariable UUID userId, @RequestBody List<UUID> roleIds) {
        return ResponseEntity.ok(userService.assignRoles(userId, roleIds));
    }
}
