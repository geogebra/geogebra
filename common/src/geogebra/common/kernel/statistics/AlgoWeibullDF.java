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
import geogebra.common.kernel.geos.GeoFunctionConditional;

/**
 * algorithm for Weibull[0,1,x]
 * @author  Michael
 */
public class AlgoWeibullDF extends AlgoElement implements AlgoDistributionDF {

	private NumberValue k, l;  // input
	private BooleanValue cumulative; // optional input
	private GeoFunctionConditional ret;     // output           

	private GeoFunction ifFun, elseFun, condFun;           
        
    @SuppressWarnings("javadoc")
	public AlgoWeibullDF(Construction cons, String label, NumberValue mean, NumberValue sd, BooleanValue cumulative) {       
  	  	this(cons, mean, sd, cumulative);
        ret.setLabel(label);
      }   
    
    @SuppressWarnings("javadoc")
	public AlgoWeibullDF(Construction cons, NumberValue k, NumberValue l, BooleanValue cumulative) {       
  	  super(cons); 
        this.k = k;
        this.l = l;
        this.cumulative = cumulative;
        ret = new GeoFunctionConditional(cons); 

        // make function x<0
		FunctionVariable fv = new FunctionVariable(kernel);	
		ExpressionNode en = new ExpressionNode(kernel,fv);
		condFun = en.lessThan(0).buildFunction(fv);
		ret.setConditionalFunction(condFun);
		
        // make function x=0
		fv = new FunctionVariable(kernel);	
		en = new ExpressionNode(kernel,0);
		ifFun = en.buildFunction(fv);
		ret.setIfFunction(ifFun);

		setInputOutput(); // for AlgoElement
        
        // compute angle
        compute();     
      }   
    
    @Override
	public Commands getClassName() {
		return Commands.Weibull;
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
        input[0] = k.toGeoElement();
        input[1] = l.toGeoElement();
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
		FunctionVariable fv = new FunctionVariable(kernel);
		ExpressionNode fvEn = new ExpressionNode(kernel, fv);
		ExpressionNode kEn = new ExpressionNode(kernel, k);
		ExpressionNode en = fvEn.divide(l).power(k).reverseSign().exp();
		
		if (cumulative != null && cumulative.getBoolean()) {


			en = en.subtractR(1);


			// old hack:
			//command = "If[x<0,0,1-exp(-(x/("+l+"))^("+k+"))]";

		} else {

			ExpressionNode prod1 = kEn.divide(l);
			
			ExpressionNode prod2 = fvEn.divide(l).power(kEn.subtract(1));
			
			en = en.multiply(prod2).multiply(prod1);
			
			// old hack:
			//command = "If[x<0,0,("+k+")/("+l+")(x/("+l+"))^("+k+"-1)exp(-(x/("+l+"))^("+k+"))]";
		}
		
		elseFun = en.buildFunction(fv);
		
		ret.setElseFunction(elseFun);


    }

	// TODO Consider locusequability

}
