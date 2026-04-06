package com.finance.analytics.repository;

import com.finance.analytics.entity.PermissionsEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PermissionRepository extends JpaRepository<PermissionsEntity, UUID> {
    boolean existsByName(String name);
    Optional<PermissionsEntity> findByName(String name);
}
