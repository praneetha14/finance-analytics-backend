package com.finance.analytics.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import io.swagger.v3.oas.annotations.media.Schema;

@Data
@AllArgsConstructor
public class PermissionRequestDTO {

    @Schema(description = "Name of the permission", example = "FINANCIAL_RECORD_READ")
    private String name;
}
