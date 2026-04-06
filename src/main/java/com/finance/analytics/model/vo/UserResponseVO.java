package com.finance.analytics.model.vo;

import java.util.List;
import java.util.UUID;

public record UserResponseVO(UUID id, String firstName, String lastName,
                             String mobile, String email, Boolean isActive,
                             List<RoleResponseVO> roles) {
}
