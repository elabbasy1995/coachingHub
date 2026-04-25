package com.elabbasy.coatchinghub.mapper;

import com.elabbasy.coatchinghub.model.dto.AdminDto;
import com.elabbasy.coatchinghub.model.dto.UserDto;
import com.elabbasy.coatchinghub.model.entity.Admin;
import com.elabbasy.coatchinghub.model.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AdminMapper extends BaseMapper<AdminDto, AdminDto, Admin> {

    @Mapping(target = "admin", ignore = true)
    @Mapping(target = "coachee", ignore = true)
    @Mapping(target = "coach", ignore = true)
    UserDto userToUserDto(User user);
}
