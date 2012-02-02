/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.common.kernel.algos;

import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoPoint2;
import geogebra.common.kernel.Construction;

/**
 *
 * @author  Michael
 * @version 
 */
public class AlgoDynamicCoordinates extends AlgoElement {

    private NumberValue x,y; // input
	private GeoPoint2 P; // input
    private GeoPoint2 M; // output        

	
    public AlgoDynamicCoordinates(Construction cons, String label, GeoPoint2 P, NumberValue x, NumberValue y) {
        super(cons);
        this.P = P;
        this.x = x;
        this.y = y;
        // create new Point
        M = new GeoPoint2(cons);
        setInputOutput();

        compute();        
        M.setLabel(label);
    }

    @Override
	public Algos getClassName() {
        return Algos.AlgoDynamicCoordinates;
    }

    // for AlgoElement
    @Override
	protected void setInputOutput() {
        input = new GeoElement[3];
        input[0] = P;
        input[1] = x.toGeoElement();
        input[2] = y.toGeoElement();

        super.setOutputLength(1);
        super.setOutput(0, M);
        setDependencies(); // done by AlgoElement
    }

    public GeoPoint2 getPoint() {
        return M;
    }

    public GeoPoint2 getParentPoint() {
        return P;
    }

    // calc midpoint
    @Override
	public final void compute() {
    	
    	double xCoord = x.getDouble();
    	double yCoord = y.getDouble();
    	
    	if (Double.isNaN(xCoord) || Double.isInfinite(xCoord) ||
    			Double.isNaN(yCoord) || Double.isInfinite(yCoord)) {
    		P.setUndefined();
    		return;
    	}
    	
    	M.setCoords(xCoord, yCoord, 1.0);
    }

    @Override
	final public String toString() {
        // Michael Borcherds 2008-03-30
        // simplified to allow better Chinese translation
    	return app.getPlain("DynamicCoordinatesOfA",P.getLabel());
    }
}
