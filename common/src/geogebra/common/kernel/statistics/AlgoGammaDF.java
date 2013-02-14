/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

/*
 * Area of polygon P[0], ..., P[n]
 *
 */

package geogebra.common.kernel.statistics;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.algos.AlgoElement;
import geogebra.common.kernel.arithmetic.BooleanValue;
import geogebra.common.kernel.arithmetic.ExpressionNode;
import geogebra.common.kernel.arithmetic.FunctionVariable;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.commands.Commands;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoFunction;

/**
 * algorithm for Normal[0,1,x]
 * @author  Michael
 */
public class AlgoGammaDF extends AlgoElement implements AlgoDistributionDF {

	private NumberValue mean, sd;  // input
	private BooleanValue cumulative; // optional input
	private GeoFunction ret;     // output               
        
    @SuppressWarnings("javadoc")
	public AlgoGammaDF(Construction cons, String label, NumberValue mean, NumberValue sd, BooleanValue cumulative) {       
  	  	this(cons, mean, sd, cumulative);
        ret.setLabel(label);
      }   
    
    @SuppressWarnings("javadoc")
	public AlgoGammaDF(Construction cons, NumberValue a, NumberValue b, BooleanValue cumulative) {       
  	  super(cons); 
        this.mean = a;
        this.sd = b;
        this.cumulative = cumulative;
        
        ret = DistributionFunctionFactory.zeroWhenNegative(cons);
		
		

        
        setInputOutput(); // for AlgoElement
        
        // compute angle
        compute();     
      }   
    
    @Override
	public Commands getClassName() {
		return Commands.Gamma;
	}
    
    // for AlgoElement
    @Override
	protected void setInputOutput() {
    	
    	// dummy function for the "x" argument, eg
    	// Normal[0,1,x]
    	// Normal[0,1,x,true]
		FunctionVariable fv = new FunctionVariable(kernel);	
		GeoFunction dummyFun = fv.wrap().buildFunction(fv);
    	
        input =  new GeoElement[cumulative == null ? 3 : 4];
        input[0] = mean.toGeoElement();
        input[1] = sd.toGeoElement();
        input[2] = dummyFun;
        if (cumulative != null) {
        	input[3] = (GeoElement) cumulative;
        }
        
        super.setOutputLength(1);
        super.setOutput(0, ret);
        setDependencies(); // done by AlgoElement
    }    
    
    /**
     * @return Normal PDF or CDF function
     */
    public GeoFunction getResult() { return ret; }        

    @Override
	public void compute() {
		NumberValue k = mean;
		NumberValue t = sd;

		FunctionVariable fv = ret.getFunctionVariables()[0];
		ExpressionNode fvEn = new ExpressionNode(kernel, fv);
		ExpressionNode kEn = new ExpressionNode(kernel, k);
		ExpressionNode en = fvEn.divide(t);
		ExpressionNode div = kEn.gamma();

		if (cumulative != null && cumulative.getBoolean()) {



			en = en.gammaIncompleteReverseArgs(k).divide(div);

			// old hack:
			//command = "If[x<0,0,gamma("+k+",x/("+t+"))/gamma("+k+")]";

		} else {

			ExpressionNode tEn = new ExpressionNode(kernel, t);
			
			div = div.multiply(tEn.power(k));
			
			en = en.reverseSign().exp().multiply(fvEn.power(kEn.subtract(1))).divide(div);


			// old hack:
			//command = "If[x<0,0,x^("+k+"-1) exp(-x/("+t+"))/(gamma("+k+")("+t+")^("+k+"))]";
		}
		
		ret.getFunctionExpression().setRight(en);
		


    }

	// TODO Consider locusequability

	
}
