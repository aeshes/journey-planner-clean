package org.aoizora.infrastructure.adapter.in.web.dto;

public record JourneyRequest(long fromStopId, long toStopId, String criteria) {}
