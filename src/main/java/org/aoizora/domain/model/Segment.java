package org.aoizora.domain.model;

import java.time.Duration;

public record Segment(
        Stop from,
        Stop to,
        TransportMode mode,
        Duration duration,
        double distanceMeters
) {}
