package com.finance.analytics.service.impl;

import com.finance.analytics.entity.FinancialRecordEntity;
import com.finance.analytics.entity.UserEntity;
import com.finance.analytics.exception.ResourceNotFoundException;
import com.finance.analytics.model.vo.DashboardSummaryVO;
import com.finance.analytics.model.vo.FinancialRecordResponseVO;
import com.finance.analytics.model.vo.SuccessResponseVO;
import com.finance.analytics.model.vo.UserResponseVO;
import com.finance.analytics.repository.FinancialRecordRepository;
import com.finance.analytics.repository.UserRepository;
import com.finance.analytics.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final FinancialRecordRepository financialRecordRepository;
    private final UserRepository userRepository;

    @Override
    public SuccessResponseVO<List<FinancialRecordResponseVO>> getRecordsByUserId(UUID userId) {
        validateUser(userId);
        List<FinancialRecordEntity> financialRecordEntities = financialRecordRepository.findByUserIdAndIsActiveTrue(userId);
        List<FinancialRecordResponseVO> financialRecordResponseVOS = new ArrayList<>();
        for (FinancialRecordEntity financialRecordEntity : financialRecordEntities) {
            FinancialRecordResponseVO financialRecordResponseVO = new FinancialRecordResponseVO(
                    financialRecordEntity.getId(),
                    financialRecordEntity.getRecordType(),
                    financialRecordEntity.getAmount(),
                    financialRecordEntity.getCategory(),
                    financialRecordEntity.getDescription(),
                    financialRecordEntity.getCreatedAt()
            );
            financialRecordResponseVOS.add(financialRecordResponseVO);
        }
        return SuccessResponseVO.of(200, "Records fetched successfully", financialRecordResponseVOS);
    }

    @Override
    public SuccessResponseVO<Page<FinancialRecordResponseVO>> getAllRecords(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<FinancialRecordEntity> financialRecordEntities = financialRecordRepository.findByIsActiveTrue(pageable);
        List<FinancialRecordResponseVO> financialRecordResponseVOS = new ArrayList<>();
        for(FinancialRecordEntity financialRecordEntity: financialRecordEntities.getContent()){
            FinancialRecordResponseVO financialRecordResponseVO = new FinancialRecordResponseVO(
                    financialRecordEntity.getId(),
                    financialRecordEntity.getRecordType(),
                    financialRecordEntity.getAmount(),
                    financialRecordEntity.getCategory(),
                    financialRecordEntity.getDescription(),
                    financialRecordEntity.getCreatedAt()
            );
            financialRecordResponseVOS.add(financialRecordResponseVO);
        }
        Page<FinancialRecordResponseVO> financialRecordResponseVOPage =
                new PageImpl<>(
                        financialRecordResponseVOS,
                        pageable,
                        financialRecordEntities.getTotalElements()
                );
        return SuccessResponseVO.of(200, "All Records fetched successfully", financialRecordResponseVOPage);
    }

    @Override
    public SuccessResponseVO<DashboardSummaryVO> getSummaryByUserId(UUID userId) {
        validateUser(userId);
        List<FinancialRecordEntity> financialRecordEntities = financialRecordRepository.findByUserIdAndIsActiveTrue(userId);
        Double totalIncome = 0.0;
        Double totalExpense = 0.0;
        Map<String, Double> categoryWiseIncome = new HashMap<>();
        for (FinancialRecordEntity financialRecordEntity : financialRecordEntities) {
            Double amount = financialRecordEntity.getAmount();
            if (financialRecordEntity.getRecordType().name().equals("INCOME")) {
                totalIncome += amount;
                String category = normalizeCategory(financialRecordEntity.getCategory());
                categoryWiseIncome.put(
                        category,
                        categoryWiseIncome.getOrDefault(category, 0.0) + amount
                );
            } else {
                totalExpense += amount;
            }
        }

        double totalBalance = totalIncome - totalExpense;

        DashboardSummaryVO dashboardSummaryVO = new DashboardSummaryVO(
                totalIncome,
                totalExpense,
                totalBalance,
                categoryWiseIncome
        );

        return SuccessResponseVO.of(200, "Summary fetched successfully", dashboardSummaryVO);
    }

    @Override
    public SuccessResponseVO<Page<UserResponseVO>> getAllUsers(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<UserEntity> userEntities = userRepository.findByIsActiveTrue(pageable);
        List<UserResponseVO> userResponseVOS = new ArrayList<>();
        for(UserEntity userEntity: userEntities.getContent()){
            UserResponseVO userResponseVO = new UserResponseVO(
                    userEntity.getId(),
                    userEntity.getFirstName(),
                    userEntity.getLastName(),
                    userEntity.getMobile(),
                    userEntity.getEmail()
            );
            userResponseVOS.add(userResponseVO);
        }
        Page<UserResponseVO> userResponseVOPage = new PageImpl<>(
                userResponseVOS,
                pageable,
                userEntities.getTotalElements()
        );
        return SuccessResponseVO.of(200, "All Users fetched successfully", userResponseVOPage);
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
