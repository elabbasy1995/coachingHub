package com.elabbasy.coatchinghub.mapper;

import com.elabbasy.coatchinghub.model.CountryDto;
import com.elabbasy.coatchinghub.model.entity.Country;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CountryMapper extends BaseMapper<CountryDto, CountryDto, Country> {
}
