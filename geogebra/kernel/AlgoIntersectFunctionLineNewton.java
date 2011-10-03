/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.kernel;

import geogebra.euclidian.EuclidianConstants;
import geogebra.kernel.arithmetic.Function;


/**
 * Finds intersection points of two polynomials
 * (using the roots of their difference)
 * 
 * @author Markus Hohenwarter
 */
public class AlgoIntersectFunctionLineNewton extends AlgoRootNewton {
    
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private GeoFunction f; // input
    private GeoLine line; // input
    private GeoPoint startPoint, rootPoint;
    
    private Function diffFunction;    
                
    public AlgoIntersectFunctionLineNewton(Construction cons, String label, 
                GeoFunction f, GeoLine line, GeoPoint startPoint) {
        super(cons);
        this.f = f;
        this.line = line;
        this.startPoint = startPoint;
                
        diffFunction = new Function(kernel);
                
        // output
        rootPoint = new GeoPoint(cons);
        setInputOutput(); // for AlgoElement    
        compute();
        rootPoint.setLabel(label);
    }
    
    public String getClassName() {
        return "AlgoIntersectFunctionLineNewton";
    }
    
    public int getRelatedModeID() {
    	return EuclidianConstants.MODE_INTERSECT;
    }
    
    
    // for AlgoElement
    protected void setInputOutput() {
        input = new GeoElement[3];      
        input[0] = f;               
        input[1] = line;
        input[2] = startPoint;
        
        output = new GeoPoint[1];
        output[0] = rootPoint;      
        setDependencies();                  
    }
    
    protected final void compute() {          	
        if (!(f.isDefined() && line.isDefined() && startPoint.isDefined())) {           
            rootPoint.setUndefined();
        } else {
            double x;
            //  check for vertical line a*x + c = 0: intersection at x=-c/a 
            if (kernel.isZero(line.y)) {
                x = -line.z / line.x;                               
            } 
            // standard case
            else {
                //get difference f - line
                Function.difference(f.getFunction(startPoint.inhomX), line, diffFunction);            	                
                x = calcRoot(diffFunction, startPoint.inhomX);                                 
            }               
                        
            if (Double.isNaN(x)) {
                rootPoint.setUndefined();
                return;
            }
            double y = f.evaluate(x);
            rootPoint.setCoords(x, y, 1.0);
            
            // check if the intersection point really is on the line
            // this is important for segments and rays            
        	if (!line.isIntersectionPointIncident(rootPoint, Kernel.MIN_PRECISION) ) {
        		 rootPoint.setUndefined();
                 return;       	                
            } 
        	
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

    final public String toString() {
        // Michael Borcherds 2008-03-31
        // simplified to allow better translation
        return app.getPlain("IntersectionPointOfABWithInitialValueC",input[0].getLabel(),input[1].getLabel(),startPoint.getLabel());

    }   
}
