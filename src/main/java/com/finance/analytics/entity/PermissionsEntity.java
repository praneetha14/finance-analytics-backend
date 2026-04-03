package com.finance.analytics.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "permissions_entity")
@Getter
@Setter
public class PermissionsEntity {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "name")
    private String name;
    private LocalDateTime createdAt;
}
