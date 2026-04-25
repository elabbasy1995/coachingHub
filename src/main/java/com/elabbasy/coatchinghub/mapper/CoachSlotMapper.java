package com.elabbasy.coatchinghub.mapper;

import com.elabbasy.coatchinghub.model.dto.CoachDto;
import com.elabbasy.coatchinghub.model.dto.CoachSlotDto;
import com.elabbasy.coatchinghub.model.entity.Coach;
import com.elabbasy.coatchinghub.model.entity.CoachSlot;
import com.elabbasy.coatchinghub.model.response.CoachSlotResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CoachSlotMapper extends BaseMapper<CoachSlotDto, CoachSlotDto, CoachSlot>{

    @Mapping(target = "coachingIndustries", ignore = true)
    @Mapping(target = "user", ignore = true)
    CoachDto toCoachDto(Coach coach);

    CoachSlotResponse toCoachSlotResponse(CoachSlot coachSlot);
}
