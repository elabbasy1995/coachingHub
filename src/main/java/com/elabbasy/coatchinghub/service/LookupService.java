package com.elabbasy.coatchinghub.service;

import com.elabbasy.coatchinghub.mapper.CoachingIndustriesMapper;
import com.elabbasy.coatchinghub.mapper.CountryMapper;
import com.elabbasy.coatchinghub.mapper.LanguageMapper;
import com.elabbasy.coatchinghub.mapper.NationalityMapper;
import com.elabbasy.coatchinghub.model.CountryDto;
import com.elabbasy.coatchinghub.model.NationalityDto;
import com.elabbasy.coatchinghub.model.dto.CoachingIndustryDto;
import com.elabbasy.coatchinghub.model.dto.LanguageDto;
import com.elabbasy.coatchinghub.model.entity.CoachingIndustry;
import com.elabbasy.coatchinghub.model.entity.Country;
import com.elabbasy.coatchinghub.model.entity.Language;
import com.elabbasy.coatchinghub.model.entity.Nationality;
import com.elabbasy.coatchinghub.model.enums.PortalAdminPermission;
import com.elabbasy.coatchinghub.model.response.PortalAdminPermissionResponse;
import com.elabbasy.coatchinghub.repository.CoachingIndustryRepository;
import com.elabbasy.coatchinghub.repository.CountryRepository;
import com.elabbasy.coatchinghub.repository.LanguageRepository;
import com.elabbasy.coatchinghub.repository.NationalityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class LookupService {

    private final CoachingIndustryRepository coachingIndustryRepository;
    private final CoachingIndustriesMapper coachingIndustriesMapper;
    private final LanguageRepository languageRepository;
    private final LanguageMapper languageMapper;
    private final CountryMapper countryMapper;
    private final CountryRepository countryRepository;
    private final NationalityMapper nationalityMapper;
    private final NationalityRepository nationalityRepository;


    public List<CountryDto> getCountryLookup() {
        List<Country> countries = countryRepository.findByActiveTrueOrderByNameEnAsc();

        return countryMapper.toDtoList(countries);
    }

    public List<NationalityDto> getNationalityLookup() {
        List<Nationality> nationalities = nationalityRepository.findByActiveTrueOrderByNameEnAsc();

        return nationalityMapper.toDtoList(nationalities);
    }

    public List<CoachingIndustryDto> getCoachingIndustriesLookup() {
        List<CoachingIndustry> coachingIndustries = coachingIndustryRepository.findAll();

        return coachingIndustriesMapper.toDtoList(coachingIndustries);
    }

    public List<LanguageDto> getLanguageLookup() {
        List<Language> languages = languageRepository.findAll();

        return languageMapper.toDtoList(languages);
    }

    public List<PortalAdminPermissionResponse> getPortalAdminPermissionsLookup() {
        return List.of(PortalAdminPermission.values()).stream()
                .map(permission -> new PortalAdminPermissionResponse(
                        permission.name(),
                        permission.getNameEn(),
                        permission.getNameAr()
                ))
                .toList();
    }
}
