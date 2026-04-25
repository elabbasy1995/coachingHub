package com.elabbasy.coatchinghub.model.dto;

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
public class RoleDto {

    private Long id;

    private String name; // ADMIN, COACH, COACHEE

    private Set<PermissionDto> permissions = new HashSet<>();
}
