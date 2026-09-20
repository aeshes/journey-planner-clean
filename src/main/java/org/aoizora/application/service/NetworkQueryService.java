package org.aoizora.application.service;

import org.aoizora.application.port.in.NetworkQueryUseCase;
import org.aoizora.application.port.out.TransportNetworkRepository;
import org.aoizora.domain.model.Segment;
import org.aoizora.domain.model.Stop;

import java.util.List;

public class NetworkQueryService implements NetworkQueryUseCase {

    private final TransportNetworkRepository repository;

    public NetworkQueryService(TransportNetworkRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<Stop> findAllStops() {
        return repository.findAllStops();
    }

    @Override
    public List<Segment> findAllSegments() {
        return repository.findAllSegments();
    }
}