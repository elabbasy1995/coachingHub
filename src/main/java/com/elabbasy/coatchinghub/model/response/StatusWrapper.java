package com.elabbasy.coatchinghub.model.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StatusWrapper {
    private String nameEn;
    private String nameAr;
}
