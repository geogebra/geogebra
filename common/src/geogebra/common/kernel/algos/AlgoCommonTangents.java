/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

/*
 * AlgoCommonTangents.java, dsun48 [6/26/2011]
 *
 */

package geogebra.common.kernel.algos;

import geogebra.common.euclidian.EuclidianConstants;
import geogebra.common.kernel.Construction;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.geos.GeoConic;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoLine;
import geogebra.common.kernel.geos.GeoPoint;
import geogebra.common.kernel.geos.GeoVec3D;

/**
 * Two tangents through point P to conic section c
 */
public class AlgoCommonTangents extends AlgoElement {

    private GeoPoint P, P2; // tmp
    private GeoConic c, c2; // input
    private GeoLine[] tangents; // output  

    private GeoLine polar, polar2;
    private AlgoIntersectLineConic algoIntersect, algoIntersect2;
    private GeoPoint[] tangentPoints, tangentPoints2;
    private boolean equalLines = false, equalLines2 = false;

    public AlgoCommonTangents(
                       Construction cons,
                       String[] labels,
                       GeoConic c,
                       GeoConic c2) {
        this(cons, c, c2);
        GeoElement.setLabels(labels, tangents);
    }

    AlgoCommonTangents(Construction cons, GeoConic c, GeoConic c2) {
        super(cons);
        this.c = c;
        this.c2 = c2;

        double r = c.getCircleRadius();
        double r2 = c2.getCircleRadius();

        // outer
        P = new GeoPoint(cons);
        if( Math.abs(r2-r) > Kernel.MIN_PRECISION) {
            P.setCoords((c.b.getX()*r2-c2.b.getX()*r)/(r2-r),
                        (c.b.getY()*r2-c2.b.getY()*r)/(r2-r), 1.0d);
        } else {
            P.setCoords((c.b.getX()*r2-c2.b.getX()*r),
                        (c.b.getY()*r2-c2.b.getY()*r), 0.0d);
        }
        // the tangents are computed by intersecting the
        // polar line of P with c
        polar = new GeoLine(cons);
        c.polarLine(P, polar);
        algoIntersect = new AlgoIntersectLineConic(cons, polar, c);
        //  this is only an internal Algorithm that shouldn't be in the construction list
        cons.removeFromConstructionList(algoIntersect);
        tangentPoints = algoIntersect.getIntersectionPoints();

        // inner
        P2 = new GeoPoint(cons);
        P2.setCoords((c.b.getX()*r2+c2.b.getX()*r)/(r2+r),
                     (c.b.getY()*r2+c2.b.getY()*r)/(r2+r), 1.0d);
        // the tangents are computed by intersecting the
        // polar line of P with c
        polar2 = new GeoLine(cons);
        c2.polarLine(P2, polar2);
        algoIntersect2 = new AlgoIntersectLineConic(cons, polar2, c2);
        //  this is only an internal Algorithm that shouldn't be in the construction list
        cons.removeFromConstructionList(algoIntersect2);
        tangentPoints2 = algoIntersect2.getIntersectionPoints();

        tangents = new GeoLine[2+2];
        tangents[0] = new GeoLine(cons);
        tangents[1] = new GeoLine(cons);
        tangents[0].setStartPoint(P);
        tangents[1].setStartPoint(P);

        tangents[0+2] = new GeoLine(cons);
        tangents[1+2] = new GeoLine(cons);
        tangents[0+2].setStartPoint(P2);
        tangents[1+2].setStartPoint(P2);

        setInputOutput(); // for AlgoElement

        compute();

        // check if both lines are equal after creation:
        // if they are equal we started with a point on the conic section
        // in this case we only want to see one tangent line,
        // so we make the second one undefined
        equalLines = tangents[0].isEqual(tangents[1]);
        if (equalLines) {        
            tangents[1].setUndefined();
            tangentPoints[1].setUndefined();
        }
        equalLines2 = tangents[0+2].isEqual(tangents[1+2]);
        if (equalLines2) {        
            tangents[1+2].setUndefined();
            tangentPoints2[1].setUndefined();
        }
    }

    @Override
	public Algos getClassName() {
        return Algos.AlgoCommonTangents;
    }

    @Override
	public int getRelatedModeID() {
        return EuclidianConstants.MODE_TANGENTS;
    }
    
    // for AlgoElement
    @Override
	protected void setInputOutput() {
        input = new GeoElement[2];
        input[0] = c;
        input[1] = c2;

        super.setOutput(tangents);
        setDependencies(); // done by AlgoElement
    }

    public GeoLine[] getTangents() {
        return tangents;
    }

