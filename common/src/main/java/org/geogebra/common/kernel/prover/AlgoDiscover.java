package org.geogebra.common.kernel.prover;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Path;
import org.geogebra.common.kernel.algos.AlgoCircleThreePoints;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.algos.AlgoIntersectLines;
import org.geogebra.common.kernel.algos.AlgoJoinPoints;
import org.geogebra.common.kernel.algos.AlgoJoinPointsSegment;
import org.geogebra.common.kernel.algos.AlgoMidpoint;
import org.geogebra.common.kernel.algos.AlgoMidpointSegment;
import org.geogebra.common.kernel.algos.AlgoPointOnPath;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GProperty;
import org.geogebra.common.kernel.geos.GeoBoolean;
import org.geogebra.common.kernel.geos.GeoConic;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoLine;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.geos.GeoSegment;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.kernel.prover.discovery.Line;
import org.geogebra.common.kernel.prover.discovery.Pool;
import org.geogebra.common.plugin.EuclidianStyleConstants;

/**
 * @author Zoltan Kovacs <zoltan@geogebra.org>
 */
public class AlgoDiscover extends AlgoElement {
    /* FIXME: When updating the underlying structure of the input,
     * the whole computation should be completely redone.
     * A similar problem can occur in some other commands of GeoGebra ART.
     */

    private GeoElement input; // input

    private GeoElement[] output; // output
    private ArrayList<GeoElement> output_wip; // output, work-in-progress

    public AlgoDiscover(final Construction cons,
                        final GeoElement input) {
        super(cons);
        cons.addCASAlgo(this);
        this.input = input;
        output_wip = new ArrayList<>();

        initialCompute();
        this.output = new GeoElement[output_wip.size()];
        for (int i = 0; i < output_wip.size(); ++i) {
            this.output[i] = output_wip.get(i);
            boolean oldMacroMode = cons.isSuppressLabelsActive();
            this.output[i].setEuclidianVisible(true);
            // this.output[i].setLineType(EuclidianStyleConstants.LINE_TYPE_DASHED_LONG);
            this.output[i].setLabelVisible(true);
            this.output[i].updateVisualStyle(GProperty.COMBINED); // visibility and style
            cons.setSuppressLabelCreation(oldMacroMode);
        }

        setInputOutput();
    }

    @Override
    public Commands getClassName() {
        return Commands.Discover;
    }

    @Override
    protected void setInputOutput() {
        super.input = new GeoElement[1];
        super.input[0] = this.input;

        super.setOutputLength(this.output.length);
        super.setOutput(this.output);
        setDependencies(); // done by AlgoElement
    }

    public GeoElement[] getResult() {
        return this.output;
    }

    @Override
    public final void compute() {
    }

    /*
     * Build the whole database of trivial collinearities,
     * including all points in the construction list
     * that precedes the input.
     */
    private void detectTrivialCollinearities(GeoPoint p) {
        TreeSet<GeoElement> ges = (TreeSet<GeoElement>) cons.getGeoSetLabelOrder().clone();
        for (GeoElement ge : ges) {
            if (ge instanceof GeoPoint) {
                collectTrivialCollinearites((GeoPoint) ge, p.equals(ge));
            }
        }
    }

