/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.common.kernel.advanced;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.algos.AlgoElement;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.commands.Commands;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.kernel.geos.GeoNumeric;


/**
 * Prime factors of a number. Adapted from AlgoMode
 * @author Michael Borcherds
 */

public class AlgoPrimeFactors extends AlgoElement {

	private NumberValue num; //input
    private GeoList outputList; //output	
    
    private static double LARGEST_INTEGER=9007199254740992d;

    public AlgoPrimeFactors(Construction cons, String label, NumberValue num) {
        super(cons);
        this.num = num;
               
        outputList = new GeoList(cons);

        setInputOutput();
        compute();
        outputList.setLabel(label);
    }

    @Override
	public Commands getClassName() {
        return Commands.PrimeFactors;
    }

    @Override
	protected void setInputOutput(){
        input = new GeoElement[1];
        input[0] = num.toGeoElement();

        super.setOutputLength(1);
        super.setOutput(0, outputList);
        setDependencies(); // done by AlgoElement
    }

    public GeoList getResult() {
        return outputList;
    }

    @Override
	public final void compute() {
    	
    	double n = Math.round(num.getDouble());
    	
    	if (n == 1) {
    		outputList.clear();
    		outputList.setDefined(true);
    		return;
    	}
    	
    	if (n < 2 || n > LARGEST_INTEGER) {
    		outputList.setUndefined();
    		return;
    	}
         	      
       outputList.setDefined(true);
       outputList.clear();
       
       int count = 0;

		for (int i = 2; i <= n / i; i++) {
			while (n % i == 0) {
				setListElement(count++, i);
				n /= i;
			}
		}
		if (n > 1) {
			setListElement(count++, n);
		}     
    }
    
    // copied from AlgoInterationList.java
    // TODO should it be centralised?
    private void setListElement(int index, double value) {
    	GeoNumeric listElement;
    	if (index < outputList.getCacheSize()) {
    		// use existing list element
    		listElement = (GeoNumeric) outputList.getCached(index);    	
    	} else {
    		// create a new list element
    		listElement = new GeoNumeric(cons);
    		listElement.setParentAlgorithm(this);
    		listElement.setConstructionDefaults();
    		listElement.setUseVisualDefaults(false);	    		
    	}
    	
    	outputList.add(listElement);
    	listElement.setValue(value);
    }    

	// TODO Consider locusequability

}
