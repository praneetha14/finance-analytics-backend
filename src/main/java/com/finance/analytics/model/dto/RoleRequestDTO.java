package com.finance.analytics.model.dto;

import com.finance.analytics.model.enums.RoleEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoleRequestDTO {

    private RoleEnum roleName;
    private List<UUID> permissionIds;
}
