package com.finance.analytics.service.impl;

import com.finance.analytics.entity.RoleEntity;
import com.finance.analytics.entity.UserEntity;
import com.finance.analytics.entity.UserRolesEntity;
import com.finance.analytics.exception.DuplicateResourceException;
import com.finance.analytics.exception.ResourceNotFoundException;
import com.finance.analytics.model.dto.CreateUserDTO;
import com.finance.analytics.config.JwtUtils;
import com.finance.analytics.model.dto.LoginDTO;
import com.finance.analytics.model.vo.AuthResponseVO;
import com.finance.analytics.model.vo.RoleResponseVO;
import com.finance.analytics.model.vo.SuccessResponseVO;
import com.finance.analytics.model.vo.UserResponseVO;
import com.finance.analytics.repository.RoleRepository;
import com.finance.analytics.repository.UserRepository;
import com.finance.analytics.repository.UserRoleRepository;
import com.finance.analytics.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final UserDetailsService userDetailsService;

    @Override
    public SuccessResponseVO<AuthResponseVO> login(LoginDTO loginDTO) {
        log.info("Attempting login for user: {}", loginDTO.getEmail());
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDTO.getEmail(), loginDTO.getPassword())
        );

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        UserEntity userEntity = userRepository.findByEmail(loginDTO.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("userId", userEntity.getId());
        extraClaims.put("roles", userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .filter(a -> a.startsWith("ROLE_"))
                .collect(Collectors.toList()));
        extraClaims.put("permissions", userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .filter(a -> !a.startsWith("ROLE_"))
                .collect(Collectors.toList()));

        String token = jwtUtils.generateToken(userDetails, extraClaims);
        
        AuthResponseVO authResponseVO = new AuthResponseVO(token, mapToVO(userEntity));
        return SuccessResponseVO.of(200, "Login successful", authResponseVO);
    }

    @Override
    public SuccessResponseVO<UserResponseVO> createUser(CreateUserDTO createUserDTO) {
        log.info("Creating user with email: {}", createUserDTO.getEmail());
        validateUserUniqueness(createUserDTO.getEmail(), createUserDTO.getMobile(), null);
        
        UserEntity userEntity = mapToEntity(createUserDTO);
        userEntity.setPassword(passwordEncoder.encode(createUserDTO.getPassword()));
        userEntity.setIsActive(true);
        
        UserEntity savedUser = userRepository.save(userEntity);
        saveUserRoles(savedUser, createUserDTO.getRoles());
        
        return SuccessResponseVO.of(201, "User created successfully", mapToVO(savedUser));
    }

    @Override
    public SuccessResponseVO<UserResponseVO> updateUser(UUID userId, CreateUserDTO createUserDTO) {
        log.info("Updating user with id: {}", userId);
        UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        validateUserUniqueness(createUserDTO.getEmail(), createUserDTO.getMobile(), userId);
        
        userEntity.setFirstName(createUserDTO.getFirstName());
        userEntity.setLastName(createUserDTO.getLastName());
        userEntity.setEmail(createUserDTO.getEmail());
        userEntity.setMobile(createUserDTO.getMobile());
        
        if (createUserDTO.getPassword() != null && !createUserDTO.getPassword().isBlank()) {
            userEntity.setPassword(passwordEncoder.encode(createUserDTO.getPassword()));
        }
        
        UserEntity updatedUser = userRepository.save(userEntity);
        
        if (createUserDTO.getRoles() != null) {
            userRoleRepository.deleteByUser(updatedUser);
            saveUserRoles(updatedUser, createUserDTO.getRoles());
        }
        
        return SuccessResponseVO.of(200, "User updated successfully", mapToVO(updatedUser));
    }

    @Override
    public void deleteUser(UUID userId) {
        log.info("Deleting user with id: {}", userId);
        UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        userEntity.setIsActive(false);
        userRepository.save(userEntity);
    }

    @Override
    public SuccessResponseVO<UserResponseVO> getUserById(UUID userId) {
        UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return SuccessResponseVO.of(200, "User fetched successfully", mapToVO(userEntity));
    }

    @Override
    public SuccessResponseVO<UserResponseVO> assignRoles(UUID userId, List<UUID> roleIds) {
        UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        userRoleRepository.deleteByUser(userEntity);
        saveUserRoles(userEntity, roleIds);
        
        return SuccessResponseVO.of(200, "Roles assigned successfully", mapToVO(userEntity));
    }

    private void validateUserUniqueness(String email, String mobile, UUID userId) {
        userRepository.findByEmail(email).ifPresent(user -> {
            if (userId == null || !user.getId().equals(userId)) {
                throw new DuplicateResourceException("Email already exists");
            }
        });
        
        // Mobile check (simplified)
        if (userRepository.existsByMobile(mobile)) {
             UserEntity existing = userRepository.findAll().stream()
                     .filter(u -> u.getMobile().equals(mobile))
                     .findFirst().orElse(null);
             if (existing != null && (userId == null || !existing.getId().equals(userId))) {
                 throw new DuplicateResourceException("Mobile number already exists");
             }
        }
    }

    private UserEntity mapToEntity(CreateUserDTO dto) {
        UserEntity entity = new UserEntity();
        entity.setFirstName(dto.getFirstName());
        entity.setLastName(dto.getLastName());
        entity.setEmail(dto.getEmail());
        entity.setMobile(dto.getMobile());
        return entity;
    }

    private void saveUserRoles(UserEntity user, List<UUID> roleIds) {
        if (roleIds == null) return;
        for (UUID roleId : roleIds) {
            RoleEntity role = roleRepository.findById(roleId)
                    .orElseThrow(() -> new ResourceNotFoundException("Role not found with id: " + roleId));
            UserRolesEntity userRole = new UserRolesEntity();
            userRole.setUser(user);
            userRole.setRole(role);
            userRoleRepository.save(userRole);
        }
    }

    private UserResponseVO mapToVO(UserEntity entity) {
        List<UserRolesEntity> userRoles = userRoleRepository.findByUser(entity);
        List<RoleResponseVO> roles = userRoles.stream()
                .map(ur -> new RoleResponseVO(ur.getRole().getId(), ur.getRole().getRoleName()))
                .collect(Collectors.toList());
        
        return new UserResponseVO(
                entity.getId(),
                entity.getFirstName(),
                entity.getLastName(),
                entity.getMobile(),
                entity.getEmail(),
                entity.getIsActive(),
                roles
        );
    }
}
