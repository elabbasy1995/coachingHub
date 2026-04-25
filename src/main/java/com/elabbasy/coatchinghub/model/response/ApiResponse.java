package com.elabbasy.coatchinghub.model.response;

import com.elabbasy.coatchinghub.config.LocalizedMessage;
import com.elabbasy.coatchinghub.constant.ErrorMessage;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    private HttpStatus httpStatus;
    private String code;
    private Date timeStamp;
    private String messageEn;
    private String messageAr;
    private T data;
    private long count;
    private Integer pageIndex;
    private Integer pageCount;
    private Integer pageSize;
    private List<ErrorResponse> errors;

    public ApiResponse(T data) {
        this.data = data;
        this.httpStatus = HttpStatus.OK;
        this.code = "200";
        this.messageAr = LocalizedMessage.getArabicMessage(ErrorMessage.SUCCESS.name());
        this.messageEn = LocalizedMessage.getEnglishMessage(ErrorMessage.SUCCESS.name());
        this.timeStamp = new Date();
        this.count = data == null ? 0 : (data instanceof List<?>) ? ((List<?>) data).size() : 1;
    }

    public ApiResponse(T data, ErrorMessage messageCode) {
        this.data = data;
        this.httpStatus = HttpStatus.OK;
        this.code = "200";
        this.messageAr = LocalizedMessage.getArabicMessage(messageCode.name());
        this.messageEn = LocalizedMessage.getEnglishMessage(messageCode.name());
        this.timeStamp = new Date();
        this.count = data == null ? 0 : (data instanceof List<?>) ? ((List<?>) data).size() : 1;
    }

    public ApiResponse(HttpStatus httpStatus) {
        if (HttpStatus.OK.equals(httpStatus) || HttpStatus.CREATED.equals(httpStatus)
                || HttpStatus.ACCEPTED.equals(httpStatus)) {
            this.httpStatus = httpStatus;
            this.code = "200";
            this.messageAr = LocalizedMessage.getArabicMessage(ErrorMessage.SUCCESS.name());
            this.messageEn = LocalizedMessage.getEnglishMessage(ErrorMessage.SUCCESS.name());
            this.timeStamp = new Date();
        } else {
            this.httpStatus = httpStatus;
            this.code = "400";
            this.messageAr = LocalizedMessage.getArabicMessage(ErrorMessage.GENERAL_ERROR.name());
            this.messageEn = LocalizedMessage.getEnglishMessage(ErrorMessage.GENERAL_ERROR.name());
            this.timeStamp = new Date();
        }
    }

    public ApiResponse(T data, long count) {
        this.data = data;
        this.count = count;
        this.httpStatus = HttpStatus.OK;
        this.code = "200";
        this.messageAr = LocalizedMessage.getArabicMessage(ErrorMessage.SUCCESS.name());
        this.messageEn = LocalizedMessage.getEnglishMessage(ErrorMessage.SUCCESS.name());
        this.timeStamp = new Date();
    }

    public ApiResponse(T data, long count, int pageCount, int pageSize, int pageIndex) {
        this.data = data;
        this.count = count;
        this.httpStatus = HttpStatus.OK;
        this.code = "200";
        this.messageAr = LocalizedMessage.getArabicMessage(ErrorMessage.SUCCESS.name());
        this.messageEn = LocalizedMessage.getEnglishMessage(ErrorMessage.SUCCESS.name());
        this.timeStamp = new Date();
        this.pageSize = pageSize;
        this.pageIndex = pageIndex;
        this.pageCount = pageCount;
    }

    public ApiResponse(HttpStatus httpStatus, String code, String messageEn, String messageAr) {
        this.httpStatus = httpStatus;
        this.code = code;
        this.messageEn = messageEn;
        this.messageAr = messageAr;
        this.timeStamp = new Date();
    }

    public ApiResponse(Page<T> page) {
        this.data = (T) page.getContent();
        this.httpStatus = HttpStatus.OK;
        this.code = "200";
        this.messageAr = LocalizedMessage.getArabicMessage(ErrorMessage.SUCCESS.name());
        this.messageEn = LocalizedMessage.getEnglishMessage(ErrorMessage.SUCCESS.name());
        this.timeStamp = new Date();
        this.count = page.getTotalElements();
        this.pageCount = page.getTotalPages();
        this.pageIndex = page.getNumber();
        this.pageSize = page.getSize();
    }

}
