package com.finance.analytics.model.vo;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class FailureResponseVO {

    private final int code;
    private final String message;
    private final Map<String, Object> body;

    private FailureResponseVO(int code, String message, Map<String, Object> body) {
        this.code = code;
        this.message = message;
        this.body = body;
    }

    public static FailureResponseVO of(int code, String message) {
        return new FailureResponseVO(code, message, Map.of(
                "status", code,
                "error", message
        ));
    }

    public static FailureResponseVO of(int code, String message, Map<String, Object> details) {
        return new FailureResponseVO(code, message, details);
    }
}
