package org.geogebra.common.kernel.prover.discovery;

import java.util.HashSet;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.geos.GeoLine;
import org.geogebra.common.kernel.geos.GeoPoint;

public class Line {
    private HashSet<GeoPoint> points = new HashSet<GeoPoint>();
    private final int id;
    private GeoLine line;

    public Line(Kernel kernel) {
        id = kernel.getApplication().getNextLineID();
    }

    public Line(GeoPoint p1, GeoPoint p2) {
        this(p1.getKernel());
        points.add(p1);
        points.add(p2);
    }

    public HashSet<GeoPoint> getPoints() {
        return points;
    }

    public void collinear(GeoPoint p) {
        points.add(p);
    }

}
