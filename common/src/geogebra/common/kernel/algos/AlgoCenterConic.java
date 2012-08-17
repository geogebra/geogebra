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

package geogebra.common.kernel.algos;

import geogebra.common.euclidian.EuclidianConstants;
import geogebra.common.kernel.Construction;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.geos.GeoConic;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoPoint;
import geogebra.common.kernel.kernelND.GeoConicNDConstants;


/**
 * Center of a conic section. 
 */
public class AlgoCenterConic extends AlgoElement {

    private GeoConic c; // input
    private GeoPoint midpoint; // output                 

    public AlgoCenterConic(Construction cons, String label, GeoConic c) {
        super(cons);
        this.c = c;
        midpoint = new GeoPoint(cons);
        setInputOutput(); // for AlgoElement

        compute();
        midpoint.setLabel(label);
    }

    @Override
	public Algos getClassName() {
        return Algos.AlgoCenterConic;
    }

    @Override
	public int getRelatedModeID() {
    	return EuclidianConstants.MODE_MIDPOINT;
    }
    
    // for AlgoElement
    @Override
	protected void setInputOutput() {
        input = new GeoElement[1];
        input[0] = c;

        super.setOutputLength(1);
        super.setOutput(0, midpoint);
        setDependencies(); // done by AlgoElement
    }

    GeoConic getConic() {
        return c;
    }
    public GeoPoint getPoint() {
        return midpoint;
    }

    @Override
	public final void compute() {
        if (!c.isDefined()) {
            midpoint.setUndefined();
            return;
        }

        switch (c.type) {
            case GeoConicNDConstants.CONIC_CIRCLE :
            case GeoConicNDConstants.CONIC_ELLIPSE :
            case GeoConicNDConstants.CONIC_HYPERBOLA :
            case GeoConicNDConstants.CONIC_SINGLE_POINT :
            case GeoConicNDConstants.CONIC_INTERSECTING_LINES :
                midpoint.setCoords(c.b.getX(), c.b.getY(), 1.0d);
                break;

            default :
                // midpoint undefined
                midpoint.setUndefined();
        }
    }

    @Override
	final public String toString(StringTemplate tpl) {
        // Michael Borcherds 2008-03-30
        // simplified to allow better Chinese translation
    	return app.getPlain("CenterOfA",c.getLabel(tpl));
    }

	// TODO Consider locusequability
}
