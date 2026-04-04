package com.finance.analytics.repository;

import com.finance.analytics.entity.FinancialRecordEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface FinancialRecordRepository extends JpaRepository<FinancialRecordEntity, UUID> {
    Page<FinancialRecordEntity> findByUserId(UUID userId, Pageable pageable);
}
