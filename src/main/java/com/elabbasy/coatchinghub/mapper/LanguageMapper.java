package com.elabbasy.coatchinghub.mapper;

import com.elabbasy.coatchinghub.model.dto.LanguageDto;
import com.elabbasy.coatchinghub.model.entity.Language;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface LanguageMapper extends BaseMapper<LanguageDto, LanguageDto, Language> {
}
