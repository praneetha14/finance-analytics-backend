package com.finance.analytics.model.dto;

import com.finance.analytics.model.enums.RoleEnum;
import lombok.Data;

@Data
public class RoleRequestDTO {
    private RoleEnum roleName;
}
