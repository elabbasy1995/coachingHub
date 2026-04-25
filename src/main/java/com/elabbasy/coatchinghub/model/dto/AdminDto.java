package com.elabbasy.coatchinghub.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AdminDto extends AuditBaseDto {
    private String fullName;

    @JsonIgnoreProperties({"admin", "coach", "coachee"})
    private UserDto user;
}
