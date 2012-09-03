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
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.geos.GeoBoolean;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoVector;
import geogebra.common.kernel.kernelND.GeoPointND;


/**
 * Returns whether an object is defined
 * @author Michael Borcherds
 * @version 2008-03-06
 */

public class AlgoDefined extends AlgoElement {

	private GeoElement inputGeo; //input
    private GeoBoolean outputBoolean; //output	

    public AlgoDefined(Construction cons, String label, GeoElement inputGeo) {
        super(cons);
        this.inputGeo = inputGeo;

               
        outputBoolean = new GeoBoolean(cons);

        setInputOutput();
        compute();
        outputBoolean.setLabel(label);
    }

    @Override
	public Algos getClassName() {
        return Algos.AlgoDefined;
    }

    @Override
	protected void setInputOutput(){
        input = new GeoElement[1];
        input[0] = inputGeo;

        super.setOutputLength(1);
        super.setOutput(0, outputBoolean);
        setDependencies(); // done by AlgoElement
    }

    public GeoBoolean getResult() {
        return outputBoolean;
    }

    @Override
	public final void compute() {

    	if (inputGeo.isGeoPoint()) {
    		GeoPointND p = (GeoPointND)inputGeo;
    		outputBoolean.setValue(inputGeo.isDefined() && !p.isInfinite());
    		return;
    	}
    	else if (inputGeo.isGeoVector()) {
    		GeoVector v = (GeoVector)inputGeo;
    		outputBoolean.setValue(inputGeo.isDefined() && !v.isInfinite());
    		return;
    	}
    	else if (inputGeo.isGeoFunction()) {
    		if (inputGeo.toValueString(StringTemplate.defaultTemplate).equals("?")){
    			outputBoolean.setValue(false);
        		return;
    		}
    	}

    	outputBoolean.setValue(inputGeo.isDefined());
    }

	// TODO Consider locusequability
  
}
