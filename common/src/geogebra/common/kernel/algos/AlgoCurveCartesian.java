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

package geogebra.common.kernel.algos;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.arithmetic.ExpressionNode;
import geogebra.common.kernel.arithmetic.Function;
import geogebra.common.kernel.arithmetic.FunctionVariable;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.geos.GeoCurveCartesian;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoNumeric;
import geogebra.common.kernel.kernelND.GeoCurveCartesianND;

/**
 * Cartesian curve: Curve[ x-expression in var, y-expression in var, var, from, to]
 * @author Markus Hohenwarter
 * @version 
 */
public class AlgoCurveCartesian extends AlgoElement {

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
		funVar.setVarString(localVar.getLabelSimple());
		
		ExpressionNode[] exp = new ExpressionNode[coords.length];
		Function[] fun = new Function[coords.length];

		for (int i=0;i<coords.length;i++){
			exp[i]= kernel.convertNumberValueToExpressionNode(coords[i]);
			exp[i]=exp[i].replace(localVar, funVar).wrap();
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
    
	@Override
	public Algos getClassName() {
		return Algos.AlgoCurveCartesian;
	}
    
    // for AlgoElement
	@Override
	protected void setInputOutput() {
        input = new GeoElement[coords.length+3];
        
        for (int i=0;i<coords.length;i++)
        	input[i] = coords[i].toGeoElement();
    	input[coords.length] = localVar;
    	input[coords.length+1] = from.toGeoElement();
    	input[coords.length+2] = to.toGeoElement();    	
           
        super.setOutputLength(1);
        super.setOutput(0, curve);
        setDependencies(); // done by AlgoElement
    }    
    
    public GeoCurveCartesianND getCurve() { return curve; }        

    @Override
	public final void compute() {

    	// take care to set the curve undefined
    	// if its predecessors are undefined
    	for (int i = 0; i <= 1; i++) {
    		AlgoElement algo = null;
    		if (coords[i].toGeoElement() != null)
    			algo = (coords[i].toGeoElement()).getParentAlgorithm();
    		if (algo != null) {
    			for (GeoElement geo: algo.getInput()) {
    				if (!geo.isDefined()) {
    					curve.setUndefined();
    					return;
    				}
    			}
    		}
    	}
    	curve.setDefined(true);

    	// the coord-functions don't have to be updated,
    	// so we only set the interval
    	curve.setInterval(from.getDouble(), to.getDouble());
    }

    @Override
	final public String toString(StringTemplate tpl) {
        return getCommandDescription(tpl);
    }

	// TODO Consider locusequability
}
