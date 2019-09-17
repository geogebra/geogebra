package org.geogebra.common.kernel.prover.discovery;

import static java.util.Arrays.sort;

import java.util.HashSet;
import java.util.Iterator;

import org.geogebra.common.kernel.geos.GeoConic;
import org.geogebra.common.kernel.geos.GeoPoint;

public class Circle {
    private HashSet<GeoPoint> points = new HashSet<GeoPoint>();
    private GeoConic geoConic;

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

    // Literally the same as Line.toString()
    public String toString() {
        String[] labels = new String[points.size()];
        int i = 0;
        for (GeoPoint p : points) {
            labels[i] = p.getLabelSimple();
            i++;
        }
        sort(labels);
        String ret = "";
        for (String l : labels) {
            ret += l;
        }
        return ret;
    }


    public GeoConic getGeoConic() {
        return geoConic;
    }

    public void setGeoConic(GeoConic gc) {
        geoConic = gc;
    }


}
