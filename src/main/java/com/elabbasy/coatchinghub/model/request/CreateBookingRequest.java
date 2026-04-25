package com.elabbasy.coatchinghub.model.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateBookingRequest {

    @NotNull(message = "COACH_IS_REQUIRED")
    private Long coachId;
    @NotNull(message = "SLOT_IS_REQUIRED")
    private Long coachSlotId;
}
