/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.kernel;


/**
 * Length of a GeoLocus object.
 */

public class AlgoLengthLocus extends AlgoElement {

	private static final long serialVersionUID = 1L;
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

    public String getClassName() {
        return "AlgoLengthLocus";
    }

    protected void setInputOutput(){
        input = new GeoElement[1];
        input[0] = locus;

        output = new GeoElement[1];
        output[0] = length;
        setDependencies(); // done by AlgoElement
    }

    GeoNumeric getLength() {
        return length;
    }

    protected final void compute() {
    	if (locus.isDefined())
    		length.setValue(locus.getPointLength());
    	else 
    		length.setUndefined();
    }
    
}
