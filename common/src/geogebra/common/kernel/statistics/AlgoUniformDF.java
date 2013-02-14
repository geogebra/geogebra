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
import geogebra.common.kernel.arithmetic.MyDouble;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.commands.Commands;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoFunction;

/**
 * algorithm for Uniform[a, b, x, boolean]
 * @author  Michael
 */
public class AlgoUniformDF extends AlgoElement {

	private NumberValue a, b;  // input
	private BooleanValue cumulative; // optional input
	private GeoFunction ret;     // output           

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
		ret = DistributionFunctionFactory.zeroWhenLessThan(a,cons); 


		setInputOutput(); // for AlgoElement

		// compute angle
		compute();     
	}   

	@Override
	public Commands getClassName() {
		return Commands.Uniform;
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

		FunctionVariable fv = ret.getFunctionVariables()[0];
		
		ExpressionNode lessThanB = fv.wrap().lessThan(b);

		ExpressionNode mainBranch;
		MyDouble rightBranch;
		
		if (cumulative != null && cumulative.getBoolean()) {			
			mainBranch = fv.wrap().subtract(a).divide(b.wrap().subtract(a));
			rightBranch = new MyDouble(kernel,1);			
		} else {
			mainBranch = b.wrap().subtract(a).reciprocate();
			rightBranch = new MyDouble(kernel,0);			
		}
		
		ExpressionNode en = lessThanB.ifElse(mainBranch,rightBranch);
		ret.getFunctionExpression().setRight(en);

		ret.setDefined(true);		
	}

	// TODO Consider locusequability


}
