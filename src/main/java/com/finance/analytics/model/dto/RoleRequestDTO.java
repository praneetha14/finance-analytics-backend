package com.finance.analytics.model.dto;

import com.finance.analytics.model.enums.RoleEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoleRequestDTO {

    @Schema(description = "Name of the role", example = "VIEWER")
    private RoleEnum roleName;
    
    @Schema(description = "List of permission IDs to associate with this role")
    private List<UUID> permissionIds;
}
