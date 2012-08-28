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
import geogebra.common.kernel.algos.Algos;
import geogebra.common.kernel.arithmetic.BooleanValue;
import geogebra.common.kernel.arithmetic.ExpressionNode;
import geogebra.common.kernel.arithmetic.Function;
import geogebra.common.kernel.arithmetic.FunctionVariable;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoFunction;
import geogebra.common.kernel.geos.GeoFunctionConditional;

/**
 * algorithm for Uniform[a, b, x, boolean]
 * @author  Michael
 */
public class AlgoUniformDF extends AlgoElement {

	private NumberValue a, b;  // input
	private BooleanValue cumulative; // optional input
	private GeoFunctionConditional ret;     // output           

	@SuppressWarnings("javadoc")
	public AlgoUniformDF(Construction cons, String label, NumberValue a, NumberValue b, BooleanValue cumulative) {       
		this(cons, a, b, cumulative);
		ret.setLabel(label);
	}   

	@SuppressWarnings("javadoc")
	public AlgoUniformDF(Construction cons, NumberValue a, NumberValue b, BooleanValue cumulative) {       
		super(cons); 
		this.a = a;
		this.b = b;
		this.cumulative = cumulative;
		ret = new GeoFunctionConditional(cons); 


		setInputOutput(); // for AlgoElement

		// compute angle
		compute();     
	}   

	@Override
	public Algos getClassName() {
		return Algos.AlgoUniformDF;
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

		input =  new GeoElement[cumulative == null ? 3 : 4];
		input[0] = a.toGeoElement();
		input[1] = b.toGeoElement();
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

		if (!a.isDefined() || !b.isDefined()) {
			ret.setUndefined();
			return;
		}

		if (a.getDouble() >= b.getDouble()) {
			ret.setUndefined();
			return;    		
		}

		ExpressionNode bEn = new ExpressionNode(kernel, b);


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
			en = en.subtract(a).divide(bEn.subtract(a));
			tempFun = new Function(en,fv);
			ifFun1 = new GeoFunction(cons, tempFun);


			// make function x=1
			fv = new FunctionVariable(kernel);	
			en = new ExpressionNode(kernel, 1);
			tempFun = new Function(en,fv);
			GeoFunction elseFun = new GeoFunction(cons, tempFun);

			// old hack:
			//processAlgebraCommand( "If[x<Min["+a+","+b+"],0,If[x>Max["+a+","+b+"],1,(x-Min["+a+","+b+"])/abs("+b+"-("+a+"))]]", true );


			inner.setIfFunction(ifFun1);
			inner.setElseFunction(elseFun);


		} else {


			fv = new FunctionVariable(kernel);	
			en = bEn.subtract(a).reciprocate();
			tempFun = new Function(en,fv);
			ifFun1 = new GeoFunction(cons, tempFun);

			inner.setIfFunction(ifFun1);
			inner.setElseFunction(ifFun0); // x=0


			// old hack:
			//processAlgebraCommand( "If[x<Min["+a+","+b+"],0,If[x>Max["+a+","+b+"],0,1/abs("+b+"-("+a+"))]]", true );
		}

		ret.setDefined(true);
		ret.setConditionalFunction(condFunxLessThana);
		ret.setIfFunction(ifFun0);
		ret.setElseFunction(inner);


	}

	// TODO Consider locusequability


}
