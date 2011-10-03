/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.kernel;


public abstract class AlgoIntervalAbstract extends AlgoElement {

	private static final long serialVersionUID = 1L;
	protected GeoInterval interval; //input
    protected GeoNumeric result; //output	

    AlgoIntervalAbstract(Construction cons, String label, GeoInterval interval) {
        this(cons, interval);
        
        result.setLabel(label);
    }

    AlgoIntervalAbstract(Construction cons, GeoInterval interval) {
        super(cons);
        this.interval = interval;
               
        result = new GeoNumeric(cons);

        setInputOutput();
        compute();
    }

    protected void setInputOutput(){
        input = new GeoElement[1];
        input[0] = interval;

        output = new GeoElement[1];
        output[0] = result;
        setDependencies(); // done by AlgoElement
    }

    GeoNumeric getResult() {
        return result;
    }

    
}
