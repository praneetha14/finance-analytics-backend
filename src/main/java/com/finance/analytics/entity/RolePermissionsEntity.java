package com.finance.analytics.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.UUID;

@Entity
@Table(name = "role_permissions_entity")
public class RolePermissionsEntity {

    @Id
    @GeneratedValue
    private UUID id;
    private UUID roleId;
    private UUID permissionId;
}
