package com.elabbasy.coatchinghub.model.request;

import com.elabbasy.coatchinghub.model.enums.SlotType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateCoachSlot {

    @NotNull(message = "START_TIME_REQUIRED")
    private OffsetDateTime startTime;
    @NotNull(message = "PERIOD_IS_REQUIRED")
    private SlotType slotType;
}
