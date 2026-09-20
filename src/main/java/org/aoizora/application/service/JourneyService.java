package org.aoizora.application.service;

import org.aoizora.application.port.in.PlanJourneyUseCase;
import org.aoizora.application.port.out.RouteFinder;
import org.aoizora.application.port.out.TransportNetworkRepository;
import org.aoizora.domain.exception.RouteNotFoundException;
import org.aoizora.domain.model.Journey;
import org.aoizora.domain.model.OptimizationCriteria;
import org.aoizora.domain.model.Stop;

public class JourneyService implements PlanJourneyUseCase {

    private final TransportNetworkRepository networkRepository;
    private final RouteFinder routeFinder;

    public JourneyService(TransportNetworkRepository networkRepository, RouteFinder routeFinder) {
        this.networkRepository = networkRepository;
        this.routeFinder = routeFinder;
    }

    @Override
    public Journey plan(long fromStopId, long toStopId, OptimizationCriteria criteria) {
        Stop from = networkRepository.findStopById(fromStopId)
                .orElseThrow(() -> new IllegalArgumentException("Unknown stop: " + fromStopId));
        Stop to = networkRepository.findStopById(toStopId)
                .orElseThrow(() -> new IllegalArgumentException("Unknown stop: " + toStopId));

        if (from.id() == to.id()) {
            throw new IllegalArgumentException("Origin and destination must differ");
        }

        return routeFinder.findRoute(networkRepository.findAllStops(), networkRepository.findAllSegments(), from, to, criteria)
                .orElseThrow(() -> new RouteNotFoundException(fromStopId, toStopId));
    }
}