    /*
     * Extend the database of trivial collinearities by
     * collecting all of them for a given input.
     */
    private void collectTrivialCollinearites(GeoPoint p0, boolean discover) {
        Pool trivialPool = this.input.getKernel().getApplication().getTrivialPool();
        Pool discoveryPool = this.input.getKernel().getApplication().getDiscoveryPool();

        HashSet<GeoPoint> prevPoints = new HashSet<GeoPoint>();
        for (GeoElement ge : cons.getGeoSetLabelOrder()) {
            if (ge instanceof GeoPoint && !ge.equals(p0)) {
                prevPoints.add((GeoPoint) ge);
                }
            }

        Combinations lines = new Combinations(prevPoints, 2);

        while (lines.hasNext()) {
            Set<GeoPoint> line = lines.next();
            Iterator<GeoPoint> i = line.iterator();
            GeoPoint p1 = i.next();
            GeoPoint p2 = i.next();
            if (!trivialPool.areCollinear(p0, p1, p2) &&
                    !discoveryPool.areCollinear(p0, p1, p2)) {
                // Add {p0,p1,p2} to the trivial pool if they are trivially collinear:
                checkCollinearity(p0, p1, p2);
                checkCollinearity(p1, p2, p0);
                checkCollinearity(p2, p0, p1);
            }
            if (!trivialPool.areCollinear(p0, p1, p2)) {
                trivialPool.addLine(p1, p2);
                trivialPool.addLine(p0, p1);
                trivialPool.addLine(p0, p2);
            }
        }

        if (discover) {
            // Second round:
            // put non-trivial collinearities in the
            // discovery pool.
            ArrayList<Line> oldLines = (ArrayList<Line>) discoveryPool.lines.clone();
            lines = new Combinations(prevPoints, 2);
            while (lines.hasNext()) {
                Set<GeoPoint> line = lines.next();
                Iterator<GeoPoint> i = line.iterator();
                GeoPoint p1 = i.next();
                GeoPoint p2 = i.next();
                if (!trivialPool.areCollinear(p0, p1, p2) &&
                        !discoveryPool.areCollinear(p0, p1, p2)) {
                    AlgoAreCollinear aac = new AlgoAreCollinear(cons, p0, p1, p2);
                    if (aac.getResult().getBoolean()) {
                        // Conjecture: Collinearity
                        GeoElement root = new GeoBoolean(cons);
                        root.setParentAlgorithm(aac);
                        AlgoProveDetails ap = new AlgoProveDetails(cons, root);
                        ap.compute();
                        GeoElement[] o = ap.getOutput();
                        GeoElement truth = ((GeoList) o[0]).get(0);
                        if (((GeoBoolean) truth).getBoolean()) {
                            // Theorem: Collinearity
                            discoveryPool.addCollinearity(p0, p1, p2);
                        }
                    }
                }
            }

            // Third round: Draw lines from the discovery pool
            // (those that are not yet drawn):
            for (Line l : discoveryPool.lines) {
                boolean silentmode = cons.getKernel().isSilentMode();
                boolean showline = false;
                if (!oldLines.contains(l)) {
                    showline = true;
                }
                if (oldLines.contains(l)) {
                    if (silentmode && !l.shownSilent) {
                        showline = true;
                    }
                    if (!silentmode && !l.shown) {
                        showline = true;
                    }
                }
                if (showline) {
                    GeoPoint[] twopoints = l.getPoints2();
                    addOutputLine(twopoints[0], twopoints[1]);
                    if (!silentmode) {
                        l.shown = true;
                    } else {
                        l.shownSilent = true;
                    }
                }
            }
        }
    }

