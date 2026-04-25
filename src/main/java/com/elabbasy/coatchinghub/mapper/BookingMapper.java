package com.elabbasy.coatchinghub.mapper;

import com.elabbasy.coatchinghub.model.dto.BookingDto;
import com.elabbasy.coatchinghub.model.dto.CoachDto;
import com.elabbasy.coatchinghub.model.dto.CoacheeDto;
import com.elabbasy.coatchinghub.model.entity.Booking;
import com.elabbasy.coatchinghub.model.entity.Coach;
import com.elabbasy.coatchinghub.model.entity.Coachee;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BookingMapper extends BaseMapper<BookingDto, BookingDto, Booking> {

    @Mapping(target = "coachingIndustries", ignore = true)
    @Mapping(target = "user", ignore = true)
    CoachDto toCoachDto(Coach coach);

    @Mapping(target = "user", ignore = true)
    CoacheeDto toCoacheeDto(Coachee coachee);


}
