package com.elabbasy.coatchinghub.mapper;

import java.util.List;

public interface BaseMapper<Request, Dto, Entity> {

    Dto toDto(Entity entity);
    Entity toEntity(Dto dto);
    Dto toDtoFromRequest(Request request);
    List<Dto> toDtoList(List<Entity> entities);
    List<Entity> toEntityList(List<Dto> dtos);
}
