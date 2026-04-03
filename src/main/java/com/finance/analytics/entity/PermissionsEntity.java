package com.finance.analytics.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "permissions_entity")
public class PermissionsEntity {

    @Id
    @GeneratedValue
    private UUID id;
    private String name;
    private LocalDateTime createdAt;
}
