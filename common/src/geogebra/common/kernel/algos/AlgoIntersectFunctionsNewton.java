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
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.arithmetic.Function;
import geogebra.common.kernel.commands.Commands;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoFunction;
import geogebra.common.kernel.geos.GeoPoint;


/**
 * Finds intersection points of two polynomials
 * (using the roots of their difference)
 * 
 * @author Markus Hohenwarter
 */
public class AlgoIntersectFunctionsNewton extends AlgoRootNewton {
    
    private GeoFunction f, g; // input
    private GeoPoint startPoint, rootPoint;
    
    private Function diffFunction;
    
    public AlgoIntersectFunctionsNewton(Construction cons, 
            GeoFunction f, GeoFunction g, GeoPoint startPoint) {
	    super(cons);
	    this.f = f;
	    this.g = g;
	    this.startPoint = startPoint;
	    
	    diffFunction = new Function(kernel);
	            
	    // output
	    rootPoint = new GeoPoint(cons);
	    setInputOutput(); // for AlgoElement    
	    compute();
    }
    
    public AlgoIntersectFunctionsNewton(Construction cons, String label, 
                GeoFunction f, GeoFunction g, GeoPoint startPoint) {
        this(cons,f,g,startPoint);
        rootPoint.setLabel(label);
    }
    
    @Override
	public Commands getClassName() {
		return Commands.Intersect;
	}
    
    @Override
	public int getRelatedModeID() {
    	return EuclidianConstants.MODE_INTERSECT;
    }
    
    
    // for AlgoElement
    @Override
	protected void setInputOutput() {
        input = new GeoElement[3];      
        input[0] = f;               
        input[1] = g;
        input[2] = startPoint;

        super.setOutputLength(1);
        super.setOutput(0, rootPoint);
        setDependencies();                  
    }
      
    @Override
	public final void compute() {      
        if (!(f.isDefined() && g.isDefined() && startPoint.isDefined())) {        	
            rootPoint.setUndefined();
        } else {
            // get difference f - g            
        	Function.difference(f.getFunction(startPoint.inhomX), g.getFunction(startPoint.inhomX), diffFunction);         
            double x = calcRoot(diffFunction, startPoint.inhomX);
               
            // check if x and g(x) are defined 
            if (Double.isNaN(x) || Double.isNaN(g.evaluate(x))) {
                rootPoint.setUndefined();
                return;
            }
            double y = f.evaluate(x);
            rootPoint.setCoords(x, y, 1.0);
            
            // if we got here we have a new valid rootPoint
        	// in order to make dynamic moving of the intersecting objects
        	// a little bit more stable, we try to be clever here:
        	// let's take the new rootPoints position as the next starting point
        	// for Newton's method. 
        	// Note: we should only do this if the starting point is not labeled,
        	// i.e. not visible on screen (and was probably created by clicking
        	// on an intersection)
            if (!startPoint.isLabelSet() && startPoint.isIndependent() && rootPoint.isDefined()) {
        		startPoint.setCoords(rootPoint);
        	}
        }           
    }
    
    public GeoPoint getIntersectionPoint() {
        return rootPoint;
    }

    @Override
	final public String toString(StringTemplate tpl) {
        // Michael Borcherds 2008-03-31
        // simplified to allow better translation
        return app.getPlain("IntersectionPointOfABWithInitialValueC",
        		input[0].getLabel(tpl),input[1].getLabel(tpl),startPoint.getLabel(tpl));

    }   
}
