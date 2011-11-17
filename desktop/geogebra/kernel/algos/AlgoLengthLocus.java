/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.kernel.algos;

import geogebra.kernel.Construction;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoLocus;
import geogebra.kernel.GeoNumeric;


/**
 * Length of a GeoLocus object.
 */
public class AlgoLengthLocus extends AlgoElement {

	private GeoLocus locus; //input
    private GeoNumeric length; //output	

    AlgoLengthLocus(Construction cons, String label, GeoLocus locus) {
        super(cons);
        this.locus = locus;
               
        length = new GeoNumeric(cons);

        setInputOutput();
        compute();
        length.setLabel(label);
    }

    @Override
	public String getClassName() {
        return "AlgoLengthLocus";
    }

    @Override
	protected void setInputOutput(){
        input = new GeoElement[1];
        input[0] = locus;

        super.setOutputLength(1);
        super.setOutput(0, length);
        setDependencies(); // done by AlgoElement
    }

    GeoNumeric getLength() {
        return length;
    }

    @Override
	protected final void compute() {
    	if (locus.isDefined())
    		length.setValue(locus.getPointLength());
    	else 
    		length.setUndefined();
    }
    
}
