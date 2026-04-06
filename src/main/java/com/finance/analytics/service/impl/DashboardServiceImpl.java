package com.finance.analytics.service.impl;

import com.finance.analytics.entity.FinancialRecordEntity;
import com.finance.analytics.entity.UserEntity;
import com.finance.analytics.entity.UserRolesEntity;
import com.finance.analytics.exception.ResourceNotFoundException;
import com.finance.analytics.model.vo.DashboardSummaryVO;
import com.finance.analytics.model.vo.FinancialRecordResponseVO;
import com.finance.analytics.model.vo.PaginationResponseVO;
import com.finance.analytics.model.vo.RoleResponseVO;
import com.finance.analytics.model.vo.SuccessResponseVO;
import com.finance.analytics.model.vo.UserResponseVO;
import com.finance.analytics.repository.FinancialRecordRepository;
import com.finance.analytics.repository.UserRepository;
import com.finance.analytics.repository.UserRoleRepository;
import com.finance.analytics.security.UserPrincipal;
import com.finance.analytics.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final FinancialRecordRepository financialRecordRepository;
    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;

    @Override
    public SuccessResponseVO<List<FinancialRecordResponseVO>> getRecordsByUserId(UUID userId) {
        validateUser(userId);
        checkViewerAccess(userId);
        List<FinancialRecordEntity> financialRecordEntities = financialRecordRepository.findByUserIdAndIsActiveTrue(userId);
        List<FinancialRecordResponseVO> financialRecordResponseVOS = financialRecordEntities.stream()
                .map(this::mapRecordToVO)
                .collect(Collectors.toList());
        return SuccessResponseVO.of(200, "Records fetched successfully", financialRecordResponseVOS);
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

    @Override
    public SuccessResponseVO<List<FinancialRecordResponseVO>> getAllRecords(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<FinancialRecordEntity> financialRecordEntities = financialRecordRepository.findByIsActiveTrue(pageable);
        List<FinancialRecordResponseVO> financialRecordResponseVOS = financialRecordEntities.getContent().stream()
                .map(this::mapRecordToVO)
                .collect(Collectors.toList());

        PaginationResponseVO pagination = new PaginationResponseVO(page, size, financialRecordEntities.getTotalElements());
        return SuccessResponseVO.of(200, "All Records fetched successfully", financialRecordResponseVOS, pagination);
    }

    @Override
    public SuccessResponseVO<DashboardSummaryVO> getSummaryByUserId(UUID userId) {
        validateUser(userId);
        List<FinancialRecordEntity> records = financialRecordRepository.findByUserIdAndIsActiveTrue(userId);

        Double totalIncome = 0.0;
        Double totalExpense = 0.0;
        Map<String, Double> categoryWiseIncome = new HashMap<>();
        Map<String, Double> categoryWiseExpenses = new HashMap<>();
        Map<String, Double> monthlyTrends = new TreeMap<>(); // TreeMap keeps months sorted

        DateTimeFormatter monthFormatter = DateTimeFormatter.ofPattern("yyyy-MM");

        for (FinancialRecordEntity record : records) {
            Double amount = record.getAmount();
            String category = normalizeCategory(record.getCategory());
            String month = record.getCreatedAt().format(monthFormatter);

            if ("INCOME".equals(record.getRecordType().name())) {
                totalIncome += amount;
                categoryWiseIncome.put(category, categoryWiseIncome.getOrDefault(category, 0.0) + amount);
                monthlyTrends.put(month, monthlyTrends.getOrDefault(month, 0.0) + amount);
            } else {
                totalExpense += amount;
                categoryWiseExpenses.put(category, categoryWiseExpenses.getOrDefault(category, 0.0) + amount);
                monthlyTrends.put(month, monthlyTrends.getOrDefault(month, 0.0) - amount);
            }
        }

        List<FinancialRecordResponseVO> recentActivity = records.stream()
                .sorted(Comparator.comparing(FinancialRecordEntity::getCreatedAt).reversed())
                .limit(5)
                .map(this::mapRecordToVO)
                .collect(Collectors.toList());

        DashboardSummaryVO summary = new DashboardSummaryVO(
                totalIncome,
                totalExpense,
                totalIncome - totalExpense,
                categoryWiseIncome,
                categoryWiseExpenses,
                recentActivity,
                monthlyTrends
        );

        return SuccessResponseVO.of(200, "Summary fetched successfully", summary);
    }

    private FinancialRecordResponseVO mapRecordToVO(FinancialRecordEntity entity) {
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

    @Override
    public SuccessResponseVO<List<UserResponseVO>> getAllUsers(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<UserEntity> userEntities = userRepository.findByIsActiveTrue(pageable);
        List<UserResponseVO> userResponseVOS = userEntities.getContent().stream()
                .map(this::mapToVO)
                .collect(Collectors.toList());

        PaginationResponseVO pagination = new PaginationResponseVO(page, size, userEntities.getTotalElements());
        return SuccessResponseVO.of(200, "All Users fetched successfully", userResponseVOS, pagination);
    }

    private UserResponseVO mapToVO(UserEntity entity) {
        List<UserRolesEntity> userRoles = userRoleRepository.findByUser(entity);
        List<RoleResponseVO> roles = userRoles.stream()
                .map(ur -> new RoleResponseVO(ur.getRole().getId(), ur.getRole().getRoleName()))
                .collect(Collectors.toList());

        return new UserResponseVO(
                entity.getId(),
                entity.getFirstName(),
                entity.getLastName(),
                entity.getMobile(),
                entity.getEmail(),
                entity.getIsActive(),
                roles
        );
    }

    private void validateUser(UUID userId) {
        userRepository.findById(userId).
                orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    private String normalizeCategory(String category) {
        if (category == null) {
            return "";
        }
        return category.trim().toLowerCase();
    }
}
