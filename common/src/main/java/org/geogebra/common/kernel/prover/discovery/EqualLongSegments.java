package org.geogebra.common.kernel.prover.discovery;
import static java.util.Arrays.sort;

import java.util.HashSet;

import org.geogebra.common.awt.GColor;

public class EqualLongSegments {
    private HashSet<Segment> segments = new HashSet<Segment>();
    private Boolean trivial;
    private GColor color;

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

    public EqualLongSegments(Segment s) {
        segments.add(s);
    }

    public EqualLongSegments(Segment s1, Segment s2) {
        segments.add(s1);
        segments.add(s2);
    }

    public HashSet<Segment> getSegments() {
        return segments;
    }

    public void equalLong(Segment s) {
        segments.add(s);
    }

    public boolean isEqualLongTo(Segment s) {
        return segments.contains(s);
    }

    public String toString() {
        String[] labels = new String[segments.size()];
        int i = 0;
        for (Segment s : segments) {
            labels[i] = s.toString();
            i++;
        }
        sort(labels);

        String ret = "";
        for (String la : labels) {
            ret += la + " = ";
        }
        if (!"".equals(ret)) {
            ret = ret.substring(0, ret.length() - 3);
        }
        return ret;
    }

    public void setColor(GColor c) {
        color = c;
    }

    public GColor getColor() {
        return color;
    }
}