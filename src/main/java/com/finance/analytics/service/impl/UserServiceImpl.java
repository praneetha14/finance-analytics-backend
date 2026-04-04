package com.finance.analytics.service.impl;

import com.finance.analytics.entity.RoleEntity;
import com.finance.analytics.entity.UserEntity;
import com.finance.analytics.entity.UserRolesEntity;
import com.finance.analytics.exception.DuplicateResourceException;
import com.finance.analytics.exception.ResourceNotFoundException;
import com.finance.analytics.model.dto.CreateUserDTO;
import com.finance.analytics.model.vo.SuccessResponseVO;
import com.finance.analytics.model.vo.UserResponseVO;
import com.finance.analytics.repository.RoleRepository;
import com.finance.analytics.repository.UserRepository;
import com.finance.analytics.repository.UserRoleRepository;
import com.finance.analytics.service.UserService;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;

    @Override
    public SuccessResponseVO<UserResponseVO> createUser(CreateUserDTO createUserDTO) {
        validateUserDTO(createUserDTO);
        UserEntity userEntity = mapToEntity(createUserDTO);
        userEntity = userRepository.save(userEntity);
        assignRoles(userEntity, createUserDTO.getRoles());
        UserResponseVO userResponseVO = new UserResponseVO(
                userEntity.getId(),
                userEntity.getFirstName(),
                userEntity.getLastName(),
                userEntity.getMobile(),
                userEntity.getEmail()
        );
        return SuccessResponseVO.of(201, "User created successfully", userResponseVO);
    }

    @Override
    public SuccessResponseVO<UserResponseVO> updateUser(UUID userId, CreateUserDTO createUserDTO) {
        UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        validateUserDTO(createUserDTO);
        userEntity.setFirstName(createUserDTO.getFirstName());
        userEntity.setLastName(createUserDTO.getLastName());
        userEntity.setMobile(createUserDTO.getMobile());
        userEntity.setEmail(createUserDTO.getEmail());
        userEntity.setPassword(createUserDTO.getPassword());
        userEntity = userRepository.save(userEntity);
        userRoleRepository.deleteByUserId(userEntity);
        assignRoles(userEntity, createUserDTO.getRoles());
        UserResponseVO userResponseVO = new UserResponseVO(
                userEntity.getId(),
                userEntity.getFirstName(),
                userEntity.getLastName(),
                userEntity.getMobile(),
                userEntity.getEmail()
        );
        return SuccessResponseVO.of(200, "User updated successfully", userResponseVO);
    }

    @Override
    public void deleteUser(UUID userId) {
        UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        if(!Boolean.TRUE.equals(userEntity.getIsActive())){
            throw new ResourceNotFoundException("User is not active");
        }
        userEntity.setIsActive(false);
        userRepository.save(userEntity);
    }

    private void validateUserDTO(CreateUserDTO createUserDTO) {
        if(userRepository.existsByEmail(createUserDTO.getEmail())) {
            throw new DuplicateResourceException("Email already exists");
        }
        if(userRepository.existsByMobile(createUserDTO.getMobile())) {
            throw new DuplicateResourceException("Mobile already exists");
        }
    }

    private UserEntity mapToEntity(CreateUserDTO createUserDTO) {
        UserEntity userEntity = new UserEntity();
        userEntity.setFirstName(createUserDTO.getFirstName());
        userEntity.setLastName(createUserDTO.getLastName());
        userEntity.setMobile(createUserDTO.getMobile());
        userEntity.setEmail(createUserDTO.getEmail());
        userEntity.setPassword(createUserDTO.getPassword());
        return userEntity;
    }

    private void assignRoles(UserEntity userEntity, List<UUID> roleIds) {
        if(roleIds == null || roleIds.isEmpty()) {
            return;
        }
        for(UUID roleId : roleIds) {
            RoleEntity roleEntity = roleRepository.findById(roleId)
                    .orElseThrow(() -> new ResourceNotFoundException("Role not found"));
            UserRolesEntity userRolesEntity = new UserRolesEntity();
            userRolesEntity.setRole(roleEntity);
            userRolesEntity.setUser(userEntity);
            userRoleRepository.save(userRolesEntity);
        }
    }
}
