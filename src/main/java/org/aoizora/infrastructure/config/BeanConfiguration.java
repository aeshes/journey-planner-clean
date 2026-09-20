package org.aoizora.infrastructure.config;

import org.aoizora.application.port.in.PlanJourneyUseCase;
import org.aoizora.application.port.out.RouteFinder;
import org.aoizora.application.port.out.TransportNetworkRepository;
import org.aoizora.application.service.JourneyService;
import org.aoizora.infrastructure.adapter.out.persistence.JdbcTransportNetworkRepository;
import org.aoizora.infrastructure.adapter.out.routing.GraphRouteFinder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
public class BeanConfiguration {

    @Bean
    public TransportNetworkRepository transportNetworkRepository(JdbcTemplate jdbc) {
        return new JdbcTransportNetworkRepository(jdbc);
    }

    @Bean
    public RouteFinder routeFinder() {
        return new GraphRouteFinder();
    }

    @Bean
    public PlanJourneyUseCase planJourneyUseCase(TransportNetworkRepository repository,
                                                 RouteFinder routeFinder) {
        return new JourneyService(repository, routeFinder);
    }
}
