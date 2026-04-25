package com.elabbasy.coatchinghub.controller.portal;

import com.elabbasy.coatchinghub.constant.PortalPermissionExpressions;
import com.elabbasy.coatchinghub.model.CountryDto;
import com.elabbasy.coatchinghub.model.NationalityDto;
import com.elabbasy.coatchinghub.model.dto.CoachingIndustryDto;
import com.elabbasy.coatchinghub.model.dto.LanguageDto;
import com.elabbasy.coatchinghub.model.enums.SlotType;
import com.elabbasy.coatchinghub.model.response.ApiResponse;
import com.elabbasy.coatchinghub.model.response.PortalAdminPermissionResponse;
import com.elabbasy.coatchinghub.model.response.SlotTypeResponse;
import com.elabbasy.coatchinghub.service.LookupService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/portal/api/lookup")
@RequiredArgsConstructor
@Tag(name = "Portal Lookups")
public class PortalLookupController {

    private final LookupService lookupService;

    @GetMapping("/coaching-industries")
    @PreAuthorize(PortalPermissionExpressions.COACHES)
    public ApiResponse<List<CoachingIndustryDto>> getCoachingIndustriesLookup() {
        return new ApiResponse<>(lookupService.getCoachingIndustriesLookup());
    }

    @GetMapping("/languages")
    @PreAuthorize(PortalPermissionExpressions.COACHES)
    public ApiResponse<List<LanguageDto>> getLanguages() {
        return new ApiResponse<>(lookupService.getLanguageLookup());
    }

    @GetMapping("/countries")
    @PreAuthorize(PortalPermissionExpressions.COACHES)
    public ApiResponse<List<CountryDto>> getCountries() {
        return new ApiResponse<>(lookupService.getCountryLookup());
    }

    @GetMapping("/nationalities")
    @PreAuthorize(PortalPermissionExpressions.COACHES)
    public ApiResponse<List<NationalityDto>> getNationalities() {
        return new ApiResponse<>(lookupService.getNationalityLookup());
    }

    @GetMapping("/slot-types")
    @PreAuthorize(PortalPermissionExpressions.COACHES_OR_BOOKING)
    public ApiResponse<List<SlotTypeResponse>> getSlotTypes() {
        List<SlotTypeResponse> list = Arrays.stream(SlotType.values())
                .map(type -> new SlotTypeResponse(
                        type.name(),
                        type.getNameEn(),
                        type.getNameAr(),
                        type.getDuration()
                ))
                .toList();

        return new ApiResponse<>(list);
    }

    @GetMapping("/portal-admin-permissions")
    @PreAuthorize(PortalPermissionExpressions.ADMINS)
    public ApiResponse<List<PortalAdminPermissionResponse>> getPortalAdminPermissions() {
        return new ApiResponse<>(lookupService.getPortalAdminPermissionsLookup());
    }
}
