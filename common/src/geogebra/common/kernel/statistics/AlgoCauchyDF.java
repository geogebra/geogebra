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

/**
 * algorithm for Cauchy[0,1,x]
 * @author  Michael
 */
public class AlgoCauchyDF extends AlgoElement implements AlgoDistributionDF {

	private NumberValue a, b;  // input
	private BooleanValue cumulative; // optional input
	private GeoFunction ret;     // output           
        
    @SuppressWarnings("javadoc")
	public AlgoCauchyDF(Construction cons, String label, NumberValue mean, NumberValue sd, BooleanValue cumulative) {       
  	  	this(cons, mean, sd, cumulative);
        ret.setLabel(label);
      }   
    
    @SuppressWarnings("javadoc")
	public AlgoCauchyDF(Construction cons, NumberValue a, NumberValue b, BooleanValue cumulative) {       
  	  super(cons); 
        this.a = a;
        this.b = b;
        this.cumulative = cumulative;
        ret = new GeoFunction(cons); 
        setInputOutput(); // for AlgoElement
        
        // compute angle
        compute();     
      }   
    
    @Override
	public Algos getClassName() {
        return Algos.AlgoNormalDF;
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
		FunctionVariable fv = new FunctionVariable(kernel);
		ExpressionNode x0 = new ExpressionNode(kernel, a);
		ExpressionNode g = new ExpressionNode(kernel, b);
		ExpressionNode en = new ExpressionNode(kernel, fv);
		
		if (cumulative != null && cumulative.getBoolean()) {

			
			en = en.subtract(x0).divide(g.abs()).atan().divide(Math.PI).plus(0.5);

			// old hack:
			//command = "1/pi atan((x-("+x0+"))/abs("+g+"))+0.5";

		} else {

			en = g.abs().divide(g.square().plus(en.subtract(x0).square()).multiply(Math.PI));

			// old hack:
			//command = "1/pi abs("+g+")/(("+g+")^2+(x-("+x0+"))^2)";
		}
		
		Function tempFun = new Function(en, fv);
		tempFun.initFunction();
		
		ret.setFunction(tempFun);


    }

	// TODO Consider locusequability
	
}
