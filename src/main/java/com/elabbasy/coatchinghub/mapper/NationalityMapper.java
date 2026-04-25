package com.elabbasy.coatchinghub.mapper;

import com.elabbasy.coatchinghub.model.NationalityDto;
import com.elabbasy.coatchinghub.model.entity.Nationality;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface NationalityMapper extends BaseMapper<NationalityDto, NationalityDto, Nationality> {
}
