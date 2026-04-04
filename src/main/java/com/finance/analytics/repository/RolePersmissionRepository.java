package com.finance.analytics.repository;

import com.finance.analytics.entity.RolePermissionsEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface RolePersmissionRepository extends JpaRepository<RolePermissionsEntity, UUID> {
}
