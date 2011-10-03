/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.kernel;

import geogebra.kernel.arithmetic.NumberValue;


/**
 *
 * @author  Michael
 * @version 
 */
public class AlgoDynamicCoordinates extends AlgoElement {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private NumberValue x,y; // input
	private GeoPoint P; // input
    private GeoPoint M; // output        

	
    AlgoDynamicCoordinates(Construction cons, String label, GeoPoint P, NumberValue x, NumberValue y) {
        super(cons);
        this.P = P;
        this.x = x;
        this.y = y;
        // create new Point
        M = new GeoPoint(cons);
        setInputOutput();

        compute();        
        M.setLabel(label);
    }

    public String getClassName() {
        return "AlgoDynamicCoordinates";
    }

    // for AlgoElement
    protected void setInputOutput() {
        input = new GeoElement[3];
        input[0] = P;
        input[1] = x.toGeoElement();
        input[2] = y.toGeoElement();

        output = new GeoElement[1];
        output[0] = M;
        setDependencies(); // done by AlgoElement
    }

    GeoPoint getPoint() {
        return M;
    }

    public GeoPoint getParentPoint() {
        return P;
    }

    // calc midpoint
    protected final void compute() {
    	
    	double xCoord = x.getDouble();
    	double yCoord = y.getDouble();
    	
    	if (Double.isNaN(xCoord) || Double.isInfinite(xCoord) ||
    			Double.isNaN(yCoord) || Double.isInfinite(yCoord)) {
    		P.setUndefined();
    		return;
    	}
    	
    	M.setCoords(xCoord, yCoord, 1.0);
    	
    }

    final public String toString() {
        // Michael Borcherds 2008-03-30
        // simplified to allow better Chinese translation
    	return app.getPlain("DynamicCoordinatesOfA",P.getLabel());

    }
}
