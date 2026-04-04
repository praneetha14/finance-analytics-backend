package com.finance.analytics.repository;

import com.finance.analytics.entity.UserRolesEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserRoleRepository extends JpaRepository<UserRolesEntity, UUID> {
}
