package com.finance.analytics.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.UUID;

@Entity
@Table(name = "user_entity")
public class UserEntity {

    @Id
    @GeneratedValue
    private UUID userId;
    private String firstName;
    private String lastName;
    private String mobile;
    private String email;
    private String password;
}
