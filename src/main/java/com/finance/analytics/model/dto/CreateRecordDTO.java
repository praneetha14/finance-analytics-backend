package com.finance.analytics.model.dto;

import com.finance.analytics.model.enums.RecordTypeEnum;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateRecordDTO {

    @NotNull(message = "Record type is required")
    private RecordTypeEnum recordType;

    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be greater than 0")
    private Double amount;

    @NotBlank(message = "Category should not be empty")
    @Size(max = 50, message = "Category cannot exceed 50 characters")
    private String category;

    @Size(max = 256, message = "Description cannot exceed 256 characters")
    private String description;
}
