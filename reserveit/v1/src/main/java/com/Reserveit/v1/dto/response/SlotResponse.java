package com.Reserveit.v1.dto.response;

import java.time.LocalTime;

public record SlotResponse(
        LocalTime startTime,
        LocalTime endTime,
        boolean available
) {
}
