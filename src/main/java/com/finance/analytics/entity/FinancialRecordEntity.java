package com.finance.analytics.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "financial_record_entity")
public class FinancialRecordEntity {

    @Id
    @GeneratedValue
    private UUID id;
    private String recordType;
    private Double amount;
    private UUID userId;
    private Boolean isActive;
    private LocalDateTime createdAt;
}
