package com.elabbasy.coatchinghub.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BookingFormAnswersDto {

    private String challenge;
    private String whyImportant;
    private Integer commitment;
    private Boolean openToHelp;
    private String triedBefore;
}
