package com.elabbasy.coatchinghub.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PortalCoachLookupResponse {

    private Long id;
    private String fullNameEn;
    private String fullNameAr;
}
