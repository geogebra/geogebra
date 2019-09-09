package org.geogebra.common.kernel.prover.discovery;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.prover.Combinations;

public class Pool {
    public ArrayList<Line> lines = new ArrayList<>();
    public ArrayList<Circle> circles = new ArrayList<>();
    public ArrayList<ParallelLines> directions = new ArrayList<>();

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

    public Circle getCircle(GeoPoint p1, GeoPoint p2, GeoPoint p3) {
        HashSet<GeoPoint> ps = new HashSet();
        ps.add(p1);
        ps.add(p2);
        ps.add(p3);
        for (Circle c : circles) {
            HashSet<GeoPoint> points = c.getPoints();
            if (points.contains(p1) && points.contains(p2) && points.contains(p3)) {
                return c;
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

    public boolean circleExists(GeoPoint p1, GeoPoint p2, GeoPoint p3) {
        if (getCircle(p1, p2, p3) == null) {
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

    public Circle addCircle(GeoPoint p1, GeoPoint p2, GeoPoint p3) {
        Circle c = getCircle(p1, p2, p3);
        if (c == null) {
            Circle circle = new Circle(p1, p2, p3);
            circles.add(circle);
            return circle;
        }
        return c;
    }

    public ParallelLines addDirection(Line l) {
        ParallelLines pl = getDirection(l);
        if (pl == null) {
            ParallelLines parallelLine = new ParallelLines(l);
            directions.add(parallelLine);
        }
        return pl;
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
        HashSet<GeoPoint> pointlist = (HashSet<GeoPoint>) l.getPoints().clone();
        for (GeoPoint pl : pointlist) {
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

    private void setConcylic(Circle c, GeoPoint p) {
        /* Claim that p lies on c.
         * Consider that 1236 and 3456 are already concyclic
         * and it is stated that 2 lies on 3456 by the function call.
         * Since 3 lies on 3456 and 236 exists, all points 123456 must
         * be concyclic. So we do the following:
         * For each point pairs ppc (eg. 36) of c (3456) we check if the circle ec that lies on ppc and p
         * (here 1236) already exists. If yes, all points cp (1,2,3,6) of this circle ec will
         * be claimed to be concyclic to c. Finally we remove the circle ec (1238).
         *
         * If there is no such problem, we simply add p to c.
         */
        Combinations pairlist = new Combinations(c.getPoints(), 2);
        while (pairlist.hasNext()) {
            Set<GeoPoint> ppc = pairlist.next();
            Iterator<GeoPoint> i = ppc.iterator();
            GeoPoint p1 = i.next();
            GeoPoint p2 = i.next();
            Circle ec = getCircle(p1, p2, p);
            if (ec != null && !ec.equals(c)) {
                for (GeoPoint cp : ec.getPoints()) {
                    c.concyclic(cp);
                }
                circles.remove(ec);
            }
        }
        c.concyclic(p);
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

    public Circle addConcyclicity(GeoPoint p1, GeoPoint p2, GeoPoint p3, GeoPoint p4) {
        Circle c;
        if (circleExists(p1, p2, p3)) {
            c = getCircle(p1, p2, p3);
            setConcylic(c, p4);
            return c;
        }
        if (circleExists(p1, p2, p4)) {
            c = getCircle(p1, p2, p4);
            setConcylic(c, p3);
            return c;
        }
        if (circleExists(p1, p3, p4)) {
            c = getCircle(p1, p3, p4);
            setConcylic(c, p2);
            return c;
        }
        if (circleExists(p2, p3, p4)) {
            c = getCircle(p2, p3, p4);
            setConcylic(c, p1);
            return c;
        }
        c = addCircle(p1, p2, p3);
        setConcylic(c, p4);
        return c;
    }

    public boolean areCollinear(GeoPoint p1, GeoPoint p2, GeoPoint p3) {
        Line l = getLine(p1, p2);
        if (l != null && l.getPoints().contains(p3)) {
            return true;
        }
        return false;
    }

    public boolean areConcyclic(GeoPoint p1, GeoPoint p2, GeoPoint p3, GeoPoint p4) {
        Circle c = getCircle(p1, p2, p3);
        if (c != null && c.getPoints().contains(p4)) {
            return true;
        }
        return false;
    }

    public boolean areParallel(Line l1, Line l2) {
        ParallelLines pl1 = getDirection(l1);
        ParallelLines pl2 = getDirection(l2);
        if (pl1 != null && pl2 != null && pl1.equals(pl2)) {
            return true;
        }
        return false;
    }

    public ParallelLines getDirection(Line l) {
        for (ParallelLines pl : directions) {
            if (pl.getLines().contains(l)) {
                return pl;
            }
        }
        return null;
    }

    public boolean directionExists(Line l) {
        if (getDirection(l) != null) {
            return true;
        }
        return false;
    }

    /* l1 != l2 */
    public ParallelLines addParallelism(Line l1, Line l2) {
        if (l1.equals(l2)) {
            return getDirection(l1); // no action is needed
        }
        ParallelLines dir1 = getDirection(l1);
        ParallelLines dir2 = getDirection(l2);
        if (dir1 == null && dir2 == null) {
            directions.add(new ParallelLines(l1, l2));
            return dir1;
        }
        if (dir1 != null && dir2 == null) {
            dir1.parallel(l2);
            return dir1;
        }
        if (dir1 == null && dir2 != null) {
            dir2.parallel(l1);
            return dir1;
        }
        // Unifying the two directions as one:
        for (Line l : dir1.getLines()) {
            dir2.parallel(l);
        }
        directions.remove(dir1);
        return dir2;
    }

    public void removePoint(GeoPoint p) {
        ArrayList<Line> oldLines = (ArrayList<Line>) lines.clone();
        for (Line l : oldLines) {
            if (l.getPoints().contains(p)) {
                l.deletePoint(p);
                if (l.getPoints().size() < 3) {
                    lines.remove(l);
                    if (l.getGeoLine() != null) {
                        l.getGeoLine().remove();
                    }
                }
            }
        }

        ArrayList<Circle> oldCircles = (ArrayList<Circle>) circles.clone();
        for (Circle c : oldCircles) {
            if (c.getPoints().contains(p)) {
                c.deletePoint(p);
                if (c.getPoints().size() < 4) {
                    circles.remove(c);
                    if (c.getGeoConic() != null) {
                        c.getGeoConic().remove();
                    }
                }
            }
        }
    }
}

