package com.finance.analytics.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DashboardSummaryVO {
    private Double totalIncome;
    private Double totalExpenses;
    private Double balance;
    private Map<String, Double> categoryWiseIncome;
    private Map<String, Double> categoryWiseExpenses;
    private List<FinancialRecordResponseVO> recentActivity;
    private Map<String, Double> monthlyTrends;
}