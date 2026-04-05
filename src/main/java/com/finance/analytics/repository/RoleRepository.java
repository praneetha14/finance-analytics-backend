package com.finance.analytics.repository;

import com.finance.analytics.entity.RoleEntity;
import com.finance.analytics.model.enums.RoleEnum;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface RoleRepository extends JpaRepository<RoleEntity, UUID> {
    boolean existsByRoleName(RoleEnum roleName);
    Optional<RoleEntity> findByRoleName(RoleEnum roleName);
}
