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
 * Returns whether an object is an integer
 * @author Michael Borcherds
 * @version 2008-03-06
 */

public class AlgoIsInteger extends AlgoElement {

	private static final long serialVersionUID = 1L;
	private GeoNumeric inputGeo; //input
    private GeoBoolean outputBoolean; //output	

    AlgoIsInteger(Construction cons, String label, GeoNumeric inputGeo) {
        super(cons);
        this.inputGeo = inputGeo;

               
        outputBoolean = new GeoBoolean(cons);

        setInputOutput();
        compute();
        outputBoolean.setLabel(label);
    }

    public String getClassName() {
        return "AlgoIsInteger";
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
        outputBoolean.setValue(kernel.isInteger(inputGeo.getDouble()));
    }
  
}
