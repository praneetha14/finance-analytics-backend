package com.finance.analytics.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "role_entity")
public class RoleEntity {

    @Id
    @GeneratedValue
    private UUID roleId;

    private String roleName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
