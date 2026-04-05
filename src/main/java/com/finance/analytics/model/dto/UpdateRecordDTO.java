package com.finance.analytics.model.dto;

import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateRecordDTO {

    @Positive(message = "Amount must be greater than 0")
    private Double amount;

    private String category;
    private String description;
}
