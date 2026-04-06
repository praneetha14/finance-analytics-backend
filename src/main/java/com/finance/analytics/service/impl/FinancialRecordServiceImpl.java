package com.finance.analytics.service.impl;

import com.finance.analytics.entity.FinancialRecordEntity;
import com.finance.analytics.entity.UserEntity;
import com.finance.analytics.exception.DuplicateResourceException;
import com.finance.analytics.exception.InvalidInputException;
import com.finance.analytics.exception.ResourceNotFoundException;
import com.finance.analytics.model.dto.CreateRecordDTO;
import com.finance.analytics.model.dto.UpdateRecordDTO;
import com.finance.analytics.model.enums.RecordTypeEnum;
import com.finance.analytics.model.vo.FinancialRecordResponseVO;
import com.finance.analytics.model.vo.PaginationResponseVO;
import com.finance.analytics.model.vo.SuccessResponseVO;
import com.finance.analytics.repository.FinancialRecordRepository;
import com.finance.analytics.repository.UserRepository;
import com.finance.analytics.security.UserPrincipal;
import com.finance.analytics.service.FinancialRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class FinancialRecordServiceImpl implements FinancialRecordService {

    private final FinancialRecordRepository financialRecordRepository;
    private final UserRepository userRepository;

    @Override
    public SuccessResponseVO<FinancialRecordResponseVO> createRecord(UUID userId, CreateRecordDTO createRecordDTO) {
        checkViewerAccess(userId);

        UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        String normalizedCategory = createRecordDTO.getCategory().trim().toLowerCase();
        RecordTypeEnum typeEnum = RecordTypeEnum.valueOf(createRecordDTO.getRecordType().toUpperCase());

        LocalDateTime oneMinuteAgo = LocalDateTime.now().minusMinutes(1);
        List<FinancialRecordEntity> duplicates = financialRecordRepository.findDuplicateRecord(
                userId,
                createRecordDTO.getAmount(),
                normalizedCategory,
                typeEnum,
                oneMinuteAgo);

        if (!duplicates.isEmpty()) {
            throw new DuplicateResourceException("A similar record for category '" +
                    createRecordDTO.getCategory() + "' was already created. This might be a duplicate.");
        }

        FinancialRecordEntity financialRecordEntity = new FinancialRecordEntity();
        financialRecordEntity.setRecordType(typeEnum);
        financialRecordEntity.setAmount(createRecordDTO.getAmount());
        financialRecordEntity.setCategory(normalizedCategory);
        financialRecordEntity.setDescription(createRecordDTO.getDescription());
        financialRecordEntity.setUser(userEntity);
        financialRecordEntity.setCreatedAt(LocalDateTime.now());

        financialRecordEntity = financialRecordRepository.save(financialRecordEntity);
        return SuccessResponseVO.of(201, "Record created successfully", mapToVO(financialRecordEntity));
    }

    @Override
    public SuccessResponseVO<FinancialRecordResponseVO> updateRecord(UUID recordId, UpdateRecordDTO updateRecordDTO) {
        FinancialRecordEntity financialRecordEntity = financialRecordRepository.findById(recordId)
                .orElseThrow(() -> new ResourceNotFoundException("Record not found"));

        // Enforce that VIEWER can only update own records
        checkViewerAccess(financialRecordEntity.getUser().getId());

        if (!Boolean.TRUE.equals(financialRecordEntity.getIsActive())) {
            throw new InvalidInputException("Record is already deleted");
        }
        if (updateRecordDTO.getAmount() != null) {
            if (updateRecordDTO.getAmount() <= 0) {
                throw new InvalidInputException("Amount must be greater than 0");
            }
            financialRecordEntity.setAmount(updateRecordDTO.getAmount());
        }
        if (updateRecordDTO.getCategory() != null && !updateRecordDTO.getCategory().trim().isEmpty()) {
            String newCategory = updateRecordDTO.getCategory().trim().toLowerCase();
            String existingCategory = null;
            if (financialRecordEntity.getCategory() != null) {
                existingCategory = financialRecordEntity.getCategory().trim().toLowerCase();
            }
            if (!newCategory.equals(existingCategory)) {
                financialRecordEntity.setCategory(newCategory);
            }
        }
        if (updateRecordDTO.getDescription() != null) {
            financialRecordEntity.setDescription(updateRecordDTO.getDescription());
        }
        financialRecordEntity = financialRecordRepository.save(financialRecordEntity);
        return SuccessResponseVO.of(200, "Record updated successfully", mapToVO(financialRecordEntity));
    }

    @Override
    public SuccessResponseVO<FinancialRecordResponseVO> getRecordById(UUID recordId) {
        FinancialRecordEntity recordEntity = financialRecordRepository.findById(recordId)
                .orElseThrow(() -> new ResourceNotFoundException("Record not found"));

        // Enforce that VIEWER can only see own record
        checkViewerAccess(recordEntity.getUser().getId());

        return SuccessResponseVO.of(200, "Record fetched successfully", mapToVO(recordEntity));
    }

    @Override
    public SuccessResponseVO<List<FinancialRecordResponseVO>> getFilteredRecords(
            UUID userId, String type, String category,
            LocalDateTime startDate, LocalDateTime endDate,
            Integer page, Integer size, String sort) {

        // If user is a VIEWER, force userId to be current user's ID
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserPrincipal principal) {
            boolean isViewer = authentication.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_VIEWER"));
            if (isViewer) {
                userId = principal.getId();
            }
        }

        RecordTypeEnum typeEnum = null;
        if (type != null && !type.isEmpty()) {
            try {
                typeEnum = RecordTypeEnum.valueOf(type.toUpperCase());
            } catch (IllegalArgumentException e) {
                // Ignore invalid type
            }
        }

        // Validate and provide defaults for pagination
        if (page == null || page < 0) page = 0;
        if (size == null || size <= 0) size = 10;
        Sort.Direction direction = Sort.Direction.DESC;
        if (sort != null && sort.equalsIgnoreCase("ASC")) {
            direction = Sort.Direction.ASC;
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, "createdAt"));

        Page<FinancialRecordEntity> records = financialRecordRepository.findWithFilters(
                userId, typeEnum, category, startDate, endDate, pageable);

        List<FinancialRecordResponseVO> responseList = records.getContent().stream()
                .map(this::mapToVO)
                .collect(Collectors.toList());

        PaginationResponseVO pagination = new PaginationResponseVO(page, size, records.getTotalElements());
        return SuccessResponseVO.of(200, "Filtered records fetched successfully", responseList, pagination);
    }

    @Override
    public void deleteRecord(UUID recordId) {
        FinancialRecordEntity financialRecordEntity = financialRecordRepository.findById(recordId)
                .orElseThrow(() -> new ResourceNotFoundException("Record not found"));

        // Enforce that VIEWER can only delete own records (though they shouldn't have delete permission anyway)
        checkViewerAccess(financialRecordEntity.getUser().getId());

        if (!Boolean.TRUE.equals(financialRecordEntity.getIsActive())) {
            throw new ResourceNotFoundException("Record is not active");
        }
        financialRecordEntity.setIsActive(Boolean.FALSE);
        financialRecordRepository.save(financialRecordEntity);
    }

    private void checkViewerAccess(UUID userId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserPrincipal principal) {
            boolean isViewer = authentication.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_VIEWER"));
            if (isViewer && !principal.getId().equals(userId)) {
                throw new com.finance.analytics.exception.UserUnauthorizedException("You are not authorized to access this user's data");
            }
        }
    }

    private FinancialRecordResponseVO mapToVO(FinancialRecordEntity entity) {
        return new FinancialRecordResponseVO(
                entity.getId(),
                entity.getRecordType(),
                entity.getAmount(),
                entity.getCategory(),
                entity.getDescription(),
                entity.getCreatedAt(),
                entity.getUser() != null ? entity.getUser().getId() : null
        );
    }
}
