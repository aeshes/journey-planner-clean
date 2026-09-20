package org.aoizora.domain.exception;

public class RouteNotFoundException extends RuntimeException {
    public RouteNotFoundException(long fromId, long toId) {
        super("No route found from stop " + fromId + " to stop " + toId);
    }
}
