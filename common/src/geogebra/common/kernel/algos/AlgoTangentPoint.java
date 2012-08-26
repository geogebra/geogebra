/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

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
public class AlgoTangentPoint extends AlgoElement implements TangentAlgo {

	private GeoPoint P; // input
    private GeoConic c; // input
    private GeoLine[] tangents; // output  

    private GeoLine polar;
    private AlgoIntersectLineConic algoIntersect;
    private GeoPoint[] tangentPoints;
    private boolean equalLines = false;

    /*  
    AlgoTangentPoint(Construction cons, String label, GeoPoint P, GeoConic c) {
      this(cons, P, c);
      GeoElement.setLabels(label, tangents);            
    }*/

    public AlgoTangentPoint(
        Construction cons,
        String[] labels,
        GeoPoint P,
        GeoConic c) {
        this(cons, P, c);
        GeoElement.setLabels(labels, tangents);
    }

    AlgoTangentPoint(Construction cons, GeoPoint P, GeoConic c) {
        super(cons);
        this.P = P;
        this.c = c;

        // the tangents are computed by intersecting the
        // polar line of P with c
        polar = new GeoLine(cons);
        c.polarLine(P, polar);
        algoIntersect = new AlgoIntersectLineConic(cons, polar, c);
        //  this is only an internal Algorithm that shouldn't be in the construction list
        cons.removeFromConstructionList(algoIntersect);
        tangentPoints = algoIntersect.getIntersectionPoints();

        tangents = new GeoLine[2];
        tangents[0] = new GeoLine(cons);
        tangents[1] = new GeoLine(cons);
        tangents[0].setStartPoint(P);
        tangents[1].setStartPoint(P);

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
    }

    @Override
	public Algos getClassName() {
        return Algos.AlgoTangentPoint;
    }

    @Override
	public int getRelatedModeID() {
    	return EuclidianConstants.MODE_TANGENTS;
    }
    
    // for AlgoElement
    @Override
	protected void setInputOutput() {
        input = new GeoElement[2];
        input[0] = P;
        input[1] = c;

        super.setOutput(tangents);
        setDependencies(); // done by AlgoElement
    }

    public GeoLine[] getTangents() {
        return tangents;
    }
    // Made public for LocusEqu
    public GeoPoint getPoint() {
        return P;
    }
    // Made public for LocusEqu
    public GeoConic getConic() {
        return c;
    }       

    /**
     * return intersection point of tangent line and conic c.
     * return null if line is not defined as tangent of conic c.
     */
    GeoPoint getTangentPoint(GeoConic conic, GeoLine line) {
        if (conic != c)
            return null;

        if (line == tangents[0])
			return tangentPoints[0];
		else if (line == tangents[1])
			return tangentPoints[1];
		else
            return null;
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
    }

    // calc tangents
    @Override
	public final void compute() {
        // degenerates should not have any tangents
        if (c.isDegenerate()) {
            tangents[0].setUndefined();
            tangents[1].setUndefined();
            return;
        }

        // update polar line
        c.polarLine(P, polar);

        // if P lies on the conic, the polar is a tangent        
        if (c.isIntersectionPointIncident(P, Kernel.MIN_PRECISION) || P.getIncidenceList().contains(c)) {
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
    }

    @Override
	public final String toString(StringTemplate tpl) {
        // Michael Borcherds 2008-03-30
        // simplified to allow better Chinese translation
    	return app.getPlain("TangentToAThroughB",c.getLabel(tpl),P.getLabel(tpl));

    }

	@Override
	public boolean isLocusEquable() {
		return true;
	}
}
