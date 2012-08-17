/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.common.kernel.algos;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.geos.GeoConic;
import geogebra.common.kernel.geos.GeoConicPart;
import geogebra.common.kernel.geos.GeoElement;


/**
  * Arc or sector defined by a conic, start- and end-parameter.
  */
public class AlgoConicPartConicParameters extends AlgoConicPart {

    /**
     * Creates a new arc or sector algorithm.
     * The type is either GeoConicPart.CONIC_PART_ARC or 
     * GeoConicPart.CONIC_PART_ARC       
     */
    public AlgoConicPartConicParameters(Construction cons, String label,
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
    @Override
	protected void setInputOutput() {
        input = new GeoElement[3];
        input[0] = conic;      
        input[1] = startParam.toGeoElement();
        input[2] = endParam.toGeoElement();

        super.setOutputLength(1);
        super.setOutput(0, conicPart);

        setDependencies();
    }

	// TODO Consider locusequability

    
}
