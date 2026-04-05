package com.finance.analytics.service.impl;

import com.finance.analytics.entity.RoleEntity;
import com.finance.analytics.exception.DuplicateResourceException;
import com.finance.analytics.exception.ResourceNotFoundException;
import com.finance.analytics.model.dto.RoleRequestDTO;
import com.finance.analytics.model.vo.RoleResponseVO;
import com.finance.analytics.repository.RoleRepository;
import com.finance.analytics.service.RoleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Slf4j
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;

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
        return mapToVO(savedRole);
    }

    @Override
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
        return mapToVO(updatedRole);
    }

    @Override
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
