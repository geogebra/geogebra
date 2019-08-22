package org.geogebra.common.kernel.prover.discovery;

import java.util.ArrayList;
import java.util.HashSet;

import org.geogebra.common.kernel.geos.GeoPoint;

public class Pool {
    public ArrayList<Line> lines = new ArrayList<>();

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

    private void setCollinear(Line l, GeoPoint p) {
        /* Claim that p lies on l.
         * Consider that 123 and 345 are already collinear
         * and it is stated that 2 lies on 45 by the function call.
         * Since 3 lies on 45 and 23 exists, all points 12345 must
         * be collinear. So we do the following:
         * For each point pl of l (45) we check if the line el joining pl and p
         * (here 23) already exists. If yes, all points ep (1,2,3) of this line el will
         * be claimed to be collinear to l. Finally we remove the line el (23).
         *
         * If there is no such problem, we simply add p to l.
         */
        for (GeoPoint pl : l.getPoints()) {
            Line el = getLine(pl, p);
            if (el != null && !el.equals(l)) {
                for (GeoPoint ep : el.getPoints()) {
                    l.collinear(ep);
                }
                lines.remove(el);
            }
        }
        l.collinear(p);
    }

    public Line addCollinearity(GeoPoint p1, GeoPoint p2, GeoPoint p3) {
        Line l;
        if (lineExists(p1, p2)) {
            l = getLine(p1, p2);
            setCollinear(l, p3);
            return l;
        }
        if (lineExists(p1, p3)) {
            l = getLine(p1, p3);
            setCollinear(l, p2);
            return l;
        }
        if (lineExists(p2, p3)) {
            l = getLine(p2, p3);
            setCollinear(l, p1);
            return l;
        }
        l = addLine(p1, p2);
        setCollinear(l, p3);
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

