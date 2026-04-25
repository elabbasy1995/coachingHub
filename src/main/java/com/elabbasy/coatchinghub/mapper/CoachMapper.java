package com.elabbasy.coatchinghub.mapper;

import com.elabbasy.coatchinghub.model.dto.CoachDto;
import com.elabbasy.coatchinghub.model.dto.UserDto;
import com.elabbasy.coatchinghub.model.entity.Coach;
import com.elabbasy.coatchinghub.model.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CoachMapper extends BaseMapper<CoachDto, CoachDto, Coach>{

    @Mapping(target = "admin", ignore = true)
    @Mapping(target = "coachee", ignore = true)
    @Mapping(target = "coach", ignore = true)
    UserDto userToUserDto(User user);
}
