/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

/*
 * AlgoMidpointConic.java
 *
 * Created on 11. November 2001, 21:37
 */

package geogebra.kernel;


/**
 * Center of a conic section. 
 */
public class AlgoCenterConic extends AlgoElement {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private GeoConic c; // input
    private GeoPoint midpoint; // output                 

    AlgoCenterConic(Construction cons, String label, GeoConic c) {
        super(cons);
        this.c = c;
        midpoint = new GeoPoint(cons);
        setInputOutput(); // for AlgoElement

        compute();
        midpoint.setLabel(label);
    }

    public String getClassName() {
        return "AlgoCenterConic";
    }

    // for AlgoElement
    protected void setInputOutput() {
        input = new GeoElement[1];
        input[0] = c;

        output = new GeoElement[1];
        output[0] = midpoint;
        setDependencies(); // done by AlgoElement
    }

    GeoConic getConic() {
        return c;
    }
    GeoPoint getPoint() {
        return midpoint;
    }

    protected final void compute() {
        if (!c.isDefined()) {
            midpoint.setUndefined();
            return;
        }

        switch (c.type) {
            case GeoConic.CONIC_CIRCLE :
            case GeoConic.CONIC_ELLIPSE :
            case GeoConic.CONIC_HYPERBOLA :
            case GeoConic.CONIC_SINGLE_POINT :
            case GeoConic.CONIC_INTERSECTING_LINES :
                midpoint.setCoords(c.b.x, c.b.y, 1.0d);
                break;

            default :
                // midpoint undefined
                midpoint.setUndefined();
        }
    }

    final public String toString() {
        // Michael Borcherds 2008-03-30
        // simplified to allow better Chinese translation
    	return app.getPlain("CenterOfA",c.getLabel());
    }
}
