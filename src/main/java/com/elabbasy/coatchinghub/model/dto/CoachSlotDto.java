package com.elabbasy.coatchinghub.model.dto;

import com.elabbasy.coatchinghub.model.enums.SlotStatus;
import com.elabbasy.coatchinghub.model.enums.SlotType;
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
public class CoachSlotDto extends AuditBaseDto {

    @JsonIgnoreProperties("user")
    private CoachDto coach;
    private OffsetDateTime startTimeUtc;
    private OffsetDateTime endTimeUtc;
    private Integer periodMinutes;
    private SlotStatus status = SlotStatus.AVAILABLE;
    private SlotType slotType;
}