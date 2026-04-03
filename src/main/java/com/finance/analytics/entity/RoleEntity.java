package com.finance.analytics.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "role_entity")
@Getter
@Setter
public class RoleEntity {

    @Id
    @GeneratedValue
    private UUID id;

    private String roleName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
