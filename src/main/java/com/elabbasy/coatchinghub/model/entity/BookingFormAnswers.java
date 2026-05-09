package com.elabbasy.coatchinghub.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class BookingFormAnswers {

    @Column(name = "form_challenge")
    private String challenge;

    @Column(name = "form_why_important")
    private String whyImportant;

    @Column(name = "form_commitment")
    private Integer commitment;

    @Column(name = "form_open_to_help")
    private Boolean openToHelp;

    @Column(name = "form_tried_before")
    private String triedBefore;
}
