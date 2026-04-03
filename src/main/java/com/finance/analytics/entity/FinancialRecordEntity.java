package com.finance.analytics.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "financial_record_entity")
@Getter
@Setter
public class FinancialRecordEntity {

    @Id
    @GeneratedValue
    private UUID id;
    private String recordType;
    private String category;
    private String description;
    private Double amount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private UserEntity user;
    private Boolean isActive = true;
    private LocalDateTime createdAt;
}
