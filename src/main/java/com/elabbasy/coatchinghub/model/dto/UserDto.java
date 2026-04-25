package com.elabbasy.coatchinghub.model.dto;

import com.elabbasy.coatchinghub.model.entity.Admin;
import com.elabbasy.coatchinghub.model.entity.Coachee;
import com.elabbasy.coatchinghub.model.entity.Role;
import com.elabbasy.coatchinghub.model.enums.Language;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {

    private Long id;

    private String email;

    @JsonIgnore
    private String password;

    private Language language;

    private boolean enabled = true;

    private Set<RoleDto> roles = new HashSet<>();

    @JsonIgnoreProperties("user")
    private AdminDto admin;

    @JsonIgnoreProperties("user")
    private CoacheeDto coachee;

    @JsonIgnoreProperties("user")
    private CoachDto coach;

}
