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
 * algorithm for Triangular[a, b, mode,x, boolean]
 * @author  Michael
 */
public class AlgoTriangularDF extends AlgoElement {

	private NumberValue a, b, mode;  // input
	private BooleanValue cumulative; // optional input
	private GeoFunctionConditional ret;     // output           
        
    @SuppressWarnings("javadoc")
	public AlgoTriangularDF(Construction cons, String label, NumberValue a, NumberValue b, NumberValue mode, BooleanValue cumulative) {       
  	  	this(cons, a, b, mode, cumulative);
        ret.setLabel(label);
      }   
    
    @SuppressWarnings("javadoc")
    public AlgoTriangularDF(Construction cons, NumberValue a, NumberValue b, NumberValue mode, BooleanValue cumulative) {       
  	  super(cons); 
        this.a = a;
        this.b = b;
        this.mode = mode;
        this.cumulative = cumulative;
        ret = new GeoFunctionConditional(cons); 


		setInputOutput(); // for AlgoElement
        
        // compute angle
        compute();     
      }   
    
    @Override
	public Algos getClassName() {
        return Algos.AlgoTriangularDF;
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
    	
        input =  new GeoElement[cumulative == null ? 4 : 5];
        input[0] = a.toGeoElement();
        input[1] = b.toGeoElement();
        input[2] = mode.toGeoElement();
        input[3] = dummyFun;
        if (cumulative != null) {
        	input[4] = (GeoElement) cumulative;
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
    	
    	if (!a.isDefined() || !b.isDefined() || !mode.isDefined()) {
    		ret.setUndefined();
    		return;
    	}
    	
    	if (a.getDouble() >= b.getDouble() || mode.getDouble() > b.getDouble() || mode.getDouble() < a.getDouble()) {
    		ret.setUndefined();
    		return;    		
    	}
    	
		ExpressionNode bEn = new ExpressionNode(kernel, b);
		ExpressionNode modeEn = new ExpressionNode(kernel, mode);
		

        // make function x<a
		FunctionVariable fv = new FunctionVariable(kernel);	
		ExpressionNode en = new ExpressionNode(kernel,fv);
		Function tempFun = new Function(en.lessThan(a),fv);
		GeoFunction condFunxLessThana = new GeoFunction(cons, tempFun);
		//ret.setConditionalFunction(condFun);
		
        // make function x<b
		fv = new FunctionVariable(kernel);	
		en = new ExpressionNode(kernel,fv);
		tempFun = new Function(en.lessThan(b), fv);
		GeoFunction condFunxLessThanb = new GeoFunction(cons, tempFun);
		//ret.setConditionalFunction(condFun);
		
        // make function x<mode
		fv = new FunctionVariable(kernel);	
		en = new ExpressionNode(kernel,fv);
		tempFun = new Function(en.lessThan(mode),fv);
		GeoFunction condFunxLessThanMode = new GeoFunction(cons, tempFun);
		//ret.setConditionalFunction(condFun);
		
        // make function x=0
		fv = new FunctionVariable(kernel);	
		en = new ExpressionNode(kernel, 0);
		tempFun = new Function(en,fv);
		GeoFunction ifFun0 = new GeoFunction(cons, tempFun);
		//ret.setIfFunction(ifFun);

		GeoFunctionConditional inner = new GeoFunctionConditional(cons); 
		inner.setConditionalFunction(condFunxLessThanb);
		GeoFunction ifFun1;
		
		if (cumulative != null && cumulative.getBoolean()) {


			fv = new FunctionVariable(kernel);	
			en = new ExpressionNode(kernel,fv);			
			en = en.subtract(a).square().divide(bEn.subtract(a).multiply(modeEn.subtract(a)));
			tempFun = new Function(en,fv);
			ifFun1 = new GeoFunction(cons, tempFun);

			fv = new FunctionVariable(kernel);	
			en = new ExpressionNode(kernel,fv);			
			en = en.subtract(b).square().divide(bEn.subtract(a).multiply(modeEn.subtract(b))).plus(1);
			tempFun = new Function(en,fv);
			GeoFunction ifFun2 = new GeoFunction(cons, tempFun);

	        // make function x=1
			fv = new FunctionVariable(kernel);	
			en = new ExpressionNode(kernel, 1);
			tempFun = new Function(en,fv);
			GeoFunction elseFun = new GeoFunction(cons, tempFun);
			// old hack:
			//processAlgebraCommand( "If[x < "+a+", 0, If[x < "+c+", (x - ("+a+"))^2 / ("+b+" - ("+a+")) / ("+c+" - ("+a+")), 
			//If[x < "+b+", 1 + (x - ("+b+"))^2 / ("+b+" - ("+a+")) / ("+c+" - ("+b+")), 1]]]", true);
			
			inner.setIfFunction(ifFun2);
			inner.setElseFunction(elseFun);


		} else {

			
			fv = new FunctionVariable(kernel);	
			en = new ExpressionNode(kernel,fv);			
			en = en.subtract(a).multiply(2).divide(bEn.subtract(a).multiply(modeEn.subtract(a)));
			tempFun = new Function(en,fv);
			ifFun1 = new GeoFunction(cons, tempFun);
			
			fv = new FunctionVariable(kernel);	
			en = new ExpressionNode(kernel,fv);			
			en = en.subtract(b).multiply(2).divide(bEn.subtract(a).multiply(modeEn.subtract(b)));
			tempFun = new Function(en,fv);
			GeoFunction ifFun2 = new GeoFunction(cons, tempFun);
			
			inner.setIfFunction(ifFun2);
			inner.setElseFunction(ifFun0); // x=0


			// old hack:
			//processAlgebraCommand( "If[x < "+a+", 0, If[x < "+c+", 2(x - ("+a+")) / ("+b+" - ("+a+")) / ("+c+" - ("+a+")), If[x < "+b+", 2(x - ("+b+")) / ("+b+" - ("+a+")) / ("+c+" - ("+b+")), 0]]]", true );
		}
		GeoFunctionConditional middle = new GeoFunctionConditional(cons); 
		middle.setConditionalFunction(condFunxLessThanMode);
		middle.setIfFunction(ifFun1);
		middle.setElseFunction(inner);
		
		ret.setDefined(true);
		ret.setConditionalFunction(condFunxLessThana);
		ret.setIfFunction(ifFun0);
		ret.setElseFunction(middle);
		

    }

	// TODO Consider locusequability

	
}
