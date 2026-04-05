package com.finance.analytics.model.vo;

import java.util.Map;
import java.util.UUID;

public record DashboardSummaryVO(Double totalIncome, Double totalExpense, Double totalBalance,
                                 Map<String, Double> categoryWiseIncome) {
}
