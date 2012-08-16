/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.common.kernel.algos;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.arithmetic.ExpressionNode;
import geogebra.common.kernel.arithmetic.ExpressionNodeConstants.StringType;
import geogebra.common.kernel.arithmetic.Function;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoFunction;
import geogebra.common.kernel.locusequ.EquationElement;
import geogebra.common.kernel.locusequ.EquationScope;
import geogebra.common.kernel.parser.Parser;

/**
 * Try to expand the given function to a polynomial.
 * 
 * @author Markus Hohenwarter
 */
public class AlgoPolynomialFromFunction extends AlgoElement {

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
    
    @Override
	public Algos getClassName() {
        return Algos.AlgoPolynomialFromFunction;
    }
    
    // for AlgoElement
    @Override
	protected void setInputOutput() {
        input = new GeoElement[1];
        input[0] = f;

        super.setOutputLength(1);
        super.setOutput(0, g);
        setDependencies(); // done by AlgoElement
    }

    public GeoFunction getPolynomial() {
        return g;
    }

//  ON CHANGE: similar code is in AlgoTaylorSeries
    @Override
	public final void compute() {       
        if (!f.isDefined()) {
        	g.setUndefined();
        	return;
        }    
                
        // get numeric string for function
        StringTemplate tpl = StringTemplate.get(StringType.MPREDUCE);
        String function = f.getCASString(tpl,false);    		
        String var = f.getVarString(tpl);
        
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
			NumberValue nv = en.evaluateNum();
			return nv.getDouble();
		} catch (Exception e) {
			return Double.NaN;
		} catch (Error e) {
			return Double.NaN;
		}
	}

    @Override
	final public String toString(StringTemplate tpl) {
    	return getCommandDescription(tpl);
    }

	// TODO Consider locusequability

}
