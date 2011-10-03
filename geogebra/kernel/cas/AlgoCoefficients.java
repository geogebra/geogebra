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
public class AlgoCoefficients extends AlgoElement {

	private static final long serialVersionUID = 1L;
	private GeoFunction f; // input
    private GeoList g; // output       
   
    public AlgoCoefficients(Construction cons, String label, GeoFunction f) {
    	this(cons,f);
        g.setLabel(label);
    }
    
    public AlgoCoefficients(Construction cons, GeoFunction f) {
    	super(cons);
        this.f = f;            	
    	
        g = new GeoList(cons);                
        setInputOutput(); // for AlgoElement        
        compute();
	}

	public String getClassName() {
        return "AlgoCoefficients";
    }
    
    // for AlgoElement
    protected void setInputOutput() {
        input = new GeoElement[1];
        input[0] = f;

        setOutputLength(1);
        setOutput(0,g);
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
                
        // get function and function variable string using temp variable prefixes,
		// e.g. f(x) = a x^2 returns {"ggbtmpvara ggbtmpvarx^2", "ggbtmpvarx"}
		String [] funVarStr = f.getTempVarCASString(false);

		StringBuilder sb = new StringBuilder();
        sb.append("Coefficients(");
        sb.append(funVarStr[0]); // function expression
        sb.append(",");
        sb.append(funVarStr[1]); // function variable
        sb.append(")");
		String functionOut;
		try {
			functionOut = kernel.evaluateCachedGeoGebraCAS(sb.toString());
			g.set(kernel.getAlgebraProcessor().evaluateToList(functionOut));	
			g.setDefined(true);	
		} catch (Throwable e) {
			g.setUndefined();
		}
		
    }
    
    final public String toString() {
    	return getCommandDescription();
    }

}
