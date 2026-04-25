package com.elabbasy.coatchinghub.mapper;

import com.elabbasy.coatchinghub.model.dto.CoachingIndustryDto;
import com.elabbasy.coatchinghub.model.entity.CoachingIndustry;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CoachingIndustriesMapper extends BaseMapper<CoachingIndustryDto, CoachingIndustryDto, CoachingIndustry> {
}
