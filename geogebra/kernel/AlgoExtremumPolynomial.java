/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.kernel;

import geogebra.main.Application;

import java.util.Iterator;


/**
 * Finds all local extrema of a polynomial
 * 
 * @author Markus Hohenwarter
 */
public class AlgoExtremumPolynomial extends AlgoRootsPolynomial {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public AlgoExtremumPolynomial(
        Construction cons,
        String[] labels,
        GeoFunction f) {
        super(cons, labels, f);
        
        
       // Application.debug("AlgoExtremumPolynomial: " + f + ", " + f.cons);
       // Iterator it = f.getVariables().iterator();
       // while (it.hasNext()) {
       // 	GeoElement var = (GeoElement) it.next();
       // 	Application.debug("  " + var + ", " + var.cons );
       // }
    }

    public String getClassName() {
        return "AlgoExtremumPolynomial";
    }

    public GeoPoint[] getExtremumPoints() {
        return super.getRootPoints();
    }

    protected final void compute() {
        if (f.isDefined()) {
            // TODO: remove
            //Application.debug("*** extremum of " + f);
                    	
            yValFunction = f.getFunction();       
            
            // roots of first derivative 
            //(roots without change of sign are removed)
            calcRoots(yValFunction, 1);             
        } else {
            curRealRoots = 0;
        }

        setRootPoints(curRoots, curRealRoots);
    }

    final public String toString() {
        // Michael Borcherds 2008-03-30
        // simplified to allow better Chinese translation
        return app.getPlain("ExtremumOfA",f.getLabel());

    }

}
