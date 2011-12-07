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

package geogebra.kernel.algos;

import geogebra.common.euclidian.EuclidianConstants;
import geogebra.common.kernel.algos.AlgoElement;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.kernel.Construction;
import geogebra.kernel.geos.GeoConic;
import geogebra.kernel.geos.GeoPoint2;


/**
 *
 * @author  Markus
 * @version 
 */
public class AlgoHyperbolaFociPoint extends AlgoElement {

    private GeoPoint2 A, B, C; // input    
    private GeoConic hyperbola; // output             

    public AlgoHyperbolaFociPoint(
            Construction cons,
            String label,
            GeoPoint2 A,
            GeoPoint2 B,
            GeoPoint2 C) {
    		this(cons, A, B, C);
            hyperbola.setLabel(label);
        }

    public AlgoHyperbolaFociPoint(
            Construction cons,
            GeoPoint2 A,
            GeoPoint2 B,
            GeoPoint2 C) {
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
    GeoPoint2 getFocus1() {
        return A;
    }
    GeoPoint2 getFocus2() {
        return B;
    }

    // compute hyperbola with foci A, B and length of half axis a
    @Override
	public final void compute() {
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
