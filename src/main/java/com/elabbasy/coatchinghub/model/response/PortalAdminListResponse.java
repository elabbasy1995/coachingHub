package com.elabbasy.coatchinghub.model.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PortalAdminListResponse {
    private Long id;
    private String fullName;
    private String email;
    private Boolean enabled;
    private List<PortalAdminPermissionResponse> permissions;
}
