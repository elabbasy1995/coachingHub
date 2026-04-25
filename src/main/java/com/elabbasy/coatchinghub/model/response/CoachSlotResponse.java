package com.elabbasy.coatchinghub.model.response;

import com.elabbasy.coatchinghub.model.dto.AuditBaseDto;
import com.elabbasy.coatchinghub.model.dto.CoachDto;
import com.elabbasy.coatchinghub.model.enums.SlotStatus;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CoachSlotResponse {
    private Long id;
    private OffsetDateTime startTimeUtc;
    private OffsetDateTime endTimeUtc;
    private Integer periodMinutes;
    private SlotStatus status;

}
