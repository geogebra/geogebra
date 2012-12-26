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
import geogebra.common.kernel.commands.Commands;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoFunction;

/**
 * algorithm for Logistic[0,1,x]
 * @author  Michael
 */
public class AlgoLogisticDF extends AlgoElement implements AlgoDistributionDF {

	private NumberValue mean, scale;  // input
	private BooleanValue cumulative; // optional input
	private GeoFunction ret;     // output           
        
    @SuppressWarnings("javadoc")
	public AlgoLogisticDF(Construction cons, String label, NumberValue mean, NumberValue scale, BooleanValue cumulative) {       
  	  	this(cons, mean, scale, cumulative);
        ret.setLabel(label);
      }   
    
    @SuppressWarnings("javadoc")
	public AlgoLogisticDF(Construction cons, NumberValue mean, NumberValue scale, BooleanValue cumulative) {       
  	  super(cons); 
        this.mean = mean;
        this.scale = scale;
        this.cumulative = cumulative;
        ret = new GeoFunction(cons); 
        setInputOutput(); // for AlgoElement
        
        compute();     
      }   
    
    @Override
	public Commands getClassName() {
		return Commands.Logistic;
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
        input[1] = scale.toGeoElement();
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
		ExpressionNode en = new ExpressionNode(kernel, fv);
		ExpressionNode absS = (new ExpressionNode(kernel, scale)).abs();
		
		en = en.subtract(mean).divide(absS).reverseSign().exp();

		if (cumulative != null && cumulative.getBoolean()) {


			en = en.plus(1).reciprocate();
			
			// old hack
			//processAlgebraCommand( "1/(1+exp(-(x-("+m+"))/abs("+s+")))", true );
			


		} else {

			en = en.divide(absS.multiply(en.plus(1).square()));


			// old hack:
			//processAlgebraCommand( "exp(-(x-("+m+"))/abs("+s+"))/(abs("+s+")*(1+exp(-(x-("+m+"))/abs("+s+")))^2)", true );
		}
		
		Function tempFun = new Function(en, fv);
		tempFun.initFunction();
		
		ret.setFunction(tempFun);


    }

	// TODO Consider locusequability

	
}
