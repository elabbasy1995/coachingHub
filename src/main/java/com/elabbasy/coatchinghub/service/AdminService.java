package com.elabbasy.coatchinghub.service;

import com.elabbasy.coatchinghub.constant.ErrorMessage;
import com.elabbasy.coatchinghub.exception.BusinessException;
import com.elabbasy.coatchinghub.mapper.AdminMapper;
import com.elabbasy.coatchinghub.model.dto.AdminDto;
import com.elabbasy.coatchinghub.model.entity.Admin;
import com.elabbasy.coatchinghub.model.entity.Permission;
import com.elabbasy.coatchinghub.model.entity.Role;
import com.elabbasy.coatchinghub.model.entity.User;
import com.elabbasy.coatchinghub.model.enums.PortalAdminPermission;
import com.elabbasy.coatchinghub.model.enums.RoleName;
import com.elabbasy.coatchinghub.model.request.ApiRequest;
import com.elabbasy.coatchinghub.model.request.InvitePortalAdminRequest;
import com.elabbasy.coatchinghub.model.request.UpdatePortalAdminRequest;
import com.elabbasy.coatchinghub.model.request.UpdatePortalAdminProfileRequest;
import com.elabbasy.coatchinghub.model.response.ApiResponse;
import com.elabbasy.coatchinghub.model.response.PortalAdminDetailsResponse;
import com.elabbasy.coatchinghub.model.response.PortalAdminListResponse;
import com.elabbasy.coatchinghub.model.response.PortalAdminPermissionResponse;
import com.elabbasy.coatchinghub.repository.AdminRepository;
import com.elabbasy.coatchinghub.repository.PermissionRepository;
import com.elabbasy.coatchinghub.repository.RoleRepository;
import com.elabbasy.coatchinghub.repository.UserRepository;
import com.elabbasy.coatchinghub.util.PasswordGenerator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Transactional
public class AdminService {

    private final AdminMapper adminMapper;
    private final AdminRepository adminRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailOtpService emailOtpService;

    public AdminDto inviteAdmin(InvitePortalAdminRequest request) {
        validateEmailUniqueness(request.getEmail(), null);

        Set<String> normalizedPermissions = normalizePermissions(request.getPermissions());
        String randomPassword = PasswordGenerator.generateRandomPassword(12);

        Role baseAdminRole = roleRepository.findByName(RoleName.ADMIN.name())
                .orElseThrow(() -> new BusinessException(ErrorMessage.INVALID_ROLE));
        Role scopedAdminRole = buildScopedAdminRole(request.getEmail(), normalizedPermissions);

        User user = new User();
        user.setEmail(request.getEmail().trim());
        user.setPassword(passwordEncoder.encode(randomPassword));
        user.setEnabled(true);
        user.getRoles().add(baseAdminRole);
        user.getRoles().add(scopedAdminRole);
        User savedUser = userRepository.save(user);

        Admin admin = new Admin();
        admin.setFullName(request.getFullName().trim());
        admin.setUser(savedUser);
        savedUser.setAdmin(admin);

        Admin savedAdmin = adminRepository.save(admin);
        savedUser.setAdmin(savedAdmin);

        emailOtpService.sendPortalAdminInvitation(
                savedUser.getEmail(),
                savedAdmin.getFullName(),
                randomPassword,
                normalizedPermissions
        );

        return adminMapper.toDto(savedAdmin);
    }

    public PortalAdminListResponse updateAdmin(Long adminId, UpdatePortalAdminRequest request) {
        Admin admin = adminRepository.findById(adminId)
                .orElseThrow(() -> new BusinessException(ErrorMessage.ADMIN_NOT_FOUND));

        validateEmailUniqueness(request.getEmail(), admin.getUser().getId());

        admin.setFullName(request.getFullName().trim());
        admin.getUser().setEmail(request.getEmail().trim());

        Set<String> normalizedPermissions = normalizePermissions(request.getPermissions());
        upsertScopedAdminRole(admin.getUser(), normalizedPermissions);

        userRepository.save(admin.getUser());
        return mapAdminListResponse(admin);
    }

    public PortalAdminDetailsResponse updateMyProfile(Long adminId, UpdatePortalAdminProfileRequest request) {
        Admin admin = adminRepository.findById(adminId)
                .orElseThrow(() -> new BusinessException(ErrorMessage.ADMIN_NOT_FOUND));

        if (admin.getUser() == null) {
            throw new BusinessException(ErrorMessage.USER_NOT_FOUND);
        }

        validateEmailUniqueness(request.getEmail(), admin.getUser().getId());

        admin.setFullName(request.getFullName().trim());
        admin.getUser().setEmail(request.getEmail().trim());

        if (hasAnyPasswordInput(request)) {
            updatePassword(admin.getUser(), request);
        }

        adminRepository.save(admin);
        userRepository.save(admin.getUser());
        return mapAdminDetailsResponse(admin);
    }

