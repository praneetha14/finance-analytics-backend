package com.finance.analytics.model.vo;

import java.util.UUID;

public record UserResponseVO(UUID id, String firstName, String lastName,
                             String mobile, String email) {
}
