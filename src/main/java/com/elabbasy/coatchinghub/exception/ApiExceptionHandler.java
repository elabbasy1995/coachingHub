package com.elabbasy.coatchinghub.exception;

import com.elabbasy.coatchinghub.config.LocalizedMessage;
import com.elabbasy.coatchinghub.constant.ErrorMessage;
import com.elabbasy.coatchinghub.model.response.ApiResponse;
import com.elabbasy.coatchinghub.model.response.ErrorResponse;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@ControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(value = {BusinessException.class})
    public ResponseEntity<ApiResponse<Object>> handleBusinessException(final BusinessException businessException) {
        List<ErrorResponse> errors = new ArrayList<>();
        businessException.getErrors().stream().forEach(errorMessage -> {
            String messageAr = LocalizedMessage.getArabicMessage(errorMessage.name());
            String messageEn = LocalizedMessage.getEnglishMessage(errorMessage.name());
            ErrorResponse errorResponse = new ErrorResponse(messageEn, messageAr);
            errors.add(errorResponse);
        });

        if (businessException.getErrors().contains(ErrorMessage.USER_INACTIVE)) {
            ApiResponse<Object> response = ApiResponse.builder()
                    .code("422")
                    .httpStatus(HttpStatus.UNPROCESSABLE_ENTITY)
                    .messageAr(errors.stream().map(ErrorResponse::getMessageAr).collect(Collectors.joining(" , ")))
                    .messageEn(errors.stream().map(ErrorResponse::getMessageEn).collect(Collectors.joining(" , ")))
                    .timeStamp(new Date())
                    .errors(errors)
                    .build();
            return new ResponseEntity<>(response, HttpStatus.UNPROCESSABLE_ENTITY);
        } else {
            //todo log errors
            ApiResponse<Object> response = ApiResponse.builder()
                    .code("400")
                    .httpStatus(HttpStatus.BAD_REQUEST)
                    .messageAr(errors.stream().map(ErrorResponse::getMessageAr).collect(Collectors.joining(" , ")))
                    .messageEn(errors.stream().map(ErrorResponse::getMessageEn).collect(Collectors.joining(" , ")))
                    .timeStamp(new Date())
                    .errors(errors)
                    .build();
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    @ExceptionHandler(value = {Exception.class})
    public ResponseEntity<ApiResponse<Object>> handleException(final Exception exception) {
        log.error(exception.toString());
        exception.printStackTrace();
        ApiResponse<Object> response = ApiResponse.builder()
                .code("500")
                .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                .messageAr(exception.getMessage())
                .messageEn(exception.getMessage())
                .timeStamp(new Date())
                .build();
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(value = {MethodArgumentNotValidException.class})
    public ResponseEntity<ApiResponse<Object>> handleMethodArgumentNotValidException(final MethodArgumentNotValidException exception) {
        List<ErrorResponse> errorResponses = new ArrayList<>();

        String defaultMessageAr = LocalizedMessage.getArabicMessage(ErrorMessage.MISSING_REQUIRED_ATTRIBUTES.name());
        String defaultMessageEn = LocalizedMessage.getEnglishMessage(ErrorMessage.MISSING_REQUIRED_ATTRIBUTES.name());

        exception.getBindingResult().getFieldErrors().forEach(error -> {
            String messageAr = LocalizedMessage.getArabicMessage(error.getDefaultMessage());
            String messageEn = LocalizedMessage.getEnglishMessage(error.getDefaultMessage());
            if (Objects.isNull(messageAr) || messageAr.isEmpty()) {
                messageAr = defaultMessageAr;
            }
            if (Objects.isNull(messageEn) || messageEn.isEmpty()) {
                messageEn = defaultMessageEn;
            }
            ErrorResponse errorResponse = new ErrorResponse(messageEn, messageAr);
            errorResponses.add(errorResponse);
        });

        ApiResponse<Object> response = ApiResponse.builder()
                .code("400")
                .httpStatus(HttpStatus.BAD_REQUEST)
                .messageAr(errorResponses.stream().map(ErrorResponse::getMessageAr).collect(Collectors.joining(" , ")))
                .messageEn(errorResponses.stream().map(ErrorResponse::getMessageEn).collect(Collectors.joining(" , ")))
                .timeStamp(new Date())
                .errors(errorResponses)
                .build();
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {ServletRequestBindingException.class})
    public ResponseEntity<ApiResponse<Object>> handleServletRequestBindingException(final ServletRequestBindingException exception) {
        List<ErrorResponse> errorResponses = new ArrayList<>();

        String defaultMessageAr = LocalizedMessage.getArabicMessage(ErrorMessage.UNAUTHORIZE_TO_PERFORM_ACTION.name());
        String defaultMessageEn = LocalizedMessage.getEnglishMessage(ErrorMessage.UNAUTHORIZE_TO_PERFORM_ACTION.name());

        ApiResponse<Object> response = ApiResponse.builder()
                .code("403")
                .httpStatus(HttpStatus.FORBIDDEN)
                .messageAr(defaultMessageAr)
                .messageEn(defaultMessageEn)
                .timeStamp(new Date())
                .errors(errorResponses)
                .build();
        return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(value = ExpiredJwtException.class)
    public ResponseEntity<ApiResponse<Object>> handleExpiredJwtException(final ExpiredJwtException exception) {
        List<ErrorResponse> errorResponses = new ArrayList<>();

        String defaultMessageAr = LocalizedMessage.getArabicMessage(ErrorMessage.UNAUTHORIZE_TO_PERFORM_ACTION.name());
        String defaultMessageEn = LocalizedMessage.getEnglishMessage(ErrorMessage.UNAUTHORIZE_TO_PERFORM_ACTION.name());

        ApiResponse<Object> response = ApiResponse.builder()
                .code("401")
                .httpStatus(HttpStatus.UNAUTHORIZED)
                .messageAr(defaultMessageAr)
                .messageEn(defaultMessageEn)
                .timeStamp(new Date())
                .errors(errorResponses)
                .build();
        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }
}
