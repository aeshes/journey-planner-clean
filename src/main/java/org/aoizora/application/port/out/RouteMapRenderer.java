package org.aoizora.application.port.out;

import org.aoizora.domain.model.Journey;
import org.aoizora.domain.model.Segment;
import org.aoizora.domain.model.Stop;

import java.util.List;

public interface RouteMapRenderer {
    String render(List<Stop> stops, List<Segment> segments, Journey journey);
}