    public final void initialCompute() {

        detectTrivialCollinearities((GeoPoint) this.input);
        // Remove this to get collinearity check demo:
        if (1 + 2 == 3)
            return;

        Pool trivialPool = this.input.getKernel().getApplication().getTrivialPool();
        Pool discoveryPool = this.input.getKernel().getApplication().getDiscoveryPool();
        trivialPool.lines.clear();
        discoveryPool.lines.clear();

        if (this.input instanceof GeoPoint) {
            HashSet<GeoPoint> notChildren = new HashSet<GeoPoint>();
            for (GeoElement ge : cons.getGeoSetLabelOrder()) {
                if (ge instanceof GeoPoint && !ge.equals((this.input)) && !ge.getAllPredecessors().contains(this.input)) {
                    notChildren.add((GeoPoint) ge);
                }
            }

            GeoPoint p0 = (GeoPoint) this.input;

            Combinations lines = new Combinations(notChildren, 2);
            while (lines.hasNext()) {
                Set<GeoPoint> line = lines.next();
                Iterator<GeoPoint> i = line.iterator();
                GeoPoint p1 = i.next();
                GeoPoint p2 = i.next();
                AlgoAreCollinear aac = new AlgoAreCollinear(cons, p0, p1, p2);
                if (aac.getResult().getBoolean()) {
                    // Conjecture: Collinearity
                    GeoElement root = new GeoBoolean(cons);
                    root.setParentAlgorithm(aac);
                    AlgoProveDetails ap = new AlgoProveDetails(cons, root);
                    ap.compute();
                    GeoElement[] o = ap.getOutput();
                    GeoElement truth = ((GeoList) o[0]).get(0);
                    checkCollinearity(p0, p1, p2);
                    checkCollinearity(p1, p2, p0);
                    checkCollinearity(p2, p0, p1);
                    // The symbolic computation should be done just later in the 3rd run.
                    if (!trivialPool.areCollinear(p0, p1, p2) &&
                            !discoveryPool.areCollinear(p0, p1, p2) &&
                            ((GeoBoolean) truth).getBoolean()) {
                        // Theorem: Collinearity
                        discoveryPool.addCollinearity(p0, p1, p2);
                        // Later it can be removed if it proves to be trivial.
                    }
                }
            }
            // Remove trivial lines from discovery pool (2nd run):
            ArrayList<Line> discoveryPoolLines = (ArrayList<Line>) discoveryPool.lines.clone();
            for (Line dl : discoveryPoolLines) {
                HashSet<GeoPoint> ps = dl.getPoints();
                Combinations twoPointss = new Combinations(ps, 2);
                while (twoPointss.hasNext()) {
                    Set<GeoPoint> twoPoints = twoPointss.next();
                    Iterator<GeoPoint> it = twoPoints.iterator();
                    GeoPoint p1 = it.next();
                    GeoPoint p2 = it.next();
                    Line tl = trivialPool.getLine(p1, p2);
                    if (tl != null) {
                        Line dl2 = discoveryPool.getLine(p1, p2);
                        discoveryPool.lines.remove(dl2);
                    }
                }
            }
            // Draw remaining lines (that are not trivial, 3rd run):
            for (Line dl : discoveryPool.lines) {
                GeoPoint[] ps = dl.getPoints2();
                addOutputLine(ps[0], ps[1]);
            }

            // Remove this to get circles+parallels demo.
            if (1 + 2 == 3)
                return;

            Combinations circles = new Combinations(notChildren, 3);
            while (circles.hasNext()) {
                Set<GeoPoint> circle = circles.next();
                Iterator<GeoPoint> i = circle.iterator();
                GeoPoint p1 = i.next();
                GeoPoint p2 = i.next();
                GeoPoint p3 = i.next();

                AlgoAreConcyclic aac = new AlgoAreConcyclic(cons, (GeoPoint) this.input, p1, p2, p3);
                if (aac.getResult().getBoolean()) {
                    // Conjecture: Concyclic
                    GeoElement root = new GeoBoolean(cons);
                    root.setParentAlgorithm(aac);
                    AlgoProveDetails ap = new AlgoProveDetails(cons, root);
                    ap.compute();
                    GeoElement[] o = ap.getOutput();
                    GeoElement truth = ((GeoList) o[0]).get(0);
                    if (((GeoBoolean) truth).getBoolean()) {
                        if (true) {
                            // Theorem: Concyclicity
                            addOutputCircle(p1, p2, p3);
                        }
                    }
                }

                // Parallelism check
                /*
                 * FIXME: Lots of extra pair of lines will be created unnecessarily
                 * if the same lines are defined by several pairs of points.
                 * This issue should be fixed by defining the set of parallel lines
                 * and if one entry already exists then no new pair of parallel lines
                 * should be added. This set should be maintained in a "discovery pool".
                 */
                GeoPoint[][] pp = {{p1, p2, p3}, {p2, p3, p1}, {p3, p1, p2}};
                for (int j = 0; j < 3; j++) {
                    boolean theorem = false;
                    GeoPoint q1 = pp[j][0];
                    GeoPoint q2 = pp[j][1];
                    GeoPoint q3 = pp[j][2];
                    AlgoJoinPoints ajp1 = new AlgoJoinPoints(cons, null, (GeoPoint) this.input, q1);
                    AlgoJoinPoints ajp2 = new AlgoJoinPoints(cons, null, q2, q3);
                    GeoLine l1 = ajp1.getLine();
                    GeoLine l2 = ajp2.getLine();
                    AlgoAreParallel aap = new AlgoAreParallel(cons, l1, l2);
                    if (aap.getResult().getBoolean()) {
                        // Conjecture: Parallel
                        GeoElement root = new GeoBoolean(cons);
                        root.setParentAlgorithm(aap);
                        AlgoProveDetails ap = new AlgoProveDetails(cons, root);
                        ap.compute();
                        GeoElement[] o = ap.getOutput();
                        GeoElement truth = ((GeoList) o[0]).get(0);
                        if (((GeoBoolean) truth).getBoolean()) {
                            if (true) {
                                // Theorem: Parallelism
                                addOutputLines(l1, l2);
                                theorem = true;
                            }
                        }
                    }
                    if (!theorem) {
                        l1.remove();
                        l2.remove();
                    }
                }
            }
        }
    }

    GColor nextColor(GeoElement e) {
        return e.getAutoColorScheme()
                .getNext(!cons.getKernel().isSilentMode());
    }

