package com.finance.analytics.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import io.swagger.v3.oas.annotations.media.Schema;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateRecordDTO {

    @NotBlank(message = "Record type is required")
    @Pattern(regexp = "^(INCOME|EXPENSE)$", message = "Record type must be either 'INCOME' or 'EXPENSE' (case-sensitive)")
    @Schema(description = "Type of financial record", allowableValues = {"INCOME", "EXPENSE"}, example = "INCOME")
    private String recordType;

    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be greater than 0")
    @Schema(description = "Amount of the record", example = "100.0", minimum = "0.01")
    private Double amount;

    @NotBlank(message = "Category should not be empty")
    @Size(max = 50, message = "Category cannot exceed 50 characters")
    @Schema(description = "Category of the record", example = "Salary")
    private String category;

    @Size(max = 256, message = "Description cannot exceed 256 characters")
    @Schema(description = "Description of the record", example = "Monthly salary")
    private String description;
}
