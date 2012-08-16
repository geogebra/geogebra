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

package geogebra.common.kernel.algos;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.arithmetic.BooleanValue;
import geogebra.common.kernel.arithmetic.ExpressionNode;
import geogebra.common.kernel.arithmetic.Function;
import geogebra.common.kernel.arithmetic.FunctionVariable;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoFunction;
import geogebra.common.kernel.geos.GeoFunctionConditional;
import geogebra.common.kernel.locusequ.EquationElement;
import geogebra.common.kernel.locusequ.EquationScope;

/**
 * algorithm for ChiSquared[a,x]
 * @author  Michael
 */
public class AlgoChiSquaredDF extends AlgoElement implements AlgoDistributionDF {

	private NumberValue k;  // input
	private BooleanValue cumulative; // optional input
	private GeoFunctionConditional ret;     // output           

	private GeoFunction ifFun, elseFun, condFun;           
        
    @SuppressWarnings("javadoc")
	public AlgoChiSquaredDF(Construction cons, String label, NumberValue mean, BooleanValue cumulative) {       
  	  	this(cons, mean, cumulative);
        ret.setLabel(label);
      }   
    
    @SuppressWarnings("javadoc")
	public AlgoChiSquaredDF(Construction cons, NumberValue a, BooleanValue cumulative) {       
  	  super(cons); 
        this.k = a;
        this.cumulative = cumulative;
        
        ret = new GeoFunctionConditional(cons); 

        // make function x<0
		FunctionVariable fv = new FunctionVariable(kernel);	
		ExpressionNode en = new ExpressionNode(kernel,fv);
		Function tempFun = new Function(en.lessThan(0),fv);
		condFun = new GeoFunction(cons, tempFun);
		ret.setConditionalFunction(condFun);
		
        // make function x=0
		fv = new FunctionVariable(kernel);	
		en = new ExpressionNode(kernel,0);
		tempFun = new Function(en,fv);
		ifFun = new GeoFunction(cons, tempFun);
		ret.setIfFunction(ifFun);
		
		

        
        setInputOutput(); // for AlgoElement
        
        // compute angle
        compute();     
      }   
    
    @Override
	public Algos getClassName() {
        return Algos.AlgoChiSquaredDF;
    }
    
    // for AlgoElement
    @Override
	protected void setInputOutput() {
    	
    	// dummy function for the "x" argument, eg
    	// Normal[0,1,x]
    	// Normal[0,1,x,true]
		FunctionVariable fv = new FunctionVariable(kernel);	
		ExpressionNode en = new ExpressionNode(kernel,fv);
		Function tempFun = new Function(en,fv);
		GeoFunction dummyFun = new GeoFunction(cons, tempFun);
    	
        input =  new GeoElement[cumulative == null ? 2 : 3];
        input[0] = k.toGeoElement();
        input[1] = dummyFun;
        if (cumulative != null) {
        	input[2] = (GeoElement) cumulative;
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
		ExpressionNode kEn = new ExpressionNode(kernel, k);
		ExpressionNode halfk = kEn.divide(2);
		ExpressionNode en;
		ExpressionNode div = halfk.gamma();
		

		if (cumulative != null && cumulative.getBoolean()) {


			en = new ExpressionNode(kernel, fv);
			
			en = en.divide(2).gammaIncompleteReverseArgs(halfk).divide(div);

			// old hack:
			//command = "If[x<0,0,gamma(("+k+")/2,x/2)/gamma(("+k+")/2)]";

		} else {

			en = new ExpressionNode(kernel, fv);
			
			en = en.power(halfk.subtract(1)).multiply(en.reverseSign().divide(2).exp());
			
			div = div.multiply(halfk.powerR(2));
			
			en = en.divide(div);
			
			

			// old hack:
			//command = "If[x<0,0,(x^(("+k+")/2-1)exp(-x/2))/(2^(("+k+")/2)gamma(("+k+")/2))]";
		}
		
		Function tempFun = new Function(en, fv);
		tempFun.initFunction();
		
		elseFun = new GeoFunction(cons, tempFun);
		
		ret.setElseFunction(elseFun);
		


    }

	

	// TODO Consider locusequability

}
