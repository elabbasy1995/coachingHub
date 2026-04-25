package com.elabbasy.coatchinghub.mapper;

import com.elabbasy.coatchinghub.model.dto.CoachDto;
import com.elabbasy.coatchinghub.model.dto.CoacheeDto;
import com.elabbasy.coatchinghub.model.dto.NotificationDto;
import com.elabbasy.coatchinghub.model.entity.Coach;
import com.elabbasy.coatchinghub.model.entity.Coachee;
import com.elabbasy.coatchinghub.model.entity.Notification;
import com.elabbasy.coatchinghub.model.response.NotificationResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface NotificationMapper extends BaseMapper<NotificationDto, NotificationDto, Notification> {

    @Mapping(target = "coachingIndustries", ignore = true)
    @Mapping(target = "user", ignore = true)
    CoachDto toCoachDto(Coach coach);

    @Mapping(target = "user", ignore = true)
    CoacheeDto toCoacheeDto(Coachee coachee);

    NotificationResponse toNotificationResponse(Notification entity);

    List<NotificationResponse> toNotificationResponseList(List<Notification> notifications);
}
