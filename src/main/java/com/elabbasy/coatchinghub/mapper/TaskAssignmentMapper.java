package com.elabbasy.coatchinghub.mapper;

import com.elabbasy.coatchinghub.model.dto.CoachDto;
import com.elabbasy.coatchinghub.model.dto.CoacheeDto;
import com.elabbasy.coatchinghub.model.dto.TaskAssignmentDto;
import com.elabbasy.coatchinghub.model.entity.Coach;
import com.elabbasy.coatchinghub.model.entity.Coachee;
import com.elabbasy.coatchinghub.model.entity.TaskAssignment;
import com.elabbasy.coatchinghub.model.response.TaskAssignmentSummaryResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TaskAssignmentMapper extends BaseMapper<TaskAssignmentDto, TaskAssignmentDto, TaskAssignment> {

    @Mapping(target = "coachingIndustries", ignore = true)
    @Mapping(target = "user", ignore = true)
    CoachDto toCoachDto(Coach coach);

    @Mapping(target = "user", ignore = true)
    CoacheeDto toCoacheeDto(Coachee coachee);

    @Mapping(target = "assignmentId", source = "id")
    @Mapping(target = "templateId", source = "taskTemplate.id")
    @Mapping(target = "title", source = "taskTemplate.title")
    @Mapping(target = "description", source = "taskTemplate.description")
    @Mapping(target = "status", expression = "java(entity.getStatus().name())")
    TaskAssignmentSummaryResponse toSummary(TaskAssignment entity);

    List<TaskAssignmentSummaryResponse> toSummaryList(List<TaskAssignment> taskAssignments);
}
