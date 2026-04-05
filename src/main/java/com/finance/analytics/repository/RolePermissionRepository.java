package com.finance.analytics.repository;

import com.finance.analytics.entity.RoleEntity;
import com.finance.analytics.entity.RolePermissionsEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface RolePermissionRepository extends JpaRepository<RolePermissionsEntity, UUID> {
    List<RolePermissionsEntity> findByRole(RoleEntity role);
}
