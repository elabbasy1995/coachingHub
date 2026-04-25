package com.elabbasy.coatchinghub.mapper;

import com.elabbasy.coatchinghub.model.dto.CoacheeDto;
import com.elabbasy.coatchinghub.model.dto.UserDto;
import com.elabbasy.coatchinghub.model.entity.Coachee;
import com.elabbasy.coatchinghub.model.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CoacheeMapper extends BaseMapper<CoacheeDto, CoacheeDto, Coachee> {

    @Mapping(target = "admin", ignore = true)
    @Mapping(target = "coachee", ignore = true)
    @Mapping(target = "coach", ignore = true)
    UserDto userToUserDto(User user);
}
