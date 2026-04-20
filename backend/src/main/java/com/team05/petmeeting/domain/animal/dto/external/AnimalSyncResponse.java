package com.team05.petmeeting.domain.animal.dto.external;

public record AnimalSyncResponse(
        String message,
        int savedCount,
        long elapsedMs
) {
}
