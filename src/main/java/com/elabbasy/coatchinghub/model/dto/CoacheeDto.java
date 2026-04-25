package com.elabbasy.coatchinghub.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CoacheeDto extends AuditBaseDto {

    private String fullName;
    private LocalDate birthDate;
    private String profileImageUrl;
    private String phoneNumber;


    @JsonIgnoreProperties({"admin", "coach", "coachee"})
    private UserDto user;
}
