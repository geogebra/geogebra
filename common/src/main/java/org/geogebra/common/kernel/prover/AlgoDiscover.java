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
import org.geogebra.common.kernel.prover.discovery.Pool;
import org.geogebra.common.plugin.EuclidianStyleConstants;

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
        ;
        for (GeoElement ge : ges) {
            if (ge instanceof GeoPoint && !p.equals(ge)) {
                collectCollinearites((GeoPoint) ge, false);
                collectCollinearites((GeoPoint) ge, false);
            }
        }
        collectCollinearites(p, true);
        collectConcyclicities(p, true);
    }

    /*
     * Extend the database of collinearities by
     * collecting all of them for a given input.
     */
    private void collectCollinearites(GeoPoint p0, boolean discover) {
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
                        ap.remove();
                    }
                    aac.remove();
                }
            }

            // Third round: Draw lines from the discovery pool
            // (those that are not yet drawn):
            for (Line l : discoveryPool.lines) {
                if (!alreadyDrawn(l)) {
                    GeoPoint[] twopoints = l.getPoints2();
                    if (l.getPoints().contains(p0)) {
                        l.setGeoLine(addOutputLine(twopoints[0], twopoints[1]));
                    }
                }
            }
        }
    }

    /*
     * Extend the database of collinearities by
     * collecting all of them for a given input.
     */
    private void collectConcyclicities(GeoPoint p0, boolean discover) {
        Pool trivialPool = this.input.getKernel().getApplication().getTrivialPool();
        Pool discoveryPool = this.input.getKernel().getApplication().getDiscoveryPool();

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

            if (!trivialPool.areConcyclic(p0, p1, p2, p3) &&
                    !discoveryPool.areConcyclic(p0, p1, p2, p3)) {
                // Add {p0,p1,p2,p3} to the trivial pool if they are trivially concyclic:
                checkConcyclicity(p0, p1, p2, p3);
            }
            if (!trivialPool.areConcyclic(p0, p1, p2, p3)) {
                trivialPool.addCircle(p0, p1, p2);
                trivialPool.addCircle(p0, p1, p3);
                trivialPool.addCircle(p0, p2, p3);
                trivialPool.addCircle(p1, p2, p3);
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
                        !trivialPool.areConcyclic(p0, p1, p2, p3) &&
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
                            discoveryPool.addConcyclicity(p0, p1, p2, p3);
                        }
                        ap.remove();
                    }
                    aac.remove();
                }
            }

            // Third round: Draw circles from the discovery pool
            // (those that are not yet drawn):
            for (Circle c : discoveryPool.circles) {
                if (!alreadyDrawn(c)) {
                    GeoPoint[] threepoints = c.getPoints3();
                    if (c.getPoints().contains(p0)) {
                        c.setGeoConic(addOutputCircle(threepoints[0], threepoints[1], threepoints[2]));
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
        Pool trivialPool = this.input.getKernel().getApplication().getTrivialPool();
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
                    trivialPool.addLine(startpoint, secondpoint);
                    for (GeoPoint p : ortholinepoints) {
                        trivialPool.addCollinearity(startpoint, secondpoint, p);
                    }
                }
            }
        }
    }

    public final void initialCompute() {
        detectOrthogonalCollinearities();
        detectProperties((GeoPoint) this.input);
    }

    private GColor nextColor(GeoElement e) {
        return e.getAutoColorScheme()
                .getNext(true);
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
            if (ge instanceof GeoLine) {
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

    void checkConcyclicity(GeoPoint A, GeoPoint B, GeoPoint C, GeoPoint D) {
        // TODO. To be written.
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