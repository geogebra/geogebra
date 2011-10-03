/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.kernel.cas;

import geogebra.kernel.CasEvaluableFunction;
import geogebra.kernel.Construction;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoNumeric;
import geogebra.kernel.arithmetic.NumberValue;

/**
 * Derivative of a function
 * 
 * @author Markus Hohenwarter
 */
public class AlgoDerivative extends AlgoCasBase {

	private GeoNumeric var;
    private NumberValue order;

    public AlgoDerivative(
        Construction cons,
        String label,
        CasEvaluableFunction f,
        GeoNumeric var, 
        NumberValue order) 
    {
        this(cons, f, var, order);
        g.toGeoElement().setLabel(label);
    }
    
    public AlgoDerivative(Construction cons,  CasEvaluableFunction f) {
        this(cons, f, null, null);      
    }
    
    public AlgoDerivative(Construction cons,  CasEvaluableFunction f, GeoNumeric var, NumberValue order) {
            super(cons, f);
            this.var = var;
            this.order = order;
 
            setInputOutput(); // for AlgoElement    
            compute();            
     }
    
    

    public String getClassName() {
        return "AlgoCasDerivative";
    }

    // for AlgoElement
    protected void setInputOutput() {
        int length = 1;
        if (order != null) length++;
        if (var != null) length++;
        
        input = new GeoElement[length];
        length = 0;
        input[0] = f.toGeoElement();
        if (var != null)
            input[++length] = var;
        if (order != null)
            input[++length] = order.toGeoElement();

        setOutputLength(1);
        setOutput(0, g.toGeoElement());
        setDependencies(); // done by AlgoElement
    }  
    
	@Override
	protected void applyCasCommand() {
		
		// var.getLabel() can return a number in wrong alphabet (need ASCII)
		boolean internationalizeDigits = kernel.internationalizeDigits;
		kernel.internationalizeDigits = false;
		
		// get variable string with tmp prefix, 
		// e.g. "x" becomes "ggbtmpvarx" here
		boolean isUseTempVariablePrefix = kernel.isUseTempVariablePrefix();
		kernel.setUseTempVariablePrefix(true);
		String varStr = var != null ? var.getLabel() : f.getVarString();
		kernel.setUseTempVariablePrefix(isUseTempVariablePrefix);

		 kernel.internationalizeDigits = internationalizeDigits;
		
		 sbAE.setLength(0);
		 sbAE.append("Derivative(%");
		 sbAE.append(",");
		 sbAE.append(varStr);
		 sbAE.append(",");
		 sbAE.append(order == null ? 1 : (int) Math.round(order.getDouble()));
		 sbAE.append(")");
		 
		 // find symbolic derivative of f
		 g.setUsingCasCommand(sbAE.toString(), f, true);	

	}
  
    final public String toString() {
    	StringBuilder sb = new StringBuilder();
        
        if (var != null) {
        	// Derivative[ a x^2, x ]
        	sb.append(super.toString());
        } else {
        	// 2. Derivative of a x^2
	        if (order != null) {
	        	String orderStr = order.toGeoElement().getLabel();
	        	char firstCh = orderStr.charAt(0);
	        	if (firstCh >= '0' && firstCh <= '9') {
	        		// numeric, convert 3 -> 3rd (in current locale)
	        		orderStr = app.getOrdinalNumber((int)order.getDouble());
	        	} else {
	        		// symbolic, convert n -> nth (in current locale)
	        		orderStr = app.getPlain("Ath", orderStr); 
	        	}

	        	sb.append(app.getPlain("ADerivativeOfB", orderStr, f.toGeoElement().getLabel()));
	        } else {
	        	sb.append(app.getPlain("DerivativeOfA",f.toGeoElement().getLabel()));
	        }
        }
        
        
        if (!f.toGeoElement().isIndependent()) { // show the symbolic representation too
            sb.append(": ");
            sb.append(g.toGeoElement().getLabel());
            sb.append('(');
            sb.append(g.getVarString());
            sb.append(") = ");
    		sb.append(g.toSymbolicString());            
        } 
        
        return sb.toString();
    }

}
