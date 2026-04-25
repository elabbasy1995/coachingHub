package com.elabbasy.coatchinghub.model.response;

import com.elabbasy.coatchinghub.model.entity.Permission;
import com.elabbasy.coatchinghub.model.entity.RefreshToken;
import com.elabbasy.coatchinghub.model.entity.Role;
import com.elabbasy.coatchinghub.model.entity.User;
import com.elabbasy.coatchinghub.model.enums.RoleName;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
public class LoginResponse implements Serializable {

    private String accessToken;
    @JsonIgnoreProperties("user")
    private RefreshToken refreshToken;
    private List<String> permissions;

    public LoginResponse(String accessToken, RefreshToken refreshToken) {
        this(accessToken, refreshToken, List.of());
    }

    public LoginResponse(String accessToken, RefreshToken refreshToken, List<String> permissions) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.permissions = permissions == null ? List.of() : List.copyOf(permissions);
    }

    public LoginResponse(String accessToken, RefreshToken refreshToken, User user, RoleName activeRole) {
        this(accessToken, refreshToken, resolvePermissions(user, activeRole));
    }

    private static List<String> resolvePermissions(User user, RoleName activeRole) {
        if (user == null || activeRole == null || user.getRoles() == null) {
            return List.of();
        }

        String activeRoleName = activeRole.name();
        return user.getRoles().stream()
                .filter(Objects::nonNull)
                .filter(role -> isRoleInActiveScope(role, activeRoleName))
                .map(Role::getPermissions)
                .filter(Objects::nonNull)
                .flatMap(Collection::stream)
                .map(Permission::getName)
                .filter(Objects::nonNull)
                .distinct()
                .sorted()
                .toList();
    }

    private static boolean isRoleInActiveScope(Role role, String activeRoleName) {
        return role.getName() != null
                && (role.getName().equals(activeRoleName)
                || role.getName().startsWith(activeRoleName + "_SCOPE_"));
    }

}
