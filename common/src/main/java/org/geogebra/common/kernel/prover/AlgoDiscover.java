package org.geogebra.common.kernel.prover;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

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
import org.geogebra.common.kernel.algos.AlgoOrthoLinePointLine;
import org.geogebra.common.kernel.algos.AlgoPointOnPath;
import org.geogebra.common.kernel.cas.UsesCAS;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GProperty;
import org.geogebra.common.kernel.geos.GeoBoolean;
import org.geogebra.common.kernel.geos.GeoConic;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoLine;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.geos.GeoSegment;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.prover.discovery.Circle;
import org.geogebra.common.kernel.prover.discovery.Line;
import org.geogebra.common.kernel.prover.discovery.ParallelLines;
import org.geogebra.common.kernel.prover.discovery.Pool;
import org.geogebra.common.plugin.EuclidianStyleConstants;
import org.geogebra.common.util.debug.Log;

/**
 * @author Zoltan Kovacs <zoltan@geogebra.org>
 */

// This is actually not an algo. Consider putting this entirely in CmdDiscover.
public class AlgoDiscover extends AlgoElement implements UsesCAS {

    private GeoElement input; // input
    private GeoElement output; // output, actually null

    public AlgoDiscover(final Construction cons,
                        final GeoElement input) {
        super(cons);
        setPrintedInXML(false);
        this.input = input;
        this.output = null;

        initialCompute();
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
        super.setOutputLength(0);
        setDependencies(); // done by AlgoElement
    }

    public GeoElement getResult() {
        return this.output;
    }

    @Override
    public final void compute() { // do nothing for the moment
    }

    /*
     * Build the whole database of properties,
     * including all points in the construction list.
     */
    private void detectProperties(GeoPoint p) {
        HashSet<GeoElement> ges = new HashSet<>();
        for (GeoElement ge : cons.getGeoSetLabelOrder()) {
            ges.add(ge);
        }
        for (GeoElement ge : ges) {
            if (ge instanceof GeoPoint && !p.equals(ge)) {
                collectCollinearites((GeoPoint) ge, false);
                collectConcyclicities((GeoPoint) ge, false);
            }
        }
        collectCollinearites(p, true);
        collectConcyclicities(p, true);

        for (GeoElement ge : ges) {
            if (ge instanceof GeoPoint && !p.equals(ge)) {
                collectParallelisms((GeoPoint) ge, false);
            }
        }
        collectParallelisms(p, true);
    }

    /*
     * Extend the database of collinearities by
     * collecting all of them for a given input.
     */
    private void collectCollinearites(GeoPoint p0, boolean discover) {
        Pool discoveryPool = cons.getDiscoveryPool();

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
            if (!discoveryPool.areCollinear(p0, p1, p2)) {
                // Add {p0,p1,p2} to the trivial pool if they are trivially collinear:
                checkCollinearity(p0, p1, p2);
                checkCollinearity(p1, p2, p0);
                checkCollinearity(p2, p0, p1);
            }
            if (!discoveryPool.areCollinear(p0, p1, p2)) {
                discoveryPool.addLine(p1, p2);
                discoveryPool.addLine(p0, p1);
                discoveryPool.addLine(p0, p2);
            }
        }

