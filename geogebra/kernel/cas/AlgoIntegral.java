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
import geogebra.kernel.GeoFunction;
import geogebra.kernel.GeoNumeric;



/**
 * Integral of a function
 * 
 * @author Markus Hohenwarter
 */
public class AlgoIntegral extends AlgoCasBase {
 
	private GeoNumeric var;
	
    public AlgoIntegral(
            Construction cons,
            String label,
            CasEvaluableFunction f,
            GeoNumeric var) 
        {
            this(cons, f, var);
            g.toGeoElement().setLabel(label);
        }
        
    public AlgoIntegral(Construction cons,  CasEvaluableFunction f, GeoNumeric var) {
            super(cons, f);
            this.var = var;
 
            setInputOutput(); // for AlgoElement    
            compute();            
     }
    
    public String getClassName() {
        return "AlgoCasIntegral";
    }   
    
    protected void setInputOutput() {
        int length = 1;
        if (var != null) length++;
        
        input = new GeoElement[length];
        length = 0;
        input[0] = f.toGeoElement();
        if (var != null)
            input[++length] = var;

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
		 sbAE.append("Integral(%");
		 sbAE.append(",");
		 sbAE.append(varStr);
		 sbAE.append(")");
		 
		 // find symbolic derivative of f
		 g.setUsingCasCommand(sbAE.toString(), f, true);	
	}

    final public String toString() {
        StringBuilder sb = new StringBuilder();
        
        if (var != null) {
        	// Integral[ a x^2, x ]
        	sb.append(super.toString());
        } else {
	        // Michael Borcherds 2008-03-30
	        // simplified to allow better Chinese translation
	        sb.append(app.getPlain("IntegralOfA",f.toGeoElement().getLabel()));
        }
        
     
        if (!f.toGeoElement().isIndependent()) { // show the symbolic representation too
            sb.append(": ");
            sb.append(g.toGeoElement().getLabel());
            if (g.toGeoElement() instanceof GeoFunction)
            {
            	sb.append('(');
            	sb.append(((GeoFunction) g.toGeoElement()).getVarString());
            	sb.append(')');
            }
            sb.append(" = ");
            sb.append(g.toSymbolicString());
        }

        return sb.toString();
    }

	

}
