package org.geogebra.common.kernel.prover.discovery;

import java.util.ArrayList;
import java.util.HashSet;

import org.geogebra.common.kernel.geos.GeoPoint;

public class Pool {
    ArrayList<Line> lines = new ArrayList<>();

    public Line getLine(GeoPoint p1, GeoPoint p2) {
        HashSet<GeoPoint> ps = new HashSet();
        ps.add(p1);
        ps.add(p2);
        for (Line l : lines) {
            HashSet<GeoPoint> points = l.getPoints();
            if (points.contains(p1) && points.contains(p2)) {
                return l;
            }
        }
        return null;
    }

    public boolean lineExists(GeoPoint p1, GeoPoint p2) {
        if (getLine(p1, p2) == null) {
            return false;
        }
        return true;
    }

    public Line addLine(GeoPoint p1, GeoPoint p2) {
        Line l = getLine(p1, p2);
        if (l == null) {
            Line line = new Line(p1, p2);
            lines.add(line);
            return line;
        }
        return l;
    }

    public Line addCollinearity(GeoPoint p1, GeoPoint p2, GeoPoint p3) {
        Line l = addLine(p1, p2);
        l.collinear(p3);
        return l;
    }

    public boolean areCollinear(GeoPoint p1, GeoPoint p2, GeoPoint p3) {
        Line l = getLine(p1, p2);
        if (l != null && l.getPoints().contains(p3)) {
            return true;
        }
        return false;
    }

}

