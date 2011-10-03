/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.kernel;

import geogebra.kernel.arithmetic.ExpressionNode;
import geogebra.kernel.arithmetic.Function;
import geogebra.kernel.arithmetic.NumberValue;
import geogebra.kernel.parser.Parser;

/**
 * Try to expand the given function to a polynomial.
 * 
 * @author Markus Hohenwarter
 */
public class AlgoPolynomialFromFunction extends AlgoElement {

	private static final long serialVersionUID = 1L;
	private GeoFunction f; // input
    private GeoFunction g; // output         
    private Parser parser;
   
    public AlgoPolynomialFromFunction(Construction cons, String label, GeoFunction f) {
    	super(cons);
        this.f = f;            	
    	
        parser = new Parser(cons.getKernel(), cons);
        
        g = new GeoFunction(cons);                
        setInputOutput(); // for AlgoElement        
        compute();
        g.setLabel(label);
    }
    
    public String getClassName() {
        return "AlgoPolynomialFromFunction";
    }
    
    // for AlgoElement
    protected void setInputOutput() {
        input = new GeoElement[1];
        input[0] = f;

        output = new GeoElement[1];
        output[0] = g;
        setDependencies(); // done by AlgoElement
    }

    public GeoFunction getPolynomial() {
        return g;
    }

//  ON CHANGE: similar code is in AlgoTaylorSeries
    protected final void compute() {       
        if (!f.isDefined()) {
        	g.setUndefined();
        	return;
        }    
                
        // get numeric string for function
        int oldPrintForm = kernel.getCASPrintForm();
        kernel.setCASPrintForm(ExpressionNode.STRING_TYPE_MPREDUCE);
        String function = f.getCASString(false);    		
        String var = f.getVarString();
        kernel.setCASPrintForm(oldPrintForm);
        
        // expand expression and get polynomial coefficients
        String [] strCoeffs = kernel.getPolynomialCoeffs(function, var);
        if (strCoeffs == null) {
        	 g.setDefined(false);
        	 return;
        }
        
        //  build polynomial 
        
        // Michael Borcherds 2008-23-01 BEGIN
        // moved shared code into AlgoPolynomialFromCoordinates.buildPolyFunctionExpression()
        
        int n=strCoeffs.length;
        double coeffs[] = new double[n];
  	    for (int k = strCoeffs.length-1; k >= 0 ; k--) {
  	        coeffs[k] = evaluateToDouble(strCoeffs[k]);  	 	 
			 if (Double.isNaN(coeffs[k]) || Double.isInfinite(coeffs[k])) {
				 g.setUndefined();
				 return;
			 }
		 }
        
   		Function polyFun = AlgoPolynomialFromCoordinates.
   			buildPolyFunctionExpression(kernel,coeffs);

   		if (polyFun==null) {
   		    g.setUndefined();
       	    return;			   			   			
   		}
        
		g.setFunction(polyFun);			
		g.setDefined(true);		
    }
    
    private double evaluateToDouble(String str) {
		try {
			ExpressionNode en = parser.parseExpression(str);			
			NumberValue nv = (NumberValue) en.evaluate();
			return nv.getDouble();
		} catch (Exception e) {
			return Double.NaN;
		} catch (Error e) {
			return Double.NaN;
		}
	}


    final public String toString() {
    	return getCommandDescription();
    }

}
