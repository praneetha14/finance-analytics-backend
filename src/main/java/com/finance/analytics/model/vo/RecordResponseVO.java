package com.finance.analytics.model.vo;

import com.finance.analytics.model.enums.RecordTypeEnum;

import java.time.LocalDateTime;
import java.util.UUID;

public record RecordResponseVO(UUID id, RecordTypeEnum recordTypeEnum, Double amount,
                               String category, String description, LocalDateTime createdAt) {
}
