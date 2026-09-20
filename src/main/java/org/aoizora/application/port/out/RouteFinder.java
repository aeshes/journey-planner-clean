package org.aoizora.application.port.out;

import org.aoizora.domain.model.Journey;
import org.aoizora.domain.model.OptimizationCriteria;
import org.aoizora.domain.model.Segment;
import org.aoizora.domain.model.Stop;

import java.util.List;
import java.util.Optional;

public interface RouteFinder {
    Optional<Journey> findRoute(List<Stop> stops, List<Segment> segments, Stop from, Stop to, OptimizationCriteria criteria);
}
