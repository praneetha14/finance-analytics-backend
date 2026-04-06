package com.finance.analytics.service.impl;

import com.finance.analytics.entity.PermissionsEntity;
import com.finance.analytics.entity.RoleEntity;
import com.finance.analytics.entity.RolePermissionsEntity;
import com.finance.analytics.exception.DuplicateResourceException;
import com.finance.analytics.exception.ResourceNotFoundException;
import com.finance.analytics.model.dto.RoleRequestDTO;
import com.finance.analytics.model.vo.RoleResponseVO;
import com.finance.analytics.repository.PermissionRepository;
import com.finance.analytics.repository.RolePermissionRepository;
import com.finance.analytics.repository.RoleRepository;
import com.finance.analytics.service.RoleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final RolePermissionRepository rolePermissionRepository;

    @Override
    public RoleResponseVO createRole(RoleRequestDTO roleRequestDTO) {
        log.info("Creating new role: {}", roleRequestDTO.getRoleName());

        if (roleRepository.existsByRoleName(roleRequestDTO.getRoleName())) {
            throw new DuplicateResourceException("Role already exists: " + roleRequestDTO.getRoleName());
        }

        RoleEntity roleEntity = new RoleEntity();
        roleEntity.setRoleName(roleRequestDTO.getRoleName());
        roleEntity.setCreatedAt(LocalDateTime.now());
        roleEntity.setUpdatedAt(LocalDateTime.now());

        RoleEntity savedRole = roleRepository.save(roleEntity);
        saveRolePermissions(savedRole, roleRequestDTO.getPermissionIds());

        return mapToVO(savedRole);
    }

    @Override
    @Transactional
    public RoleResponseVO updateRole(UUID id, RoleRequestDTO roleRequestDTO) {
        log.info("Updating role with id: {}", id);

        RoleEntity roleEntity = roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with id: " + id));

        if (!roleEntity.getRoleName().equals(roleRequestDTO.getRoleName()) &&
                roleRepository.existsByRoleName(roleRequestDTO.getRoleName())) {
            throw new DuplicateResourceException("Role already exists: " + roleRequestDTO.getRoleName());
        }

        roleEntity.setRoleName(roleRequestDTO.getRoleName());
        roleEntity.setUpdatedAt(LocalDateTime.now());

        RoleEntity updatedRole = roleRepository.save(roleEntity);

        rolePermissionRepository.deleteByRole(updatedRole);
        saveRolePermissions(updatedRole, roleRequestDTO.getPermissionIds());

        return mapToVO(updatedRole);
    }

    private void saveRolePermissions(RoleEntity role, List<UUID> permissionIds) {
        if (permissionIds == null || permissionIds.isEmpty()) return;

        for (UUID permissionId : permissionIds) {
            PermissionsEntity permission = permissionRepository.findById(permissionId)
                    .orElseThrow(() -> new ResourceNotFoundException("Permission not found with id: " + permissionId));

            RolePermissionsEntity rolePermissionsEntity = new RolePermissionsEntity();
            rolePermissionsEntity.setRole(role);
            rolePermissionsEntity.setPermission(permission);
            rolePermissionRepository.save(rolePermissionsEntity);
        }
    }

    @Override
    @Transactional
    public void deleteRole(UUID id) {
        log.info("Deleting role with id: {}", id);

        if (!roleRepository.existsById(id)) {
            throw new ResourceNotFoundException("Role not found with id: " + id);
        }

        roleRepository.deleteById(id);
    }

    @Override
    public RoleResponseVO getRoleById(UUID id) {
        log.info("Fetching role with id: {}", id);

        return roleRepository.findById(id)
                .map(this::mapToVO)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with id: " + id));
    }

    @Override
    public List<RoleResponseVO> getAllRoles() {
        log.info("Fetching all roles");

        return roleRepository.findAll().stream()
                .map(this::mapToVO)
                .collect(Collectors.toList());
    }

    private RoleResponseVO mapToVO(RoleEntity roleEntity) {
        return new RoleResponseVO(roleEntity.getId(), roleEntity.getRoleName());
    }
}
