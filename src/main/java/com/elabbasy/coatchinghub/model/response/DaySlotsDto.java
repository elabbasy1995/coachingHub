package com.elabbasy.coatchinghub.model.response;

import com.elabbasy.coatchinghub.model.dto.CoachSlotDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DaySlotsDto {
    private LocalDate date;               // The day
    private List<CoachSlotResponse> slots;
}