package com.finance.analytics.service;

import com.finance.analytics.entity.UserEntity;
import com.finance.analytics.model.vo.DashboardSummaryVO;
import com.finance.analytics.model.vo.FinancialRecordResponseVO;
import com.finance.analytics.model.vo.SuccessResponseVO;
import com.finance.analytics.model.vo.UserResponseVO;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.UUID;

public interface DashboardService {

    SuccessResponseVO<List<FinancialRecordResponseVO>> getRecordsByUserId(UUID userId);

    SuccessResponseVO<Page<FinancialRecordResponseVO>> getAllRecords(int page, int size);

    SuccessResponseVO<DashboardSummaryVO> getSummaryByUserId(UUID userId);

    SuccessResponseVO<Page<UserResponseVO>> getAllUsers(int page, int size);
}
