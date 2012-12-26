/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.common.kernel.cas;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.algos.AlgoElement;
import geogebra.common.kernel.algos.Algos;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.commands.Commands;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.kernel.geos.GeoNumeric;


/**
 * Prime factors of a number. Adapted from AlgoPrimeFactors
 * @author Zbynek Konecny
 */

public class AlgoPrimeFactorization extends AlgoElement {

	private NumberValue num; //input
    private GeoList outputList; //output	    
    
    /**
     * Largest integer
     */
    public static double LARGEST_INTEGER=9007199254740992d;
    /**
     * Creates new factorization algo 
     * @param cons construction
     * @param num Number to factorize
     */
    public AlgoPrimeFactorization(Construction cons,  NumberValue num) {
        super(cons);        
        this.num = num;
               
        outputList = new GeoList(cons);

        setInputOutput();
        compute();
    }
    
    /**
     * Creates new factorization algo 
     * @param cons construction
     * @param label label for output
     * @param num Number to factorize
     */
    public AlgoPrimeFactorization(Construction cons, String label, NumberValue num) {
        this(cons,num);
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

        setOutputLength(1);
        setOutput(0,outputList);
        setDependencies(); // done by AlgoElement
    }

    /**
     * Returns the list of points (prime,exponent)
     * @return the list of points (prime,exponent)
     */
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
			int exp = 0;
			while (n % i == 0) {
				exp++;
				n /= i;
			}
			if(exp > 0){
				setListElement(count++, i,exp);
			}
		}
		if (n > 1) {
			setListElement(count++, n,1);
		}     
    }
    
    // copied from AlgoInterationList.java
    // TODO should it be centralised?
    private void setListElement(int index, double value, double exp) {
    	GeoList listElement;
    	if (index < outputList.getCacheSize()) {
    		// use existing list element
    		listElement = (GeoList) outputList.getCached(index);  
    		listElement.clear();
    	} else {
    		// create a new list element
    		listElement = new GeoList(cons);
    		listElement.setParentAlgorithm(this);
    		listElement.setConstructionDefaults();
    		listElement.setUseVisualDefaults(false);	    		
    	}
    	
    	outputList.add(listElement);
    	GeoNumeric prime = new GeoNumeric(cons);
    	prime.setValue(value);
    	GeoNumeric exponent = new GeoNumeric(cons);
    	exponent.setValue(exp);
    	listElement.add(prime);
    	listElement.add(exponent);
    }    

	// TODO Consider locusequability

}
