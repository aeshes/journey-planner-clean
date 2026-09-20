package org.aoizora.application.port.in;

import org.aoizora.domain.model.Journey;
import org.aoizora.domain.model.OptimizationCriteria;

public interface PlanJourneyUseCase {
    Journey plan(long fromStopId, long toStopId, OptimizationCriteria criteria);
}
