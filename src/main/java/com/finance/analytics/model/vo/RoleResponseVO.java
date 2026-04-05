package com.finance.analytics.model.vo;

import com.finance.analytics.model.enums.RoleEnum;
import java.util.UUID;

public record RoleResponseVO(UUID id, RoleEnum roleName) {
}
