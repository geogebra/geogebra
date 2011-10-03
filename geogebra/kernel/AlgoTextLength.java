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
 * Length of a GeoText object.
 * @author Michael
 */

public class AlgoTextLength extends AlgoElement {

	private static final long serialVersionUID = 1L;
	private GeoText text; //input
    private GeoNumeric length; //output	

    AlgoTextLength(Construction cons, String label, GeoText text) {
        super(cons);
        this.text = text;
               
        length = new GeoNumeric(cons);

        setInputOutput();
        compute();
        length.setLabel(label);
    }

    public String getClassName() {
        return "AlgoTextLength";
    }

    protected void setInputOutput(){
        input = new GeoElement[1];
        input[0] = text;

        output = new GeoElement[1];
        output[0] = length;
        setDependencies(); // done by AlgoElement
    }

    GeoNumeric getLength() {
        return length;
    }

    protected final void compute() {
    	if (text.isDefined())
    		length.setValue(text.getTextString().length());
    	else 
    		length.setUndefined();
    }
    
}
