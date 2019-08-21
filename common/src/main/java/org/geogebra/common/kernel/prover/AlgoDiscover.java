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
            this.output[i].setLineType(EuclidianStyleConstants.LINE_TYPE_DASHED_LONG);
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

    public final void initialCompute() {

        if (this.input instanceof GeoPoint) {
            HashSet<GeoPoint> notChildren = new HashSet<GeoPoint>();
            for (GeoElement ge : cons.getGeoSetLabelOrder()) {
                if (ge instanceof GeoPoint && !ge.equals((this.input)) && !ge.getAllPredecessors().contains(this.input)) {
                    notChildren.add((GeoPoint) ge);
                }
            }

            Combinations lines = new Combinations(notChildren, 2);
            while (lines.hasNext()) {
                Set<GeoPoint> line = lines.next();
                Iterator<GeoPoint> i = line.iterator();
                GeoPoint p1 = i.next();
                GeoPoint p2 = i.next();
                AlgoAreCollinear aac = new AlgoAreCollinear(cons, (GeoPoint) this.input, p1, p2);
                if (aac.getResult().getBoolean()) {
                    // Conjecture: Collinearity
                    GeoElement root = new GeoBoolean(cons);
                    root.setParentAlgorithm(aac);
                    AlgoProveDetails ap = new AlgoProveDetails(cons, root);
                    ap.compute();
                    GeoElement[] o = ap.getOutput();
                    GeoElement truth = ((GeoList) o[0]).get(0);
                    if (((GeoBoolean) truth).getBoolean()) {
                        if (!isTrivialCollinearity(p1,p2,(GeoPoint) this.input) &&
                                !isTrivialCollinearity(p2,(GeoPoint) this.input,p1) &&
                                !isTrivialCollinearity((GeoPoint) this.input,p1,p2)) {
                            // Theorem: Collinearity
                            addOutputLine(p1, p2);
                        }
                    }
                }
            }

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
            }
        }
    }

    void addOutputLine(GeoPoint A, GeoPoint B) {
        boolean oldMacroMode = cons.isSuppressLabelsActive();
        AlgoJoinPoints ajp = new AlgoJoinPoints(cons, null, A, B);
        GeoLine l = ajp.getLine();
        l.setObjColor(GColor.PURPLE);
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
        circle.setObjColor(GColor.PURPLE);
        circle.setEuclidianVisible(true);
        circle.setLineType(EuclidianStyleConstants.LINE_TYPE_DASHED_LONG);
        circle.setLabelVisible(true);
        circle.updateVisualStyle(GProperty.COMBINED); // visibility and style
        cons.setSuppressLabelCreation(oldMacroMode);
        output_wip.add(circle);
    }

    boolean isTrivialCollinearity(GeoPoint A, GeoPoint B, GeoPoint C) {
        /*
         * FIXME. This is incomplete (e.g. intersection of lines is missing)
         * and badly organized. Instead, there should be a set of lines
         * created: each element should contain a set of points that
         * are lying on a given line.
         */
        AlgoElement ae = C.getParentAlgorithm();
        if (ae instanceof AlgoMidpoint) {
            GeoElement[] inps = ((AlgoMidpoint) ae).getInput();
            if ((inps[0].equals(A) && inps[1].equals(B)) ||
                    (inps[0].equals(B) && inps[1].equals(A))) {
                // C is a midpoint of AB:
                return true;
            }
        }
        if (ae instanceof AlgoMidpointSegment) {
            GeoSegment seg = (GeoSegment) ((AlgoMidpointSegment) ae).getInput(0);
            GeoPoint p1 = seg.startPoint;
            GeoPoint p2 = seg.endPoint;
            if ((p1.equals(A) && p2.equals(B)) ||
                    (p1.equals(B) && p2.equals(A))) {
                // C is a midpoint of AB:
                return true;
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
                    return true;
                }
            }
        }

        return false;
    }
}
