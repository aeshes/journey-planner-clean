package org.aoizora.infrastructure.adapter.out.persistence;

import org.aoizora.application.port.out.TransportNetworkRepository;
import org.aoizora.domain.model.Segment;
import org.aoizora.domain.model.Stop;
import org.aoizora.domain.model.TransportMode;
import org.springframework.jdbc.core.JdbcTemplate;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

public class JdbcTransportNetworkRepository implements TransportNetworkRepository {

    private static final double WALK_SPEED_KMH = 5.0;

    private final JdbcTemplate jdbc;

    public JdbcTransportNetworkRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public Optional<Stop> findStopById(long id) {
        return jdbc.query("SELECT id, name, latitude, longitude FROM settlement WHERE id = ?", rs -> rs.next() ?
                        Optional.of(new Stop(rs.getLong("id"),
                        rs.getString("name"),
                        rs.getDouble("latitude"),
                        rs.getDouble("longitude"))) : Optional.empty(), id);
    }

    @Override
    public List<Stop> findAllStops() {
        return jdbc.query("SELECT id, name, latitude, longitude FROM settlement", (rs, rowNum) -> new Stop(rs.getLong("id"),
                        rs.getString("name"),
                        rs.getDouble("latitude"),
                        rs.getDouble("longitude")));
    }

    @Override
    public List<Segment> findAllSegments() {
        return jdbc.query("""
                        SELECT r.id, r.id_from, r.id_to, r.distance,
                               s1.name AS from_name, s1.latitude AS from_lat, s1.longitude AS from_lon,
                               s2.name AS to_name,   s2.latitude AS to_lat,   s2.longitude AS to_lon,
                               rt.transport_type, rt.speed_kmh
                        FROM road r
                        JOIN settlement s1 ON s1.id = r.id_from
                        JOIN settlement s2 ON s2.id = r.id_to
                        LEFT JOIN road_transport rt ON rt.road_id = r.id
                        """, (rs, rowNum) -> {
                    Stop from = new Stop(rs.getLong("id_from"),
                            rs.getString("from_name"),
                            rs.getDouble("from_lat"),
                            rs.getDouble("from_lon"));
                    Stop to = new Stop(rs.getLong("id_to"),
                            rs.getString("to_name"),
                            rs.getDouble("to_lat"),
                            rs.getDouble("to_lon"));
                    double meters = rs.getDouble("distance") * 1000.0;
                    TransportMode mode = TransportMode.fromName(rs.getString("transport_type"));
                    double speedKmh = rs.getDouble("speed_kmh") > 0 ? rs.getDouble("speed_kmh") : WALK_SPEED_KMH;
                    long seconds = Math.round(meters / (speedKmh * 1000.0 / 3600.0));
                    return new Segment(from, to, mode, Duration.ofSeconds(seconds), meters);
                });
    }
}
