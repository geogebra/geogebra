/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.kernel;

import geogebra.kernel.kernelND.GeoPointND;


/**
 * Returns whether an object is defined
 * @author Michael Borcherds
 * @version 2008-03-06
 */

public class AlgoDefined extends AlgoElement {

	private static final long serialVersionUID = 1L;
	private GeoElement inputGeo; //input
    private GeoBoolean outputBoolean; //output	

    AlgoDefined(Construction cons, String label, GeoElement inputGeo) {
        super(cons);
        this.inputGeo = inputGeo;

               
        outputBoolean = new GeoBoolean(cons);

        setInputOutput();
        compute();
        outputBoolean.setLabel(label);
    }

    public String getClassName() {
        return "AlgoDefined";
    }

    protected void setInputOutput(){
        input = new GeoElement[1];
        input[0] = inputGeo;

        output = new GeoElement[1];
        output[0] = outputBoolean;
        setDependencies(); // done by AlgoElement
    }

    GeoBoolean getResult() {
        return outputBoolean;
    }

    protected final void compute() {

    	if (inputGeo.isGeoPoint()) {
    		GeoPointND p = (GeoPointND)inputGeo;
    		outputBoolean.setValue(inputGeo.isDefined() && !p.isInfinite());
    		return;
    	}
    	else if (inputGeo.isGeoPoint()) {
    		GeoVector v = (GeoVector)inputGeo;
    		outputBoolean.setValue(inputGeo.isDefined() && !v.isInfinite());
    		return;
    	}
    	else if (inputGeo.isGeoFunction()) {
    		if (inputGeo.toValueString().equals("?")){
    			outputBoolean.setValue(false);
        		return;
    		}
    	}

    	outputBoolean.setValue(inputGeo.isDefined());
    }
  
}
