package org.geogebra.common.kernel.prover.discovery;

import java.util.HashSet;
import java.util.Iterator;

import org.geogebra.common.kernel.geos.GeoLine;
import org.geogebra.common.kernel.geos.GeoPoint;

public class Line {
    private HashSet<GeoPoint> points = new HashSet<GeoPoint>();
    private GeoLine geoLine;

    public Boolean getTrivial() {
        return trivial;
    }

    public boolean isTheorem() {
        if (trivial != null && !trivial) {
            return true;
        }
        return false;
    }

    public void setTrivial(Boolean trivial) {
        this.trivial = trivial;
    }

    private Boolean trivial;

    public Line(GeoPoint p1, GeoPoint p2) {
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

    public GeoLine getGeoLine() {
        return geoLine;
    }

    public void setGeoLine(GeoLine gl) {
        geoLine = gl;
    }


}
