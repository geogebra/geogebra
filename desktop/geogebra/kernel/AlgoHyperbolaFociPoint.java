/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

/*
 * AlgoHyperbolaFociLength.java
 *
 * Created on 15. November 2001, 21:37
 */

package geogebra.kernel;

import geogebra.euclidian.EuclidianConstants;


/**
 *
 * @author  Markus
 * @version 
 */
public class AlgoHyperbolaFociPoint extends AlgoElement {

    private GeoPoint A, B, C; // input    
    private GeoConic hyperbola; // output             

    AlgoHyperbolaFociPoint(
            Construction cons,
            String label,
            GeoPoint A,
            GeoPoint B,
            GeoPoint C) {
    		this(cons, A, B, C);
            hyperbola.setLabel(label);
        }

    public AlgoHyperbolaFociPoint(
            Construction cons,
            GeoPoint A,
            GeoPoint B,
            GeoPoint C) {
            super(cons);
            this.A = A;
            this.B = B;
            this.C = C;
            hyperbola = new GeoConic(cons);
            setInputOutput(); // for AlgoElement

            compute();
        }

    @Override
	public String getClassName() {
        return "AlgoHyperbolaFociPoint";
    }
    
    @Override
	public int getRelatedModeID() {
    	return EuclidianConstants.MODE_HYPERBOLA_THREE_POINTS;
    }
    

    // for AlgoElement
    @Override
	protected void setInputOutput() {
        input = new GeoElement[3];
        input[0] = A;
        input[1] = B;
        input[2] = C;

        super.setOutputLength(1);
        super.setOutput(0, hyperbola);
        setDependencies(); // done by AlgoElement
    }

    public GeoConic getHyperbola() {
        return hyperbola;
    }
    GeoPoint getFocus1() {
        return A;
    }
    GeoPoint getFocus2() {
        return B;
    }

    // compute hyperbola with foci A, B and length of half axis a
    @Override
	protected final void compute() {
		double xyA[] = new double[2];
		double xyB[] = new double[2];
		double xyC[] = new double[2];
		A.getInhomCoords(xyA);
		B.getInhomCoords(xyB);
		C.getInhomCoords(xyC);
		
		double length = Math.sqrt((xyA[0]-xyC[0])*(xyA[0]-xyC[0])+(xyA[1]-xyC[1])*(xyA[1]-xyC[1])) -
		Math.sqrt((xyB[0]-xyC[0])*(xyB[0]-xyC[0])+(xyB[1]-xyC[1])*(xyB[1]-xyC[1]));		
		
		length = Math.abs(length);
    	
		hyperbola.setEllipseHyperbola(A, B, length/2);
    }

    @Override
	final public String toString() {
        return app.getPlain("HyperbolaWithFociABPassingThroughC",A.getLabel(),B.getLabel(),C.getLabel());               
    }
}
