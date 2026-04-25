package com.elabbasy.coatchinghub.model.request;

import com.elabbasy.coatchinghub.model.enums.CoachStatus;
import com.elabbasy.coatchinghub.model.enums.Gender;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class CoachSearchCriteria {

    private String name;
    private Gender gender;
    private Integer minYearsOfExperience;
    private Set<Long> languageIds;
    private Set<Long> coachingIndustryIds;

}
