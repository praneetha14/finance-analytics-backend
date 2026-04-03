package com.finance.analytics.model.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class UpdateRecordDTO {

    @Positive(message = "Amount must be greater than 0")
    private Double amount;

    private String category;
    private String description;
}
