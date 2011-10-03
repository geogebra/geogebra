/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

/*
 * AlgoDependentNumber.java
 *
 * Created on 30. August 2001, 21:37
 */

package geogebra.kernel;

import geogebra.kernel.arithmetic.ExpressionNode;
import geogebra.kernel.arithmetic.Function;
import geogebra.kernel.arithmetic.FunctionVariable;
import geogebra.kernel.arithmetic.NumberValue;
import geogebra.kernel.kernelND.GeoCurveCartesianND;

/**
 * Cartesian curve: Curve[ x-expression in var, y-expression in var, var, from, to]
 * @author Markus Hohenwarter
 * @version 
 */
public class AlgoCurveCartesian extends AlgoElement {

	private static final long serialVersionUID = 1L;
	private NumberValue[] coords; // input
	private NumberValue from, to;  // input
    private GeoNumeric localVar;     // input
    private GeoCurveCartesianND curve;  // output
        
    /** Creates new AlgoJoinPoints */
    public AlgoCurveCartesian(Construction cons, String label, 
			NumberValue[] coords,  
			GeoNumeric localVar, NumberValue from, NumberValue to)  {
    	super(cons);
    	
    	this.coords = coords;
    	this.from = from;
    	this.to = to;
    	this.localVar = localVar;
        
    	// we need to create Function objects for the coord NumberValues,
		// so let's get the expressions of xcoord and ycoord and replace
		// the localVar by a functionVar		
		FunctionVariable funVar = new FunctionVariable(kernel);
		funVar.setVarString(localVar.label);
		
		ExpressionNode[] exp = new ExpressionNode[coords.length];
		Function[] fun = new Function[coords.length];

		for (int i=0;i<coords.length;i++){
			exp[i]= kernel.convertNumberValueToExpressionNode(coords[i]);
			exp[i].replaceAndWrap(localVar, funVar);
			fun[i] = new Function(exp[i], funVar);
		}
        
		// create the curve
		curve = createCurve(cons, fun);
       
        setInputOutput(); // for AlgoElement
        
        // compute value of dependent number
        compute();      
        curve.setLabel(label);
    }   
    
    /** creates a curve
     * @param cons
     * @param fun
     * @return a curve
     */
    protected GeoCurveCartesianND createCurve(Construction cons, Function[] fun){
    	return new GeoCurveCartesian(cons, fun[0], fun[1]);
    }
    
	public String getClassName() {
		return "AlgoCurveCartesian";
	}
    
    // for AlgoElement
	protected void setInputOutput() {
        input = new GeoElement[coords.length+3];
        
        for (int i=0;i<coords.length;i++)
        	input[i] = coords[i].toGeoElement();
    	input[coords.length] = localVar;
    	input[coords.length+1] = from.toGeoElement();
    	input[coords.length+2] = to.toGeoElement();    	
        
        output = new GeoElement[1];        
        output[0] = curve;        
        setDependencies(); // done by AlgoElement
    }    
    
    public GeoCurveCartesianND getCurve() { return curve; }        
    
    protected final void compute() {    
    	// the coord-functions don't have to be updated,
    	// so we only set the interval
    	curve.setInterval(from.getDouble(), to.getDouble());
    }   
    
    final public String toString() {
        return getCommandDescription();
    }
}