        // Second round:
        // put non-trivial collinearities in the
        // discovery pool. It is needed to do this for all p0 (not for just the final
        // one to discover) in order to have all parallel lines correctly.
        lines = new Combinations(prevPoints, 2);
        while (lines.hasNext()) {
            Set<GeoPoint> line = lines.next();
            Iterator<GeoPoint> i = line.iterator();
            GeoPoint p1 = i.next();
            GeoPoint p2 = i.next();
            if (!discoveryPool.areCollinear(p0, p1, p2)) {
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
                        discoveryPool.addCollinearity(p0, p1, p2).setTrivial(false);
                    }
                    ap.remove();
                }
                aac.remove();
            }
        }

        if (discover) {
            // Third round: Draw lines from the discovery pool
            // (those that are not yet drawn):
            for (Line l : discoveryPool.lines) {
                if (l.isTheorem() && !alreadyDrawn(l)) {
                    GeoPoint[] twopoints = l.getPoints2();
                    if (l.getPoints().contains(p0)) {
                        l.setGeoLine(addOutputLine(twopoints[0], twopoints[1]));
                    }
                }
            }
        }
    }

    /*
     * Extend the database by
     * collecting all conclicities for a given input.
     */
    private void collectConcyclicities(GeoPoint p0, boolean discover) {
        Pool discoveryPool = cons.getDiscoveryPool();

        HashSet<GeoPoint> prevPoints = new HashSet<GeoPoint>();
        for (GeoElement ge : cons.getGeoSetLabelOrder()) {
            if (ge instanceof GeoPoint && !ge.equals(p0)) {
                prevPoints.add((GeoPoint) ge);
            }
        }

        Combinations circles = new Combinations(prevPoints, 3);

        while (circles.hasNext()) {
            Set<GeoPoint> circle = circles.next();
            Iterator<GeoPoint> i = circle.iterator();
            GeoPoint p1 = i.next();
            GeoPoint p2 = i.next();
            GeoPoint p3 = i.next();
            // In case 3 of the 4 are collinear, let's ignore this case:
            if (are3Collinear(p0, p1, p2, p3)) {
                continue;
            }

            if (!discoveryPool.areConcyclic(p0, p1, p2, p3)) {
                // Add {p0,p1,p2,p3} to the trivial pool if they are trivially concyclic:
                checkConcyclicity(p0, p1, p2, p3);
            }
            if (!discoveryPool.areConcyclic(p0, p1, p2, p3)) {
                discoveryPool.addCircle(p0, p1, p2);
                discoveryPool.addCircle(p0, p1, p3);
                discoveryPool.addCircle(p0, p2, p3);
                discoveryPool.addCircle(p1, p2, p3);
            }
        }

        if (discover) {
            // Second round:
            // put non-trivial concyclicities in the
            // discovery pool.
            circles = new Combinations(prevPoints, 3);
            while (circles.hasNext()) {
                Set<GeoPoint> circle = circles.next();
                Iterator<GeoPoint> i = circle.iterator();
                GeoPoint p1 = i.next();
                GeoPoint p2 = i.next();
                GeoPoint p3 = i.next();
                if (!are3Collinear(p0, p1, p2, p3) &&
                        !discoveryPool.areConcyclic(p0, p1, p2, p3)) {
                    AlgoAreConcyclic aac = new AlgoAreConcyclic(cons, p0, p1, p2, p3);
                    if (aac.getResult().getBoolean()) {
                        // Conjecture: Concyclicity
                        GeoElement root = new GeoBoolean(cons);
                        root.setParentAlgorithm(aac);
                        AlgoProveDetails ap = new AlgoProveDetails(cons, root);
                        ap.compute();
                        GeoElement[] o = ap.getOutput();
                        GeoElement truth = ((GeoList) o[0]).get(0);
                        if (((GeoBoolean) truth).getBoolean()) {
                            // Theorem: Concyclicity
                            discoveryPool.addConcyclicity(p0, p1, p2, p3).setTrivial(false);
                        }
                        ap.remove();
                    }
                    aac.remove();
                }
            }

            // Third round: Draw circles from the discovery pool
            // (those that are not yet drawn):
            for (Circle c : discoveryPool.circles) {
                if (c.isTheorem() && !alreadyDrawn(c)) {
                    GeoPoint[] threepoints = c.getPoints3();
                    if (c.getPoints().contains(p0)) {
                        c.setGeoConic(addOutputCircle(threepoints[0], threepoints[1], threepoints[2]));
                    }
                }
            }
        }
    }

    /*
     * Extend the database by
     * collecting all parallelisms for a given input.
     */
    private void collectParallelisms(GeoPoint p0, boolean discover) {
        Pool discoveryPool = cons.getDiscoveryPool();

        HashSet<GeoPoint> prevPoints = new HashSet<GeoPoint>();
        for (GeoElement ge : cons.getGeoSetLabelOrder()) {
            if (ge instanceof GeoPoint && !ge.equals(p0)) {
                prevPoints.add((GeoPoint) ge);
            }
        }

        HashSet<Line> allLines = new HashSet<>();
        allLines.addAll(discoveryPool.lines);

        // First run: Finding trivial parallelisms.
        for (Line l1 : allLines) {
            for (GeoPoint p1 : prevPoints) {
                if (!l1.getPoints().contains(p0) && !l1.getPoints().contains(p1)) {
                    // if they are not collinear
                    GeoPoint[] p23 = l1.getPoints2();
                    Line l2 = discoveryPool.getLine(p0, p1);
                    // Consider further trivial checks...

                    AlgoJoinPoints ajp1 = new AlgoJoinPoints(cons, null, p23[0], p23[1]);
                    AlgoJoinPoints ajp2 = new AlgoJoinPoints(cons, null, p0, p1);
                    GeoLine gl1 = ajp1.getLine();
                    GeoLine gl2 = ajp2.getLine();

                    if (!discoveryPool.areParallel(l1, l2)) {
                        // Add {p0,p1,p2} to the trivial pool if they are trivially parallel:
                        checkParallelism(gl1, gl2);
                    }
                    gl1.remove();
                    gl2.remove();
                    ajp1.remove();
                    ajp2.remove();
                    if (!discoveryPool.areParallel(l1, l2)) {
                        discoveryPool.addDirection(l1);
                        discoveryPool.addDirection(l2);
                    }
                }
            }
        }

        if (discover) {
            // Second run: detect non-trivial parallelisms...
            for (Line l1 : allLines) {
                for (GeoPoint p1 : prevPoints) {
                    if (!l1.getPoints().contains(p0) && !l1.getPoints().contains(p1)) {
                        // if they are not collinear
                        GeoPoint[] p23 = l1.getPoints2();
                        Line l2 = discoveryPool.getLine(p0, p1);
                        // Consider further trivial checks...

                        if (!discoveryPool.areParallel(l1, l2)) {
                            AlgoJoinPoints ajp1 = new AlgoJoinPoints(cons, null, p23[0], p23[1]);
                            AlgoJoinPoints ajp2 = new AlgoJoinPoints(cons, null, p0, p1);
                            GeoLine gl1 = ajp1.getLine();
                            GeoLine gl2 = ajp2.getLine();
                            if (gl1.isParallel(gl2)) {
                                AlgoAreParallel aap = new AlgoAreParallel(cons, gl1, gl2);
                                GeoElement root = new GeoBoolean(cons);
                                root.setParentAlgorithm(aap);
                                AlgoProveDetails ap = new AlgoProveDetails(cons, root);
                                ap.compute();
                                GeoElement[] o = ap.getOutput();
                                GeoElement truth = ((GeoList) o[0]).get(0);
                                if (((GeoBoolean) truth).getBoolean()) {
                                    // Theorem: Parallelism
                                    discoveryPool.addParallelism(l1, l2).setTrivial(false);
                                }
                                ap.remove();
                                aap.remove();
                            }
                            gl1.remove();
                            gl2.remove();
                            ajp1.remove();
                            ajp2.remove();
                        }
                    }
                }
            }

            // Third round: Draw all lines from the discovery pool
            // (those that are not yet drawn):
            for (ParallelLines pl : discoveryPool.directions) {
                if (pl.isTheorem()) {
                    boolean showIt = false;
                    HashSet<Line> linesDrawn = new HashSet<>();
                    HashSet<Line> linesToDraw = new HashSet<>();
                    for (Line l : pl.getLines()) {
                        if (l.getPoints().contains(p0)) {
                            showIt = true;
                        }
                        if (alreadyDrawn(l)) {
                            linesDrawn.add(l);
                        } else {
                            linesToDraw.add(l);
                        }
                    }
                    if (showIt) {
                        pl.setGeoLines(addOutputLines(linesDrawn, linesToDraw));
                    }
                }
            }
        }
    }


    private boolean are3Collinear(GeoPoint A, GeoPoint B, GeoPoint C, GeoPoint D) {
        if (GeoPoint.collinear(A, B, C) || GeoPoint.collinear(A, B, D) || GeoPoint.collinear(A, C, D)
                || GeoPoint.collinear(B, C, D)) {
            return true;
        }
        return false;
    }

    private void detectOrthogonalCollinearities() {
        Pool discoveryPool = cons.getDiscoveryPool();
        for (GeoElement ortholine : cons.getGeoSetLabelOrder()) {
            if (ortholine instanceof GeoLine && ortholine.getParentAlgorithm() instanceof AlgoOrthoLinePointLine) {
                GeoPoint startpoint = ((GeoLine) ortholine).getStartPoint();
                HashSet<GeoPoint> ortholinepoints = new HashSet<>();
                GeoPoint secondpoint = null;
                // ortholinepoints.add(startpoint); // it is always there, no point to store it and waste memory
                for (GeoElement point : cons.getGeoSetLabelOrder()) {
                    if (point instanceof GeoPoint) {
                        AlgoElement ae = point.getParentAlgorithm();
                        if (ae instanceof AlgoIntersectLines) {
                            GeoLine line1 = (GeoLine) ae.getInput(0);
                            GeoLine line2 = (GeoLine) ae.getInput(1);
                            if (line1.equals(ortholine) || line2.equals(ortholine)) {
                                if (secondpoint == null) {
                                    secondpoint = (GeoPoint) point;
                                } else {
                                    ortholinepoints.add((GeoPoint) point);
                                }
                            }
                        }
                    }
                }
                if (ortholinepoints.size() > 0) {
                    discoveryPool.addLine(startpoint, secondpoint);
                    for (GeoPoint p : ortholinepoints) {
                        discoveryPool.addCollinearity(startpoint, secondpoint, p).setTrivial(true);
                    }
                }
            }
        }
    }

    public final void initialCompute() {
        Pool discoveryPool = cons.getDiscoveryPool();
        Log.debug("The discovery pool contains " + discoveryPool.lines.size() + " lines, " +
                discoveryPool.circles.size() + " circles and " +
                discoveryPool.directions.size() + " directions.");
        detectOrthogonalCollinearities();
        detectProperties((GeoPoint) this.input);
    }

    private GColor nextColor(GeoElement e) {
        return e.getAutoColorScheme()
                .getNext(true);
    }

    ArrayList<GeoLine> addOutputLines(HashSet<Line> drawn, HashSet<Line> toDraw) {
        ArrayList<GeoLine> ret = new ArrayList<>();
        GColor color = null;
        if (!drawn.isEmpty()) {
            Iterator<Line> it = drawn.iterator();
            Line l1 = it.next();
            GeoLine gl1 = getAlreadyDrawn(l1);
            color = gl1.getAlgebraColor();
        } else {
            if (!cons.getKernel().isSilentMode()) {
                Iterator<Line> it = toDraw.iterator();
                Line l1 = it.next();
                GeoPoint p = l1.getPoints2()[0];
                color = nextColor((GeoElement) p);
            }
        }
        boolean oldMacroMode = cons.isSuppressLabelsActive();
        HashSet<Line> allLines = new HashSet<>();
        allLines.addAll(drawn);
        allLines.addAll(toDraw);
        for (Line l : allLines) {
            GeoLine gl;
            if (drawn.contains(l)) {
                gl = getAlreadyDrawn(l);
            } else {
                GeoPoint[] ps = l.getPoints2();
                AlgoJoinPoints ajp = new AlgoJoinPoints(cons, null, ps[0], ps[1]);
                gl = ajp.getLine();
            }
            if (color != null) {
                gl.setObjColor(color);
            }
            gl.setEuclidianVisible(true);
            gl.setLineType(EuclidianStyleConstants.LINE_TYPE_FULL);
            gl.setLineThickness(2);
            gl.setLabelVisible(true);
            gl.setEuclidianVisible(true);
            gl.setLineType(EuclidianStyleConstants.LINE_TYPE_FULL);
            gl.updateVisualStyle(GProperty.COMBINED);
            cons.setSuppressLabelCreation(oldMacroMode);
            ret.add(gl);
        }
        return ret;
    }

    GeoLine addOutputLine(GeoPoint A, GeoPoint B) {
        boolean oldMacroMode = cons.isSuppressLabelsActive();
        AlgoJoinPoints ajp = new AlgoJoinPoints(cons, null, A, B);
        GeoLine l = ajp.getLine();
        if (!A.getKernel().isSilentMode()) {
            l.setObjColor(nextColor(l));
        }
        l.setEuclidianVisible(true);
        l.setLineType(EuclidianStyleConstants.LINE_TYPE_DASHED_LONG);
        l.setLabelVisible(true);
        l.updateVisualStyle(GProperty.COMBINED); // visibility and style
        cons.setSuppressLabelCreation(oldMacroMode);
        return l;
    }

    GeoConic addOutputCircle(GeoPoint A, GeoPoint B, GeoPoint C) {
        boolean oldMacroMode = cons.isSuppressLabelsActive();
        AlgoCircleThreePoints actp = new AlgoCircleThreePoints(cons, null, A, B, C);
        GeoConic circle = (GeoConic) actp.getCircle();
        if (!A.getKernel().isSilentMode()) {
            circle.setObjColor(nextColor(circle));
        }
        circle.setEuclidianVisible(true);
        circle.setLineType(EuclidianStyleConstants.LINE_TYPE_DASHED_LONG);
        circle.setLabelVisible(true);
        circle.updateVisualStyle(GProperty.COMBINED); // visibility and style
        cons.setSuppressLabelCreation(oldMacroMode);
        return circle;
    }

    private boolean alreadyDrawn(Line l) {
        for (GeoElement ge : cons.getGeoSetLabelOrder()) {
            if (ge instanceof GeoLine && !(ge instanceof GeoSegment)) {
                GeoPoint p1 = ((GeoLine) ge).startPoint;
                GeoPoint p2 = ((GeoLine) ge).endPoint;
                HashSet<GeoPoint> points = l.getPoints();
                if (points.contains(p1) && points.contains(p2)) {
                    return true;
                }
            }
        }
        return false;
    }

    private GeoLine getAlreadyDrawn(Line l) {
        for (GeoElement ge : cons.getGeoSetLabelOrder()) {
            if (ge instanceof GeoLine && !(ge instanceof GeoSegment)) {
                GeoPoint p1 = ((GeoLine) ge).startPoint;
                GeoPoint p2 = ((GeoLine) ge).endPoint;
                HashSet<GeoPoint> points = l.getPoints();
                if (points.contains(p1) && points.contains(p2)) {
                    return (GeoLine) ge;
                }
            }
        }
        return null;
    }

    private boolean alreadyDrawn(Circle c) {
        for (GeoElement ge : cons.getGeoSetLabelOrder()) {
            if (ge instanceof GeoConic && ((GeoConic) ge).isCircle()) {
                ArrayList<GeoPointND> cpoints = ((GeoConic) ge).getPointsOnConic();
                if (cpoints.size() == 3) {
                    GeoPoint p1 = (GeoPoint) cpoints.get(0);
                    GeoPoint p2 = (GeoPoint) cpoints.get(1);
                    GeoPoint p3 = (GeoPoint) cpoints.get(2);
                    HashSet<GeoPoint> points = c.getPoints();
                    if (points.contains(p1) && points.contains(p2) && points.contains(p3)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    // yet unused
    private GeoConic getAlreadyDrawn(Circle c) {
        for (GeoElement ge : cons.getGeoSetLabelOrder()) {
            if (ge instanceof GeoConic && ((GeoConic) ge).isCircle()) {
                ArrayList<GeoPointND> cpoints = ((GeoConic) ge).getPointsOnConic();
                if (cpoints.size() == 3) {
                    GeoPoint p1 = (GeoPoint) cpoints.get(0);
                    GeoPoint p2 = (GeoPoint) cpoints.get(1);
                    GeoPoint p3 = (GeoPoint) cpoints.get(2);
                    HashSet<GeoPoint> points = c.getPoints();
                    if (points.contains(p1) && points.contains(p2) && points.contains(p3)) {
                        return (GeoConic) ge;
                    }
                }
            }
        }
        return null;
    }

    void checkParallelism(GeoLine l1, GeoLine l2) {
        // TODO. To be written.
    }

    void checkConcyclicity(GeoPoint A, GeoPoint B, GeoPoint C, GeoPoint D) {
        // TODO. To be written.
    }

    void checkCollinearity(GeoPoint A, GeoPoint B, GeoPoint C) {
        /*
         * TODO. This is certainly incomplete.
         */
        Pool discoveryPool = cons.getDiscoveryPool();

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
                discoveryPool.addCollinearity(A, B, C).setTrivial(true);
            }
        }
        if (ae instanceof AlgoMidpoint) {
            GeoElement[] inps = ((AlgoMidpoint) ae).getInput();
            if ((inps[0].equals(A) && inps[1].equals(B)) ||
                    (inps[0].equals(B) && inps[1].equals(A))) {
                // C is a midpoint of AB:
                discoveryPool.addCollinearity(A, B, C).setTrivial(true);
            }
        }
        if (ae instanceof AlgoMidpointSegment) {
            GeoSegment seg = (GeoSegment) ((AlgoMidpointSegment) ae).getInput(0);
            GeoPoint p1 = seg.startPoint;
            GeoPoint p2 = seg.endPoint;
            if ((p1.equals(A) && p2.equals(B)) ||
                    (p1.equals(B) && p2.equals(A))) {
                // C is a midpoint of AB:
                discoveryPool.addCollinearity(A, B, C).setTrivial(true);
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
                    discoveryPool.addCollinearity(A, B, C).setTrivial(true);
                }
            }
        }
    }
}