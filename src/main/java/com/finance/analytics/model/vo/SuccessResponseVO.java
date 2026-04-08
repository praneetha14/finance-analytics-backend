package com.finance.analytics.model.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SuccessResponseVO<T> {

    private final int code;
    private final String message;
    private final T data;
    private PaginationResponseVO pagination;

    private SuccessResponseVO(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    private SuccessResponseVO(int code, String message, T data, PaginationResponseVO pagination) {
        this.code = code;
        this.message = message;
        this.data = data;
        this.pagination = pagination;
    }

    public static <T> SuccessResponseVO<T> of(int code, String message, T data){
        return new SuccessResponseVO<>(code, message, data);
    }

    public static <T> SuccessResponseVO<T> of(int code, String message, T data, PaginationResponseVO pagination){
        return new SuccessResponseVO<>(code, message, data, pagination);
    }
}
