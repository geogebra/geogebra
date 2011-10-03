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
  * Arc or sector defined by a conic, start- and end-parameter.
  */
public class AlgoConicPartConicParameters extends AlgoConicPart {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
     * Creates a new arc or sector algorithm.
     * The type is either GeoConicPart.CONIC_PART_ARC or 
     * GeoConicPart.CONIC_PART_ARC       
     */
    AlgoConicPartConicParameters(Construction cons, String label,
    		GeoConic circle, NumberValue startParameter, NumberValue endParameter,
    		int type) {
        super(cons, type);        
        conic = circle;
        startParam = startParameter;
        endParam = endParameter;        
        
        conicPart = new GeoConicPart(cons, type);
        setInputOutput(); // for AlgoElement      
        compute();
        
        conicPart.setLabel(label);
    }    	

    // for AlgoElement
    protected void setInputOutput() {
        input = new GeoElement[3];
        input[0] = conic;      
        input[1] = startParam.toGeoElement();
        input[2] = endParam.toGeoElement();

        output = new GeoElement[1];
        output[0] = conicPart;

        setDependencies();
    }

    
}
