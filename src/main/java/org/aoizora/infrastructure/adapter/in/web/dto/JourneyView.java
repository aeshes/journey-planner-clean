package org.aoizora.infrastructure.adapter.in.web.dto;

import java.util.List;

public record JourneyView(List<Leg> legs, String origin, String destination, long totalSeconds) {
    public record Leg(String from, String to, String mode, long seconds, double meters) {}
}