package com.finance.analytics.repository;

import com.finance.analytics.entity.FinancialRecordEntity;
import com.finance.analytics.entity.UserEntity;
import com.finance.analytics.model.enums.RecordTypeEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface FinancialRecordRepository extends JpaRepository<FinancialRecordEntity, UUID> {

    Page<FinancialRecordEntity> findByIsActiveTrue(Pageable pageable);
    List<FinancialRecordEntity> findByUserIdAndIsActiveTrue(UUID userId);

    @Query("SELECT f FROM FinancialRecordEntity f WHERE f.isActive = true " +
           "AND (:userId IS NULL OR f.user.id = :userId) " +
           "AND (:type IS NULL OR f.recordType = :type) " +
           "AND (:category IS NULL OR LOWER(f.category) LIKE LOWER(CONCAT('%', :category, '%'))) " +
           "AND (:startDate IS NULL OR f.createdAt >= :startDate) " +
           "AND (:endDate IS NULL OR f.createdAt <= :endDate)")
    Page<FinancialRecordEntity> findWithFilters(
            @Param("userId") UUID userId,
            @Param("type") RecordTypeEnum type,
            @Param("category") String category,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable);

    @Query("SELECT f FROM FinancialRecordEntity f WHERE f.user.id = :userId " +
           "AND f.amount = :amount " +
           "AND LOWER(f.category) = LOWER(:category) " +
           "AND f.recordType = :type " +
           "AND f.isActive = true " +
           "AND f.createdAt >= :afterTime")
    List<FinancialRecordEntity> findDuplicateRecord(
            @Param("userId") UUID userId,
            @Param("amount") Double amount,
            @Param("category") String category,
            @Param("type") RecordTypeEnum type,
            @Param("afterTime") LocalDateTime afterTime);
}