    void addOutputLines(GeoLine a, GeoLine b) {
        GColor c = nextColor(a);
        boolean oldMacroMode = cons.isSuppressLabelsActive();
        a.setObjColor(c);
        a.setEuclidianVisible(true);
        a.setLineType(EuclidianStyleConstants.LINE_TYPE_FULL);
        a.setLineThickness(1);
        a.setLabelVisible(true);
        b.setObjColor(c);
        b.setEuclidianVisible(true);
        b.setLineType(EuclidianStyleConstants.LINE_TYPE_FULL);
        b.setLineThickness(1);
        b.setLabelVisible(true);
        cons.setSuppressLabelCreation(oldMacroMode);
        output_wip.add(a);
        output_wip.add(b);
    }

    void addOutputLine(GeoPoint A, GeoPoint B) {
        boolean oldMacroMode = cons.isSuppressLabelsActive();
        AlgoJoinPoints ajp = new AlgoJoinPoints(cons, null, A, B);
        GeoLine l = ajp.getLine();
        l.setObjColor(nextColor(l));
        l.setEuclidianVisible(true);
        l.setLineType(EuclidianStyleConstants.LINE_TYPE_DASHED_LONG);
        l.setLabelVisible(true);
        l.updateVisualStyle(GProperty.COMBINED); // visibility and style
        cons.setSuppressLabelCreation(oldMacroMode);
        output_wip.add(l);
    }

    void addOutputCircle(GeoPoint A, GeoPoint B, GeoPoint C) {
        boolean oldMacroMode = cons.isSuppressLabelsActive();
        AlgoCircleThreePoints actp = new AlgoCircleThreePoints(cons, null, A, B, C);
        GeoConic circle = (GeoConic) actp.getCircle();
        circle.setObjColor(nextColor(circle));
        circle.setEuclidianVisible(true);
        circle.setLineType(EuclidianStyleConstants.LINE_TYPE_DASHED_LONG);
        circle.setLabelVisible(true);
        circle.updateVisualStyle(GProperty.COMBINED); // visibility and style
        cons.setSuppressLabelCreation(oldMacroMode);
        output_wip.add(circle);
    }

    void checkCollinearity(GeoPoint A, GeoPoint B, GeoPoint C) {
        /*
         * TODO. This is certainly incomplete.
         */
        Pool trivialPool = A.getKernel().getApplication().getTrivialPool();

        AlgoElement ae = C.getParentAlgorithm();

        if (ae instanceof AlgoIntersectLines) {
            GeoElement[] inps = ((AlgoIntersectLines) ae).getInput();
            GeoPoint i1 = ((GeoLine) inps[0]).getStartPoint();
            GeoPoint i2 = ((GeoLine) inps[0]).getEndPoint();
            GeoPoint j1 = ((GeoLine) inps[1]).getStartPoint();
            GeoPoint j2 = ((GeoLine) inps[1]).getEndPoint();

            if ((i1 != null && i2 != null && ((i1.equals(A) && i2.equals(B)) ||
                    (i1.equals(B) && i2.equals(A)))) ||
                    (j1 != null && j2 != null && ((j1.equals(A) && j2.equals(B)) ||
                            (j1.equals(B) && j2.equals(A))))
            ) {
                // C is an intersection of AB and something:
                trivialPool.addCollinearity(A, B, C);
            }
        }
        if (ae instanceof AlgoMidpoint) {
            GeoElement[] inps = ((AlgoMidpoint) ae).getInput();
            if ((inps[0].equals(A) && inps[1].equals(B)) ||
                    (inps[0].equals(B) && inps[1].equals(A))) {
                // C is a midpoint of AB:
                trivialPool.addCollinearity(A, B, C);
            }
        }
        if (ae instanceof AlgoMidpointSegment) {
            GeoSegment seg = (GeoSegment) ((AlgoMidpointSegment) ae).getInput(0);
            GeoPoint p1 = seg.startPoint;
            GeoPoint p2 = seg.endPoint;
            if ((p1.equals(A) && p2.equals(B)) ||
                    (p1.equals(B) && p2.equals(A))) {
                // C is a midpoint of AB:
                trivialPool.addCollinearity(A, B, C);
            }
        }
        if (ae instanceof AlgoPointOnPath) {
            Path p = ((AlgoPointOnPath) ae).getPath();
            AlgoElement aep = p.getParentAlgorithm();
            if (aep instanceof AlgoJoinPointsSegment) {
                AlgoJoinPointsSegment ajps = (AlgoJoinPointsSegment) aep;
                GeoElement[] ges = ajps.getInput();
                if ((ges[0].equals(A) && ges[1].equals(B)) ||
                        ges[0].equals(B) && ges[1].equals(A)) {
                    // C is defined to be on segment AB
                    trivialPool.addCollinearity(A, B, C);
                }
            }
        }
    }
}