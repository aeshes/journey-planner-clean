package org.aoizora.application.port.in;

import org.aoizora.domain.model.Segment;
import org.aoizora.domain.model.Stop;

import java.util.List;

public interface NetworkQueryUseCase {
    List<Stop> findAllStops();
    List<Segment> findAllSegments();
}