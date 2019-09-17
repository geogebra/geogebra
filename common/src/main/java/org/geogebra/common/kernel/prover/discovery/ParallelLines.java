package org.geogebra.common.kernel.prover.discovery;

import static java.util.Arrays.sort;

import java.util.HashSet;

import org.geogebra.common.awt.GColor;

import com.himamis.retex.editor.share.util.Unicode;

public class ParallelLines {

    private HashSet<Line> lines = new HashSet<Line>();
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

    public ParallelLines(Line l) {
        lines.add(l);
    }

    public ParallelLines(Line l1, Line l2) {
        lines.add(l1);
        lines.add(l2);
    }

    public HashSet<Line> getLines() {
        return lines;
    }

    public void parallel(Line l) {
        lines.add(l);
    }

    public boolean isParallelTo(Line l) {
        return lines.contains(l);
    }

    public String toString() {
        String[] labels = new String[lines.size()];
        int i = 0;
        for (Line l : lines) {
            labels[i] = l.toString();
            i++;
        }
        sort(labels);

        String ret = "";
        for (String la : labels) {
            ret += la + " " + Unicode.PARALLEL + " ";
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