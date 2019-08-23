package org.geogebra.common.kernel.prover.discovery;

import java.util.HashSet;

public class ParallelLines {

    private HashSet<Line> lines = new HashSet<Line>();

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
}