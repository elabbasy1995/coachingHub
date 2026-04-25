package com.elabbasy.coatchinghub.controller.mobile;

import com.elabbasy.coatchinghub.model.CountryDto;
import com.elabbasy.coatchinghub.model.NationalityDto;
import com.elabbasy.coatchinghub.model.dto.CoachingIndustryDto;
import com.elabbasy.coatchinghub.model.dto.LanguageDto;
import com.elabbasy.coatchinghub.model.enums.SlotType;
import com.elabbasy.coatchinghub.model.response.ApiResponse;
import com.elabbasy.coatchinghub.model.response.SlotTypeResponse;
import com.elabbasy.coatchinghub.service.LookupService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/mobile/api/lookup")
@RequiredArgsConstructor
@Tag(name = "Lookups")
public class MobileLookupController {

    private final LookupService lookupService;

    @GetMapping("/coaching-industries")
    public ApiResponse<List<CoachingIndustryDto>> getCoachingIndustriesLookup() {
        return new ApiResponse<>(lookupService.getCoachingIndustriesLookup());
    }

    @GetMapping("/languages")
    public ApiResponse<List<LanguageDto>> getLanguages() {
        return new ApiResponse<>(lookupService.getLanguageLookup());
    }

    @GetMapping("/countries")
    public ApiResponse<List<CountryDto>> getCountries() {
        return new ApiResponse<>(lookupService.getCountryLookup());
    }

    @GetMapping("/nationalities")
    public ApiResponse<List<NationalityDto>> getNationalities() {
        return new ApiResponse<>(lookupService.getNationalityLookup());
    }

    @GetMapping("/slot-types")
    public ApiResponse<List<SlotTypeResponse>> getSlotTypes() {
        List<SlotTypeResponse> list = Arrays.stream(SlotType.values())
                .map(type -> new SlotTypeResponse(
                        type.name(),        // enum constant (code)
                        type.getNameEn(),
                        type.getNameAr(),
                        type.getDuration()
                ))
                .toList();

        return new ApiResponse<>(list);
    }
}
