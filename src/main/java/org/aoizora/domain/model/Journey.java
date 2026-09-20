package org.aoizora.domain.model;

import java.time.Duration;
import java.util.List;

public record Journey(List<Segment> segments, Duration totalDuration) {

    public Journey {
        if (segments == null || segments.isEmpty()) {
            throw new IllegalArgumentException("Journey must have at least one segment");
        }
        segments = List.copyOf(segments);
    }

    public Stop origin() {
        return segments.get(0).from();
    }

    public Stop destination() {
        return segments.get(segments.size() - 1).to();
    }
}
