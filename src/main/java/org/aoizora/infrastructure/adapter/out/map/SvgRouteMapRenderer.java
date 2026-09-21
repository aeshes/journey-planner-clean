package org.aoizora.infrastructure.adapter.out.map;

import org.aoizora.application.port.out.RouteMapRenderer;
import org.aoizora.domain.model.Journey;
import org.aoizora.domain.model.Segment;
import org.aoizora.domain.model.Stop;

import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

public class SvgRouteMapRenderer implements RouteMapRenderer {

    private static final int WIDTH = 900;
    private static final int HEIGHT = 600;
    private static final int PADDING = 50;

    @Override
    public String render(List<Stop> stops, List<Segment> segments, Journey journey) {
        double[] bounds = computeBounds(stops, segments);
        double scale = Math.min(
                (WIDTH - 2.0 * PADDING) / span(bounds[1], bounds[3]),
                (HEIGHT - 2.0 * PADDING) / span(bounds[0], bounds[2]));
        double centerLat = (bounds[0] + bounds[2]) / 2.0;
        double centerLon = (bounds[1] + bounds[3]) / 2.0;

        Set<String> routeEdges = journey.segments().stream()
                .map(segment -> edgeKey(segment.from().id(), segment.to().id()))
                .collect(Collectors.toSet());

        StringBuilder svg = new StringBuilder();
        svg.append("<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"").append(WIDTH)
                .append("\" height=\"").append(HEIGHT).append("\" viewBox=\"0 0 ")
                .append(WIDTH).append(' ').append(HEIGHT).append("\">");
        svg.append("<rect width=\"100%\" height=\"100%\" fill=\"#f4f6f8\"/>");

        for (Segment segment : segments) {
            String stroke = routeEdges.contains(edgeKey(segment.from().id(), segment.to().id()))
                    ? "#1f6feb" : "#c9d2dc";
            svg.append("<line x1=\"").append(px(segment.from().longitude(), centerLon, scale))
                    .append("\" y1=\"").append(py(segment.from().latitude(), centerLat, scale))
                    .append("\" x2=\"").append(px(segment.to().longitude(), centerLon, scale))
                    .append("\" y2=\"").append(py(segment.to().latitude(), centerLat, scale))
                    .append("\" stroke=\"").append(stroke).append("\" stroke-width=\"2\"/>");
        }

        svg.append("<polyline points=\"");
        Stop last = journey.destination();
        for (Segment segment : journey.segments()) {
            svg.append(px(segment.from().longitude(), centerLon, scale))
                    .append(',').append(py(segment.from().latitude(), centerLat, scale)).append(' ');
        }
        svg.append(px(last.longitude(), centerLon, scale))
                .append(',').append(py(last.latitude(), centerLat, scale));
        svg.append("\" fill=\"none\" stroke=\"#1f6feb\" stroke-width=\"5\" stroke-linejoin=\"round\"/>");

        for (Stop stop : stops) {
            String color = stop.equals(journey.origin()) ? "#2da44e"
                    : stop.equals(journey.destination()) ? "#cf222e" : "#4b5563";
            svg.append(renderStop(stop, centerLat, centerLon, scale, color));
        }

        svg.append("</svg>");
        return svg.toString();
    }

    private String renderStop(Stop stop, double centerLat, double centerLon, double scale, String color) {
        String x = px(stop.longitude(), centerLon, scale);
        String y = py(stop.latitude(), centerLat, scale);
        return "<g><circle cx=\"" + x + "\" cy=\"" + y + "\" r=\"6\" fill=\"" + color + "\"/>" +
                "<text x=\"" + fmt(Double.parseDouble(x) + 9) + "\" y=\"" + fmt(Double.parseDouble(y) + 4) + "\"" +
                " font-size=\"12\" fill=\"#24292f\">" + escapeXml(stop.name()) + "</text></g>";
    }

    private double[] computeBounds(List<Stop> stops, List<Segment> segments) {
        double minLat = Double.MAX_VALUE;
        double minLon = Double.MAX_VALUE;
        double maxLat = -Double.MAX_VALUE;
        double maxLon = -Double.MAX_VALUE;
        for (Stop stop : stops) {
            minLat = Math.min(minLat, stop.latitude());
            maxLat = Math.max(maxLat, stop.latitude());
            minLon = Math.min(minLon, stop.longitude());
            maxLon = Math.max(maxLon, stop.longitude());
        }
        for (Segment segment : segments) {
            minLat = Math.min(minLat, segment.from().latitude());
            minLon = Math.min(minLon, segment.from().longitude());
            minLat = Math.min(minLat, segment.to().latitude());
            minLon = Math.min(minLon, segment.to().longitude());
            maxLat = Math.max(maxLat, segment.from().latitude());
            maxLon = Math.max(maxLon, segment.from().longitude());
            maxLat = Math.max(maxLat, segment.to().latitude());
            maxLon = Math.max(maxLon, segment.to().longitude());
        }
        if (stops.isEmpty() && segments.isEmpty()) {
            return new double[]{0.0, 0.0, 1.0, 1.0};
        }
        return new double[]{minLat, minLon, maxLat, maxLon};
    }

    private double span(double min, double max) {
        double diff = max - min;
        return diff < 1e-9 ? 1.0 : diff;
    }

    private String edgeKey(long a, long b) {
        return Math.min(a, b) + "-" + Math.max(a, b);
    }

    private String px(double lon, double centerLon, double scale) {
        return fmt((WIDTH / 2.0) + (lon - centerLon) * scale);
    }

    private String py(double lat, double centerLat, double scale) {
        return fmt((HEIGHT / 2.0) - (lat - centerLat) * scale);
    }

    private String fmt(double value) {
        if (Double.isNaN(value) || Double.isInfinite(value)) {
            return "0";
        }
        return String.format(Locale.ROOT, "%.1f", value);
    }

    private String escapeXml(String value) {
        return value == null ? "" : value
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&apos;");
    }
}