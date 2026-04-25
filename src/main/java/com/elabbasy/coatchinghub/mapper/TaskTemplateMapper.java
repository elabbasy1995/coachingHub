package com.elabbasy.coatchinghub.mapper;

import com.elabbasy.coatchinghub.model.dto.CoachDto;
import com.elabbasy.coatchinghub.model.dto.TaskTemplateDto;
import com.elabbasy.coatchinghub.model.entity.Coach;
import com.elabbasy.coatchinghub.model.entity.TaskQuestion;
import com.elabbasy.coatchinghub.model.entity.TaskTemplate;
import com.elabbasy.coatchinghub.model.response.TaskAssignmentDetailsResponse;
import com.elabbasy.coatchinghub.model.response.TaskTemplateResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TaskTemplateMapper extends BaseMapper<TaskTemplateDto, TaskTemplateDto, TaskTemplate> {

    @Mapping(target = "coachingIndustries", ignore = true)
    @Mapping(target = "user", ignore = true)
    CoachDto toCoachDto(Coach coach);

    TaskTemplateResponse toTaskTemplateResponse(TaskTemplate taskTemplate);

    List<TaskTemplateResponse> toTaskTemplateResponseList(List<TaskTemplate> taskTemplates);

    TaskAssignmentDetailsResponse.TaskQuestionDetails toTaskQuestionDetails(TaskQuestion taskQuestion);

}
