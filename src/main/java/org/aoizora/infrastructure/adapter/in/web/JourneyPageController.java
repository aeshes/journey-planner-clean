package org.aoizora.infrastructure.adapter.in.web;

import org.aoizora.application.port.in.NetworkQueryUseCase;
import org.aoizora.application.port.in.PlanJourneyUseCase;
import org.aoizora.application.port.out.RouteMapRenderer;
import org.aoizora.domain.exception.RouteNotFoundException;
import org.aoizora.domain.model.Journey;
import org.aoizora.domain.model.OptimizationCriteria;
import org.aoizora.infrastructure.adapter.in.web.dto.JourneyView;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Controller
public class JourneyPageController {

    private final PlanJourneyUseCase planJourney;
    private final NetworkQueryUseCase networkQuery;
    private final RouteMapRenderer routeMapRenderer;

    public JourneyPageController(PlanJourneyUseCase planJourney,
                                 NetworkQueryUseCase networkQuery,
                                 RouteMapRenderer routeMapRenderer) {
        this.planJourney = planJourney;
        this.networkQuery = networkQuery;
        this.routeMapRenderer = routeMapRenderer;
    }

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("stops", networkQuery.findAllStops());
        model.addAttribute("selectedCriteria", "FASTEST");
        return "index";
    }

    @PostMapping("/journey")
    public String plan(@RequestParam("fromStopId") long fromStopId,
                       @RequestParam("toStopId") long toStopId,
                       @RequestParam(value = "criteria", defaultValue = "FASTEST") String criteria,
                       Model model) {
        try {
            OptimizationCriteria optimizationCriteria = OptimizationCriteria.valueOf(criteria.toUpperCase());
            Journey journey = planJourney.plan(fromStopId, toStopId, optimizationCriteria);
            String svg = routeMapRenderer.render(networkQuery.findAllStops(), networkQuery.findAllSegments(), journey);
            model.addAttribute("mapDataUri", toDataUri(svg));
            model.addAttribute("journey", toView(journey));
        } catch (IllegalArgumentException | RouteNotFoundException ex) {
            model.addAttribute("error", ex.getMessage());
        }
        model.addAttribute("stops", networkQuery.findAllStops());
        model.addAttribute("selectedFrom", fromStopId);
        model.addAttribute("selectedTo", toStopId);
        model.addAttribute("selectedCriteria", criteria.toUpperCase());
        return "index";
    }

    private String toDataUri(String svg) {
        return "data:image/svg+xml;base64," + Base64.getEncoder().encodeToString(svg.getBytes(StandardCharsets.UTF_8));
    }

    private JourneyView toView(Journey journey) {
        var legs = journey.segments().stream()
                .map(s -> new JourneyView.Leg(s.from().name(), s.to().name(), s.mode().name(), s.duration().toSeconds(), s.distanceMeters()))
                .toList();
        return new JourneyView(legs, journey.origin().name(), journey.destination().name(), journey.totalDuration().toSeconds());
    }
}