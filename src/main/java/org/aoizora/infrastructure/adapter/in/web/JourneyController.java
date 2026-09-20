package org.aoizora.infrastructure.adapter.in.web;

import org.aoizora.application.port.in.PlanJourneyUseCase;
import org.aoizora.domain.model.Journey;
import org.aoizora.domain.model.OptimizationCriteria;
import org.aoizora.infrastructure.adapter.in.web.dto.JourneyRequest;
import org.aoizora.infrastructure.adapter.in.web.dto.JourneyResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/journey")
public class JourneyController {

    private final PlanJourneyUseCase planJourney;

    public JourneyController(PlanJourneyUseCase planJourney) {
        this.planJourney = planJourney;
    }

    @PostMapping
    public JourneyResponse plan(@RequestBody JourneyRequest request) {
        OptimizationCriteria criteria = OptimizationCriteria.valueOf(request.criteria() == null ? "FASTEST" : request.criteria().toUpperCase());
        Journey journey = planJourney.plan(request.fromStopId(), request.toStopId(), criteria);
        return toResponse(journey);
    }

    private JourneyResponse toResponse(Journey journey) {
        var legs = journey.segments().stream()
                .map(s -> new JourneyResponse.LegDto(s.from().name(), s.to().name(), s.mode().name(), s.duration().toSeconds(), s.distanceMeters()))
                .toList();
        return new JourneyResponse(legs, journey.totalDuration().toSeconds());
    }
}
