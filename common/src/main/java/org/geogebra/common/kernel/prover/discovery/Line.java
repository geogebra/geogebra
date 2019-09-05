package org.geogebra.common.kernel.prover.discovery;

import java.util.HashSet;
import java.util.Iterator;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.geos.GeoLine;
import org.geogebra.common.kernel.geos.GeoPoint;

public class Line {
    private HashSet<GeoPoint> points = new HashSet<GeoPoint>();
    private GeoPoint discoverInput;
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

    public GeoPoint[] getPoints2() {
        GeoPoint[] ps = new GeoPoint[2];
        Iterator<GeoPoint> it = points.iterator();
        ps[0] = it.next();
        ps[1] = it.next();
        return ps;
    }

    public void collinear(GeoPoint p) {
        points.add(p);
    }

    public void setDiscoverInput(GeoPoint p) {
        discoverInput = p;
    }

    public GeoPoint getDiscoverInput() {
        return discoverInput;
    }

    public String toString() {
        String ret = "";
        for (GeoPoint p : points) {
            ret += p.getLabelSimple();
        }
        return ret;
    }

}
