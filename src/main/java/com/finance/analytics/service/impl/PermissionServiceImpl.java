package com.finance.analytics.service.impl;

import com.finance.analytics.entity.PermissionsEntity;
import com.finance.analytics.exception.DuplicateResourceException;
import com.finance.analytics.exception.ResourceNotFoundException;
import com.finance.analytics.model.dto.PermissionRequestDTO;
import com.finance.analytics.model.vo.PermissionResponseVO;
import com.finance.analytics.repository.PermissionRepository;
import com.finance.analytics.service.PermissionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Slf4j
public class PermissionServiceImpl implements PermissionService {

    private final PermissionRepository permissionRepository;

    @Override
    public PermissionResponseVO createPermission(PermissionRequestDTO requestDTO) {
        log.info("Creating new permission: {}", requestDTO.getName());

        if (permissionRepository.existsByName(requestDTO.getName())) {
            throw new DuplicateResourceException("Permission already exists with name: " + requestDTO.getName());
        }

        PermissionsEntity permissionsEntity = new PermissionsEntity();
        permissionsEntity.setName(requestDTO.getName());
        permissionsEntity.setCreatedAt(LocalDateTime.now());

        PermissionsEntity savedPermission = permissionRepository.save(permissionsEntity);
        return mapToVO(savedPermission);
    }

    @Override
    public PermissionResponseVO updatePermission(UUID id, PermissionRequestDTO requestDTO) {
        log.info("Updating permission with id: {}", id);

        PermissionsEntity permissionsEntity = permissionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Permission not found with id: " + id));

        if (!permissionsEntity.getName().equalsIgnoreCase(requestDTO.getName()) &&
                permissionRepository.existsByName(requestDTO.getName())) {
            throw new DuplicateResourceException("Permission name already exists: " + requestDTO.getName());
        }

        permissionsEntity.setName(requestDTO.getName());

        PermissionsEntity updatedPermission = permissionRepository.save(permissionsEntity);
        return mapToVO(updatedPermission);
    }

    @Override
    public void deletePermission(UUID id) {
        log.info("Deleting permission with id: {}", id);

        if (!permissionRepository.existsById(id)) {
            throw new ResourceNotFoundException("Permission not found with id: " + id);
        }

        permissionRepository.deleteById(id);
    }

    @Override
    public PermissionResponseVO getPermissionById(UUID id) {
        log.info("Fetching permission with id: {}", id);

        return permissionRepository.findById(id)
                .map(this::mapToVO)
                .orElseThrow(() -> new ResourceNotFoundException("Permission not found with id: " + id));
    }

    @Override
    public List<PermissionResponseVO> getAllPermissions() {
        log.info("Fetching all permissions");

        return permissionRepository.findAll().stream()
                .map(this::mapToVO)
                .collect(Collectors.toList());
    }

    private PermissionResponseVO mapToVO(PermissionsEntity entity) {
        return new PermissionResponseVO(entity.getId(), entity.getName());
    }
}