    public PortalAdminDetailsResponse getAdminDetails(Long adminId) {
        Admin admin = adminRepository.findById(adminId)
                .orElseThrow(() -> new BusinessException(ErrorMessage.ADMIN_NOT_FOUND));
        return mapAdminDetailsResponse(admin);
    }

    public PortalAdminDetailsResponse getMyProfile(Long adminId) {
        return getAdminDetails(adminId);
    }

    public PortalAdminDetailsResponse enableAdmin(Long adminId) {
        Admin admin = adminRepository.findById(adminId)
                .orElseThrow(() -> new BusinessException(ErrorMessage.ADMIN_NOT_FOUND));

        if (admin.getUser() == null) {
            throw new BusinessException(ErrorMessage.USER_NOT_FOUND);
        }

        admin.getUser().setEnabled(true);
        userRepository.save(admin.getUser());
        return mapAdminDetailsResponse(admin);
    }

    public PortalAdminDetailsResponse disableAdmin(Long adminId) {
        Admin admin = adminRepository.findById(adminId)
                .orElseThrow(() -> new BusinessException(ErrorMessage.ADMIN_NOT_FOUND));

        if (admin.getUser() == null) {
            throw new BusinessException(ErrorMessage.USER_NOT_FOUND);
        }

        admin.getUser().setEnabled(false);
        userRepository.save(admin.getUser());
        return mapAdminDetailsResponse(admin);
    }

    public String resetAdminPassword(Long adminId) {
        Admin admin = adminRepository.findById(adminId)
                .orElseThrow(() -> new BusinessException(ErrorMessage.ADMIN_NOT_FOUND));

        if (admin.getUser() == null) {
            throw new BusinessException(ErrorMessage.USER_NOT_FOUND);
        }

        String newPassword = PasswordGenerator.generateRandomPassword(12);
        admin.getUser().setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(admin.getUser());

        emailOtpService.sendPortalAdminPasswordReset(
                admin.getUser().getEmail(),
                admin.getFullName(),
                admin.getUser().getEmail(),
                newPassword
        );

        return "Admin password has been reset successfully";
    }

    public ApiResponse<Void> deleteAdmin(Long adminId) {
        Admin admin = adminRepository.findById(adminId)
                .orElseThrow(() -> new BusinessException(ErrorMessage.ADMIN_NOT_FOUND));

        if (admin.getUser() != null) {
            admin.getUser().setEnabled(false);
            userRepository.save(admin.getUser());
        }

        adminRepository.softDeleteById(adminId);
        return new ApiResponse<>((Void) null);
    }

    public ApiResponse<List<PortalAdminListResponse>> findAllForAdmin(String search,
                                                                      Integer pageIndex,
                                                                      Integer pageSize,
                                                                      String sortBy,
                                                                      String sortDir) {
        String normalizedSearch = search == null || search.trim().isEmpty() ? null : search.trim();

        ApiRequest<Void> apiRequest = ApiRequest.<Void>builder()
                .pageIndex(pageIndex == null ? 0 : pageIndex)
                .pageSize(pageSize == null ? 20 : pageSize)
                .sortBy(sortBy)
                .sortDir(sortDir)
                .build();

        Page<Admin> page = normalizedSearch == null
                ? adminRepository.findAll(apiRequest.buildPagination())
                : adminRepository.searchAdmins(normalizedSearch, apiRequest.buildPagination());
        List<PortalAdminListResponse> items = page.getContent().stream()
                .map(this::mapAdminListResponse)
                .toList();

        return new ApiResponse<>(items, page.getTotalElements(), page.getTotalPages(), page.getSize(), page.getNumber());
    }

    private Set<String> normalizePermissions(List<PortalAdminPermission> permissions) {
        Set<String> normalizedPermissions = permissions.stream()
                .map(Enum::name)
                .collect(Collectors.toCollection(LinkedHashSet::new));

        if (normalizedPermissions.isEmpty()) {
            throw new BusinessException(ErrorMessage.PERMISSIONS_IS_REQUIRED);
        }

        return normalizedPermissions;
    }

    private Role buildScopedAdminRole(String email, Set<String> normalizedPermissions) {
        Role role = new Role();
        role.setName(buildScopedRoleName(email));
        role.setDescription("Scoped portal admin permissions for " + email.trim());
        role.setPermissions(resolvePermissions(normalizedPermissions));
        return roleRepository.save(role);
    }

