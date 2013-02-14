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
 * algorithm for Triangular[a, b, mode,x, boolean]
 * @author  Michael
 */
public class AlgoTriangularDF extends AlgoElement {

	private NumberValue a, b, mode;  // input
	private BooleanValue cumulative; // optional input
	private GeoFunction ret;     // output           
        
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
        ret = DistributionFunctionFactory.zeroWhenLessThan(a, cons); 


		setInputOutput(); // for AlgoElement
        
        // compute angle
        compute();     
      }   
    
    @Override
	public Commands getClassName() {
		return Commands.Triangular;
	}
    
    // for AlgoElement
    @Override
	protected void setInputOutput() {
    	
    	// dummy function for the "x" argument, eg
    	// Normal[0,1,x]
    	// Normal[0,1,x,true]
		FunctionVariable fv = new FunctionVariable(kernel);	
		GeoFunction dummyFun = fv.wrap().buildFunction(fv);
    	
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
		FunctionVariable fv = ret.getFunctionVariables()[0];	
		
        // make function x<b
		ExpressionNode lessThanB = fv.wrap().lessThan(b);
		
        // make function x<mode
		ExpressionNode lessThanMode = fv.wrap().lessThan(mode);
		
		ExpressionNode branchAtoMode, branchModeToB;
		MyDouble rightBranch;
		
		if (cumulative != null && cumulative.getBoolean()) {


				
			branchAtoMode = fv.wrap().subtract(a).square().divide(bEn.subtract(a).multiply(modeEn.subtract(a)));
			branchModeToB = fv.wrap().subtract(b).square().divide(bEn.subtract(a).multiply(modeEn.subtract(b))).plus(1);
			rightBranch = new MyDouble(kernel,1);
		} else {

			
			
			branchAtoMode = fv.wrap().subtract(a).multiply(2).divide(bEn.subtract(a).multiply(modeEn.subtract(a)));
			branchModeToB = fv.wrap().subtract(b).multiply(2).divide(bEn.subtract(a).multiply(modeEn.subtract(b)));
			rightBranch = new MyDouble(kernel,1);


			// old hack:
			//processAlgebraCommand( "If[x < "+a+", 0, If[x < "+c+", 2(x - ("+a+")) / ("+b+" - ("+a+")) / ("+c+" - ("+a+")), If[x < "+b+", 2(x - ("+b+")) / ("+b+" - ("+a+")) / ("+c+" - ("+b+")), 0]]]", true );
		}
		ExpressionNode middleRight = lessThanMode.ifElse(branchAtoMode, lessThanB.ifElse(branchModeToB,rightBranch));
		
		ret.setDefined(true);
		ret.getFunctionExpression().setRight(middleRight);
		

    }

	// TODO Consider locusequability

	
}
