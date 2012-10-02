/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

/*
 * AlgoEllipseFociPoint.java
 * 
 * Ellipse with Foci A and B passing through point C
 *
 * Michael Borcherds
 * 2008-04-06
 * adapted from EllipseFociLength
 */

package geogebra.common.kernel.algos;

import geogebra.common.euclidian.EuclidianConstants;
import geogebra.common.kernel.Construction;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.geos.GeoConic;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoPoint;

/**
 *
 * @author  Markus
 * @version 
 */
public class AlgoEllipseFociPoint extends AlgoElement {

    private GeoPoint A, B, C; // input    
    private GeoConic ellipse; // output             

    public AlgoEllipseFociPoint(
            Construction cons,
            String label,
            GeoPoint A,
            GeoPoint B,
            GeoPoint C) {
        	this(cons, A, B, C);
            ellipse.setLabel(label);
        }

    public AlgoEllipseFociPoint(
            Construction cons,
            GeoPoint A,
            GeoPoint B,
            GeoPoint C) {
            super(cons);
            this.A = A;
            this.B = B;
            this.C = C;
            ellipse = new GeoConic(cons);
            setInputOutput(); // for AlgoElement

            compute();
        	addIncidence();
        }

    /**
     * @author Tam
     * 
     * for special cases of e.g. AlgoIntersectLineConic
     */
	private void addIncidence() {
		if (C != null)
			C.addIncidence( ellipse);

	}

    @Override
	public Algos getClassName() {
        return Algos.AlgoEllipseFociPoint;
    }
    
    @Override
	public int getRelatedModeID() {
    	return EuclidianConstants.MODE_ELLIPSE_THREE_POINTS;
    }
    

    // for AlgoElement
    @Override
	protected void setInputOutput() {
        input = new GeoElement[3];
        input[0] = A;
        input[1] = B;
        input[2] = C;

        super.setOutputLength(1);
        super.setOutput(0, ellipse);
        setDependencies(); // done by AlgoElement
    }

    public GeoConic getEllipse() {
        return ellipse;
    }
    
    public GeoPoint getFocus1() {
    	// Public for LocusEqu
        return A;
    }
    public GeoPoint getFocus2() {
    	// Public for LocusEqu
        return B;
    }
    
    /**
     * Method for LocusEqu
     * @return returns external point for ellipse.
     */
    public GeoPoint getExternalPoint() {
    	return C;
    }

    // compute ellipse with foci A, B passing through C
    @Override
	public final void compute() {
    	
		double xyA[] = new double[2];
		double xyB[] = new double[2];
		double xyC[] = new double[2];
		A.getInhomCoords(xyA);
		B.getInhomCoords(xyB);
		C.getInhomCoords(xyC);
		
		double length = Math.sqrt((xyA[0]-xyC[0])*(xyA[0]-xyC[0])+(xyA[1]-xyC[1])*(xyA[1]-xyC[1])) +
		Math.sqrt((xyB[0]-xyC[0])*(xyB[0]-xyC[0])+(xyB[1]-xyC[1])*(xyB[1]-xyC[1]));
    	
        ellipse.setEllipseHyperbola(A, B, length/2);
    }

    @Override
	final public String toString(StringTemplate tpl) {
        return app.getPlain("EllipseWithFociABPassingThroughC",A.getLabel(tpl),
        		B.getLabel(tpl),C.getLabel(tpl));
    }

	@Override
	public boolean isLocusEquable() {
		return true;
	}
}
