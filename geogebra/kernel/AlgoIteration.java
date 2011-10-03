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
 * Iteration[ f(x), x0, n ] 
 * @author Markus Hohenwarter
 * @version 15-07-2007
 */

public class AlgoIteration extends AlgoElement {

	private static final long serialVersionUID = 1L;
	private GeoFunction f; //input
	private NumberValue startValue, n;
	private GeoElement startValueGeo, nGeo;
    private GeoNumeric result; //output	

    AlgoIteration(Construction cons, String label, 
    		GeoFunction f, NumberValue startValue, NumberValue n) {
        super(cons);
        this.f = f;
        this.startValue = startValue;
        startValueGeo = startValue.toGeoElement();
        this.n = n;
        nGeo = n.toGeoElement();
               
        result = new GeoNumeric(cons);

        setInputOutput();
        compute();
        result.setLabel(label);
    }

    public String getClassName() {
        return "AlgoIteration";
    }

    protected void setInputOutput(){
        input = new GeoElement[3];
        input[0] = f;
        input[1] = startValueGeo;
        input[2] = nGeo;

        output = new GeoElement[1];
        output[0] = result;
        setDependencies(); // done by AlgoElement
    }

    GeoNumeric getResult() {
        return result;
    }

    protected final void compute() {
    	if (!f.isDefined() ||  !startValueGeo.isDefined() || !nGeo.isDefined()) {
    		result.setUndefined();
    		return;
    	}
    	
    	int iterations = (int) Math.round(n.getDouble());
    	if (iterations < 0) {
    		result.setUndefined();
    		return;
    	}
    	
    	// perform iteration f(f(f(...(startValue))))
    	double val = startValue.getDouble();
    	for (int i=0; i < iterations; i++) {
    		val = f.evaluate(val);	    		
    	}    	
    	result.setValue(val);
    }
    
}
