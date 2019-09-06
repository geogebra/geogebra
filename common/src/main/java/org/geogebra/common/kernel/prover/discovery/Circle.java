package org.geogebra.common.kernel.prover.discovery;

import java.util.HashSet;
import java.util.Iterator;

import org.geogebra.common.kernel.geos.GeoPoint;

public class Circle {
    private HashSet<GeoPoint> points = new HashSet<GeoPoint>();

    public Circle(GeoPoint p1, GeoPoint p2, GeoPoint p3) {
        points.add(p1);
        points.add(p2);
        points.add(p3);
    }

    public HashSet<GeoPoint> getPoints() {
        return points;
    }

    public GeoPoint[] getPoints3() {
        GeoPoint[] ps = new GeoPoint[3];
        Iterator<GeoPoint> it = points.iterator();
        ps[0] = it.next();
        ps[1] = it.next();
        ps[2] = it.next();
        return ps;
    }

    public void concyclic(GeoPoint p) {
        points.add(p);
    }

    public void deletePoint(GeoPoint p) {
        if (!points.contains(p)) {
            return; // do nothing
        }
        points.remove(p);
    }

    public String toString() {
        String ret = "";
        for (GeoPoint p : points) {
            ret += p.getLabelSimple();
        }
        return ret;
    }

}
