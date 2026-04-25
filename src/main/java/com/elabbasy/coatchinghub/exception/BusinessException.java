package com.elabbasy.coatchinghub.exception;

import com.elabbasy.coatchinghub.constant.ErrorMessage;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
public class BusinessException extends RuntimeException {

    private final String message;
    private final List<ErrorMessage> errors;

    public BusinessException(ErrorMessage message) {
        super(message.name());
        this.message = message.name();
        this.errors = List.of(message);
    }

    public BusinessException( List<ErrorMessage> errors) {
        super(errors.stream().map(ErrorMessage::name).collect(Collectors.joining(",")));
        this.message = errors.stream().map(ErrorMessage::name).collect(Collectors.joining(","));
        this.errors = errors;
    }
}
