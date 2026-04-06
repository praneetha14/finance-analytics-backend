package com.finance.analytics.service.impl;

import com.finance.analytics.AbstractTest;
import com.finance.analytics.entity.FinancialRecordEntity;
import com.finance.analytics.entity.UserEntity;
import com.finance.analytics.model.enums.RecordTypeEnum;
import com.finance.analytics.model.vo.DashboardSummaryVO;
import com.finance.analytics.model.vo.FinancialRecordResponseVO;
import com.finance.analytics.model.vo.SuccessResponseVO;
import com.finance.analytics.repository.FinancialRecordRepository;
import com.finance.analytics.repository.UserRepository;
import com.finance.analytics.service.DashboardService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DashboardServiceTest extends AbstractTest {

    @Autowired
    private DashboardService dashboardService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FinancialRecordRepository financialRecordRepository;

    private UserEntity testUser;

    @BeforeEach
    void setUp() {
        testUser = new UserEntity();
        testUser.setFirstName("Dashboard");
        testUser.setLastName("User");
        testUser.setEmail("dashboard" + UUID.randomUUID() + "@example.com");
        testUser.setMobile("5555555555");
        testUser.setIsActive(true);
        testUser = userRepository.save(testUser);

        FinancialRecordEntity income = new FinancialRecordEntity();
        income.setRecordType(RecordTypeEnum.INCOME);
        income.setAmount(1000.0);
        income.setCategory("Salary");
        income.setUser(testUser);
        income.setCreatedAt(LocalDateTime.now());
        income.setIsActive(true);
        financialRecordRepository.save(income);

        FinancialRecordEntity expense = new FinancialRecordEntity();
        expense.setRecordType(RecordTypeEnum.EXPENSE);
        expense.setAmount(200.0);
        expense.setCategory("Food");
        expense.setUser(testUser);
        expense.setCreatedAt(LocalDateTime.now());
        expense.setIsActive(true);
        financialRecordRepository.save(expense);
    }

    @Test
    void getRecordsByUserIdSuccessTest() {
        SuccessResponseVO<List<FinancialRecordResponseVO>> response = dashboardService.getRecordsByUserId(testUser.getId());

        assertNotNull(response, ASSERTION_ERROR_MESSAGE);
        assertEquals(200, response.getCode(), ASSERTION_ERROR_MESSAGE);
        assertEquals(2, response.getData().size(), ASSERTION_ERROR_MESSAGE);
    }

    @Test
    void getSummaryByUserIdSuccessTest() {
        SuccessResponseVO<DashboardSummaryVO> response = dashboardService.getSummaryByUserId(testUser.getId());

        assertNotNull(response, ASSERTION_ERROR_MESSAGE);
        assertEquals(200, response.getCode(), ASSERTION_ERROR_MESSAGE);
        assertEquals(1000.0, response.getData().getTotalIncome(), ASSERTION_ERROR_MESSAGE);
        assertEquals(200.0, response.getData().getTotalExpenses(), ASSERTION_ERROR_MESSAGE);
        assertEquals(800.0, response.getData().getBalance(), ASSERTION_ERROR_MESSAGE);
    }

    @Test
    void getAllRecordsSuccessTest() {
        SuccessResponseVO<List<FinancialRecordResponseVO>> response = dashboardService.getAllRecords(0, 10);

        assertNotNull(response, ASSERTION_ERROR_MESSAGE);
        assertTrue(response.getPagination().getTotalCount() >= 2, ASSERTION_ERROR_MESSAGE);
    }
}