    private void upsertScopedAdminRole(User user, Set<String> normalizedPermissions) {
        Optional<Role> existingScopedRole = user.getRoles().stream()
                .filter(role -> role.getName() != null && role.getName().startsWith("ADMIN_SCOPE_"))
                .findFirst();

        if (existingScopedRole.isPresent()) {
            Role scopedRole = existingScopedRole.get();
            scopedRole.setDescription("Scoped portal admin permissions for " + user.getEmail().trim());
            scopedRole.setPermissions(resolvePermissions(normalizedPermissions));
            roleRepository.save(scopedRole);
            return;
        }

        user.getRoles().add(buildScopedAdminRole(user.getEmail(), normalizedPermissions));
    }

    private Set<Permission> resolvePermissions(Set<String> normalizedPermissions) {
        List<Permission> existingPermissions = permissionRepository.findByNameIn(normalizedPermissions);
        Set<String> existingPermissionNames = existingPermissions.stream()
                .map(Permission::getName)
                .collect(Collectors.toSet());

        List<Permission> newPermissions = normalizedPermissions.stream()
                .filter(permission -> !existingPermissionNames.contains(permission))
                .map(permissionName -> {
                    Permission permission = new Permission();
                    permission.setName(permissionName);
                    return permission;
                })
                .toList();

        List<Permission> persistedNewPermissions = permissionRepository.saveAll(newPermissions);
        Set<Permission> rolePermissions = new LinkedHashSet<>(existingPermissions);
        rolePermissions.addAll(persistedNewPermissions);
        return rolePermissions;
    }

    private String buildScopedRoleName(String email) {
        String sanitizedEmail = email == null ? "ADMIN" : email.replaceAll("[^A-Za-z0-9]", "").toUpperCase(Locale.ROOT);
        if (sanitizedEmail.isBlank()) {
            sanitizedEmail = "ADMIN";
        }

        String suffix = sanitizedEmail.length() > 20 ? sanitizedEmail.substring(0, 20) : sanitizedEmail;
        return "ADMIN_SCOPE_" + suffix + System.currentTimeMillis();
    }

    private PortalAdminListResponse mapAdminListResponse(Admin admin) {
        return new PortalAdminListResponse(
                admin.getId(),
                admin.getFullName(),
                admin.getUser() != null ? admin.getUser().getEmail() : null,
                admin.getUser() != null ? admin.getUser().isEnabled() : null,
                mapPermissions(admin)
        );
    }

    private PortalAdminDetailsResponse mapAdminDetailsResponse(Admin admin) {
        return new PortalAdminDetailsResponse(
                admin.getId(),
                admin.getFullName(),
                admin.getUser() != null ? admin.getUser().getEmail() : null,
                admin.getUser() != null ? admin.getUser().isEnabled() : null,
                mapPermissions(admin),
                admin.getCreatedDate(),
                admin.getCreatedBy(),
                admin.getUpdatedDate(),
                admin.getUpdatedBy()
        );
    }

    private List<PortalAdminPermissionResponse> mapPermissions(Admin admin) {
        if (admin.getUser() == null) {
            return List.of();
        }

        return admin.getUser().getRoles().stream()
                .flatMap(role -> role.getPermissions().stream())
                .map(Permission::getName)
                .distinct()
                .map(this::mapPermissionResponse)
                .filter(permission -> permission != null)
                .toList();
    }

    private PortalAdminPermissionResponse mapPermissionResponse(String permissionName) {
        try {
            PortalAdminPermission permission = PortalAdminPermission.valueOf(permissionName);
            return new PortalAdminPermissionResponse(
                    permission.name(),
                    permission.getNameEn(),
                    permission.getNameAr()
            );
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }

    private void validateEmailUniqueness(String email, Long currentUserId) {
        userRepository.findByEmail(email.trim())
                .filter(existingUser -> currentUserId == null || !existingUser.getId().equals(currentUserId))
                .ifPresent(existingUser -> {
                    throw new BusinessException(ErrorMessage.EMAIL_ALREADY_EXIST);
                });
    }

    private boolean hasAnyPasswordInput(UpdatePortalAdminProfileRequest request) {
        return hasText(request.getCurrentPassword())
                || hasText(request.getNewPassword())
                || hasText(request.getConfirmPassword());
    }

    private void updatePassword(User user, UpdatePortalAdminProfileRequest request) {
        if (!hasText(request.getCurrentPassword())
                || !hasText(request.getNewPassword())
                || !hasText(request.getConfirmPassword())) {
            throw new BusinessException(ErrorMessage.MISSING_REQUIRED_ATTRIBUTES);
        }

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new BusinessException(ErrorMessage.INVALID_CURRENT_PASSWORD);
        }

        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new BusinessException(ErrorMessage.PASSWORDS_DO_NOT_MATCH);
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
    }

    private boolean hasText(String value) {
        return value != null && !value.trim().isEmpty();
    }
}