    /**
     * Inits the helping interesection algorithm to take
     * the current position of the lines into account.
     * This is important so the the tangent lines are not
     * switched after loading a file
     */
    @Override
	public void initForNearToRelationship() {
        // if first tangent point is not on first tangent,
        // we switch the intersection points
        if (!tangents[0].isOnFullLine(tangentPoints[0], Kernel.MIN_PRECISION)) {
            algoIntersect.initForNearToRelationship();
            // remember first point
            double px = tangentPoints[0].x;
            double py = tangentPoints[0].y;
            double pz = tangentPoints[0].z;
            // first = second
            algoIntersect.setIntersectionPoint(0, tangentPoints[1]);
            // second = first
            tangentPoints[1].setCoords(px, py, pz);
            algoIntersect.setIntersectionPoint(1, tangentPoints[1]);
        }
        if (!tangents[0+2].isOnFullLine(tangentPoints2[0], Kernel.MIN_PRECISION)) {
            algoIntersect2.initForNearToRelationship();
            // remember first point
            double px = tangentPoints2[0].x;
            double py = tangentPoints2[0].y;
            double pz = tangentPoints2[0].z;
            // first = second
            algoIntersect2.setIntersectionPoint(0, tangentPoints2[1]);
            // second = first
            tangentPoints2[1].setCoords(px, py, pz);
            algoIntersect2.setIntersectionPoint(1, tangentPoints2[1]);
        }
    }

    // calc tangents
    @Override
	public final void compute() {

        if( !c.isCircle() || !c2.isCircle() ) {
            for(int i=0; i<4; i++) {
                tangents[i].setUndefined();
            }
            return;
        }

        double r = c.getCircleRadius();
        double r2 = c2.getCircleRadius();

        // outer
        if( Math.abs(r2-r) > Kernel.MIN_PRECISION) {
            P.setCoords((c.b.getX()*r2-c2.b.getX()*r)/(r2-r),
                        (c.b.getY()*r2-c2.b.getY()*r)/(r2-r), 1.0d);
        } else {
            P.setCoords((c.b.getX()*r2-c2.b.getX()*r),
                        (c.b.getY()*r2-c2.b.getY()*r), 0.0d);
        }
        // update polar line
        c.polarLine(P, polar);
        // if P lies on the conic, the polar is a tangent        
        if (c.isIntersectionPointIncident(P, Kernel.MIN_PRECISION)) {
            tangents[0].setCoords(polar);
            tangentPoints[0].setCoords(P);
            // check if we had equal lines at the beginning
            // if so we still don't want to see the second line
            if (equalLines) {
                tangents[1].setUndefined();
                tangentPoints[1].setUndefined();
            } else {
                tangents[1].setCoords(polar);
                tangentPoints[1].setCoords(P);
            }
        }
        // if P is not on the conic, the tangents pass through
        // the intersection points of polar and conic
        else {
            // intersect polar line with conic -> tangentPoints
            algoIntersect.update();
            // calc tangents through tangentPoints
            GeoVec3D.lineThroughPoints(P, tangentPoints[0], tangents[0]);
            GeoVec3D.lineThroughPoints(P, tangentPoints[1], tangents[1]);
            // we no longer have equal lines (if we ever had them)
            equalLines = false;
        }

        // inner
        P2.setCoords((c.b.getX()*r2+c2.b.getX()*r)/(r2+r),
                     (c.b.getY()*r2+c2.b.getY()*r)/(r2+r), 1.0d);
        // update polar line
        c2.polarLine(P2, polar2);
        // if P lies on the conic, the polar is a tangent        
        if (c2.isIntersectionPointIncident(P2, Kernel.MIN_PRECISION)) {
            tangents[0+2].setCoords(polar2);
            tangentPoints2[0].setCoords(P2);
            // check if we had equal lines at the beginning
            // if so we still don't want to see the second line
            if (equalLines2) {
                tangents[1+2].setUndefined();
                tangentPoints2[1].setUndefined();
            } else {
            	tangents[1+2].setCoords(polar2);
            	tangentPoints2[1].setCoords(P2);
            }
        }
        // if P is not on the conic, the tangents pass through
        // the intersection points of polar and conic
        else {
            // intersect polar line with conic -> tangentPoints
            algoIntersect2.update();
            // calc tangents through tangentPoints
            GeoVec3D.lineThroughPoints(P2, tangentPoints2[0], tangents[0+2]);
            GeoVec3D.lineThroughPoints(P2, tangentPoints2[1], tangents[1+2]);
            // we no longer have equal lines (if we ever had them)
            equalLines2 = false;
        }

    } // end of compute

    @Override
	public final String toString(StringTemplate tpl) {
        // Michael Borcherds 2008-03-30
        // simplified to allow better Chinese translation
        return app.getPlain("CommonTangentOfCirclesAandB", c.getLabel(tpl), c2.getLabel(tpl));
    }

	// TODO Consider locusequability
}

// Local Variables:
// indent-tabs-mode: nil
// c-basic-offset: 4
// tab-width: 4
// End:
// vim: set expandtab shiftwidth=4 softtabstop=4 tabstop=4
