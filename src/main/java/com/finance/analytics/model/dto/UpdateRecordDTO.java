package com.finance.analytics.model.dto;

import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import io.swagger.v3.oas.annotations.media.Schema;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateRecordDTO {

    @Positive(message = "Amount must be greater than 0")
    @Schema(description = "Updated amount of the record", example = "150.0")
    private Double amount;

    @Schema(description = "Updated category of the record", example = "Grocery")
    private String category;
    
    @Schema(description = "Updated description of the record", example = "Weekly grocery shopping")
    private String description;
}
