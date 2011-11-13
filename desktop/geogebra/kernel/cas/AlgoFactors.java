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
import geogebra.kernel.GeoList;
/**
 * Try to expand the given function 
 * 
 * @author Michael Borcherds
 */
public class AlgoFactors extends AlgoElement {

	private static final long serialVersionUID = 1L;
	private GeoFunction f; // input
    private GeoList g; // output        
    
    private StringBuilder sb = new StringBuilder();
   
    public AlgoFactors(Construction cons, String label, GeoFunction f) {
    	super(cons);
        this.f = f;            	
    	
        g = new GeoList(cons);                
        setInputOutput(); // for AlgoElement        
        compute();
        g.setLabel(label);
    }
    
    public String getClassName() {
        return "AlgoFactors";
    }
    
    // for AlgoElement
    protected void setInputOutput() {
        input = new GeoElement[1];
        input[0] = f;

        output = new GeoElement[1];
        output[0] = g;
        setDependencies(); // done by AlgoElement
    }

    public GeoList getResult() {
        return g;
    }

    protected final void compute() {       
        if (!f.isDefined()) {
        	g.setUndefined();
        	return;
        }    

	    try {
	    	// get function and function variable string using temp variable prefixes,
			// e.g. f(x) = a x^2 returns {"ggbtmpvara ggbtmpvarx^2", "ggbtmpvarx"}
			String [] funVarStr = f.getTempVarCASString(false);
		
		    sb.setLength(0);
		    sb.append("Numeric(Factors(");
		    sb.append(funVarStr[0]); // function expression
		    sb.append("))");
	        // cached evaluation of MPReduce as we are only using variable values
			String listOut = kernel.evaluateCachedGeoGebraCAS(sb.toString());	
				   
			if (listOut == null || listOut.length()==0) {
				g.setUndefined(); 
			}
			else {
				// read result back into list
				g.set(kernel.getAlgebraProcessor().evaluateToList(listOut));
			}
	    } catch (Throwable th) {
	    	g.setUndefined();
	    }
    }
    
    final public String toString() {
    	return getCommandDescription();
    }

}
