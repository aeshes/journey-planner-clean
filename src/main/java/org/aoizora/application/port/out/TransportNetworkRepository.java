package org.aoizora.application.port.out;

import org.aoizora.domain.model.Segment;
import org.aoizora.domain.model.Stop;

import java.util.List;
import java.util.Optional;

public interface TransportNetworkRepository {
    Optional<Stop> findStopById(long id);
    List<Stop> findAllStops();
    List<Segment> findAllSegments();
}
