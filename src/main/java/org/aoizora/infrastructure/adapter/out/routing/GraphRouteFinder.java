package org.aoizora.infrastructure.adapter.out.routing;

import org.aoizora.application.port.out.RouteFinder;
import org.aoizora.domain.model.Journey;
import org.aoizora.domain.model.OptimizationCriteria;
import org.aoizora.domain.model.Segment;
import org.aoizora.domain.model.Stop;

import java.time.Duration;
import java.util.*;

public class GraphRouteFinder implements RouteFinder {

    @Override
    public Optional<Journey> findRoute(List<Stop> stops, List<Segment> segments, Stop from, Stop to, OptimizationCriteria criteria) {
        Map<Stop, List<Segment>> graph = buildGraph(segments);
        Map<Stop, Segment> bestSegment = runDijkstra(graph, from, to, criteria);
        return buildJourney(bestSegment, to);
    }

    private Map<Stop, List<Segment>> buildGraph(List<Segment> segments) {
        Map<Stop, List<Segment>> graph = new HashMap<>();
        for (Segment segment : segments) {
            graph.computeIfAbsent(segment.from(), key -> new ArrayList<>()).add(segment);
            graph.computeIfAbsent(segment.to(), key -> new ArrayList<>()).add(segment);
        }
        return graph;
    }

    private Map<Stop, Segment> runDijkstra(Map<Stop, List<Segment>> graph, Stop from, Stop to, OptimizationCriteria criteria) {
        Map<Stop, Double> distances = new HashMap<>();
        Map<Stop, Segment> bestSegment = new HashMap<>();
        PriorityQueue<Stop> queue = new PriorityQueue<>(Comparator.comparingDouble(stop -> distances.getOrDefault(stop, Double.POSITIVE_INFINITY)));

        distances.put(from, 0.0);
        queue.add(from);

        while (!queue.isEmpty()) {
            Stop current = queue.poll();
            if (current.equals(to)) {
                break;
            }
            relax(current, graph.getOrDefault(current, List.of()), distances, bestSegment, queue, criteria);
        }
        return bestSegment;
    }

    private void relax(Stop current, List<Segment> segments, Map<Stop, Double> distances,
                       Map<Stop, Segment> bestSegment, PriorityQueue<Stop> queue, OptimizationCriteria criteria) {
        for (Segment segment : segments) {
            Stop destination = segment.from().equals(current) ? segment.to() : segment.from();
            double newDistance = distances.get(current) + weight(segment, criteria);
            if (newDistance < distances.getOrDefault(destination, Double.POSITIVE_INFINITY)) {
                distances.put(destination, newDistance);
                bestSegment.put(destination, segment);
                queue.add(destination);
            }
        }
    }

    private Optional<Journey> buildJourney(Map<Stop, Segment> bestSegment, Stop to) {
        List<Segment> path = new ArrayList<>();
        Stop cursor = to;
        while (true) {
            Segment segment = bestSegment.get(cursor);
            if (segment == null) {
                break;
            }
            Stop previous = segment.from().equals(cursor) ? segment.to() : segment.from();
            path.add(new Segment(previous, cursor, segment.mode(), segment.duration(), segment.distanceMeters()));
            cursor = previous;
        }
        Collections.reverse(path);
        if (path.isEmpty()) {
            return Optional.empty();
        }
        Duration totalDuration = path.stream()
                .map(Segment::duration)
                .reduce(Duration.ZERO, Duration::plus);
        return Optional.of(new Journey(path, totalDuration));
    }

    private double weight(Segment segment, OptimizationCriteria criteria) {
        return switch (criteria) {
            case FASTEST  -> segment.duration().toSeconds();
            case SHORTEST -> segment.distanceMeters();
        };
    }
}