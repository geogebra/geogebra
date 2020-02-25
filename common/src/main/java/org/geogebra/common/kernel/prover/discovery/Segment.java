package org.geogebra.common.kernel.prover.discovery;

import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.geos.GeoSegment;

public class Segment {
    private GeoPoint startPoint, endPoint;
    private GeoSegment geoSegment;

    public Segment(GeoPoint p1, GeoPoint p2) {
        startPoint = p1;
        endPoint = p2;
    }

    public String toString() {
        return startPoint.getLabelSimple() + endPoint.getLabelSimple();
    }

    public GeoSegment getGeoSegment() {
        return geoSegment;
    }

    public GeoPoint getStartPoint() { return startPoint; }

    public GeoPoint getEndPoint() { return endPoint; }

    public void setGeoSegment(GeoSegment gs) {
        geoSegment = gs;
    }

}
