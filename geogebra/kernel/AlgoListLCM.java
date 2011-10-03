/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.kernel;

import java.math.BigInteger;

/**
 * LCM of a list.
 * adapted from AlgoListMax
 * @author Michael Borcherds
 * @version 01-08-2011
 */

public class AlgoListLCM extends AlgoElement {

	private static final long serialVersionUID = 1L;
	private static final BigInteger bigZero = BigInteger.valueOf(0);
	private GeoList geoList; //input
    private GeoNumeric num; //output	

    AlgoListLCM(Construction cons, String label, GeoList geoList) {
        super(cons);
        this.geoList = geoList;
               
        num = new GeoNumeric(cons);

        setInputOutput();
        compute();
        num.setLabel(label);
    }

    public String getClassName() {
        return "AlgoListLCM";
    }

    protected void setInputOutput(){
        input = new GeoElement[1];
        input[0] = geoList;

        setOutputLength(1);
        setOutput(0, num);
        setDependencies(); // done by AlgoElement
    }

    GeoNumeric getLCM() {
        return num;
    }
    
    protected final void compute() {
    	int size = geoList.size();
    	if (!geoList.isDefined() ||  size == 0) {
    		num.setUndefined();
    		return;
    	}
    	
    	if (!geoList.getGeoElementForPropertiesDialog().isGeoNumeric()) {
    		num.setUndefined();
    		return;   		
    	}
    	
    	BigInteger lcm = BigInteger.valueOf((long)((GeoNumeric)(geoList.get(0))).getDouble());
    	
    	for (int i = 1 ; i < geoList.size() ; i++) {
    		double nd = ((GeoNumeric)(geoList.get(i))).getDouble();
    		
    		if(!kernel.isInteger(nd)){
    			num.setUndefined();
    			return;
    		}    		
        	BigInteger n = BigInteger.valueOf((long)nd);
        	if(n.compareTo(bigZero)==0){
        		lcm = bigZero;
        	}else{
        		BigInteger product = n.multiply(lcm);
        		lcm =  product.divide(lcm.gcd(n));
        	}
    	}
    	
    	double resultD = Math.abs(lcm.doubleValue());
    	
    	// can't store integers greater than this in a double accurately
    	if(Math.abs(lcm.doubleValue())>1e15){
			num.setUndefined();
			return;
		}    		
    	num.setValue(resultD);
    	
    }
    
}
