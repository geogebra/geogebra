/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.kernel.cas;

import geogebra.kernel.AlgoElement;
import geogebra.kernel.Construction;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoFunction;
import geogebra.kernel.GeoNumeric;
/**
 * Try to expand the given function 
 * 
 * @author Michael Borcherds
 */
public class AlgoDegree extends AlgoElement {

	private static final long serialVersionUID = 1L;
	private GeoFunction f; // input
    private GeoNumeric num; // output        
    
    private StringBuilder sb = new StringBuilder();
   
    public AlgoDegree(Construction cons, String label, GeoFunction f) {
    	super(cons);
        this.f = f;            	
    	
        num = new GeoNumeric(cons);                
        setInputOutput(); // for AlgoElement        
        compute();
        num.setLabel(label);
    }
    
    public String getClassName() {
        return "AlgoDegree";
    }
    
    // for AlgoElement
    protected void setInputOutput() {
        input = new GeoElement[1];
        input[0] = f;

        output = new GeoElement[1];
        output[0] = num;
        setDependencies(); // done by AlgoElement
    }

    public GeoNumeric getResult() {
        return num;
    }

    protected final void compute() {       
        if (!f.isDefined()) {
        	num.setUndefined();
        	return;
        }    
               
     	// get function and function variable string using temp variable prefixes,
		// e.g. f(x) = a x^2 returns {"ggbtmpvara ggbtmpvarx^2", "ggbtmpvarx"}
		String [] funVarStr = f.getTempVarCASString(false);
	    
	    sb.setLength(0);
        sb.append("Degree(");
        sb.append(funVarStr[0]); // function expression
        sb.append(",");
        sb.append(funVarStr[1]); // function variable
        sb.append(")");
		String functionOut;
		try {
			functionOut = kernel.evaluateCachedGeoGebraCAS(sb.toString());
			num.setValue(Double.parseDouble(functionOut));	
		} catch (Throwable e) {
			System.err.println("AlgoCasDegree: " + e.getMessage());
			num.setUndefined();
		}
		
    }
    
    final public String toString() {
    	return getCommandDescription();
    }

}
