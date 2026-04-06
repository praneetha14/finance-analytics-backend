package com.finance.analytics.repository;

import com.finance.analytics.entity.UserEntity;
import com.finance.analytics.entity.UserRolesEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface UserRoleRepository extends JpaRepository<UserRolesEntity, UUID> {
    List<UserRolesEntity> findByUser(UserEntity user);
    void deleteByUser(UserEntity user);
}
