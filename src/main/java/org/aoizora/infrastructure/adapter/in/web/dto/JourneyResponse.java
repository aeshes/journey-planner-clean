package org.aoizora.infrastructure.adapter.in.web.dto;

import java.util.List;

public record JourneyResponse(List<LegDto> legs, long totalSeconds) {
    public record LegDto(String from, String to, String mode, long seconds, double meters) {}
}