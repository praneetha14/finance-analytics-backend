package com.finance.analytics.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateRecordDTO {

    @NotBlank(message = "Record type is required")
    @Pattern(regexp = "^(INCOME|EXPENSE)$", message = "Record type must be either 'INCOME' or 'EXPENSE' (case-sensitive)")
    private String recordType;

    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be greater than 0")
    private Double amount;

    @NotBlank(message = "Category should not be empty")
    @Size(max = 50, message = "Category cannot exceed 50 characters")
    private String category;

    @Size(max = 256, message = "Description cannot exceed 256 characters")
    private String description;
}
