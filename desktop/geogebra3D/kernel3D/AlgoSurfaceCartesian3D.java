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

package geogebra3D.kernel3D;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.algos.AlgoElement;
import geogebra.common.kernel.arithmetic.ExpressionNode;
import geogebra.common.kernel.arithmetic.FunctionNVar;
import geogebra.common.kernel.arithmetic.FunctionVariable;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.commands.Commands;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoNumeric;
import geogebra.common.kernel.kernelND.GeoSurfaceCartesianND;

/**
 * Cartesian curve: Curve[ x-expression in var, y-expression in var, var, from, to]
 * @author Markus Hohenwarter
 * @version 
 */
public class AlgoSurfaceCartesian3D extends AlgoElement {

	private static final long serialVersionUID = 1L;
	private NumberValue[] coords; // input
	private NumberValue[] from, to;  // input
    private GeoNumeric[] localVar;     // input
    private GeoSurfaceCartesianND surface;  // output
        
    /** Creates new AlgoJoinPoints */
    public AlgoSurfaceCartesian3D(Construction cons, String label, 
			NumberValue[] coords,  
			GeoNumeric[] localVar, NumberValue[] from, NumberValue[] to)  {
    	super(cons);
    	
    	this.coords = coords;
    	this.from = from;
    	this.to = to;
    	this.localVar = localVar;
        
    	// we need to create Function objects for the coord NumberValues,
		// so let's get the expressions of xcoord and ycoord and replace
		// the localVar by a functionVar		
    	FunctionVariable[] funVar = new FunctionVariable[localVar.length];
    	for (int i=0;i<localVar.length; i++){
    		funVar[i] = new FunctionVariable(kernel);
    		funVar[i].setVarString(localVar[i].getLabel(StringTemplate.defaultTemplate));
    	}
		
		ExpressionNode[] exp = new ExpressionNode[coords.length];
		FunctionNVar[] fun = new FunctionNVar[coords.length];

		for (int i=0;i<coords.length;i++){
			exp[i]= kernel.convertNumberValueToExpressionNode(coords[i]);
			for (int j=0;j<localVar.length; j++)
				exp[i]=exp[i].replace(localVar[j], funVar[j]).wrap();
			fun[i] = new FunctionNVar(exp[i], funVar);
		}
        
		// create the curve
		surface = createCurve(cons, fun);
       
        setInputOutput(); // for AlgoElement
        
        // compute value of dependent number
        compute();      
        surface.setLabel(label);
    }   
    
    /** creates a curve
     * @param cons
     * @param fun
     * @return a curve
     */
    protected GeoSurfaceCartesianND createCurve(Construction cons, FunctionNVar[] fun){
    	return new GeoSurfaceCartesian3D(cons, fun);
    }
    
    @Override
	public Commands getClassName() {
        return Commands.Surface;
    }
    
    // for AlgoElement
	@Override
	protected void setInputOutput() {
        input = new GeoElement[coords.length+3*localVar.length];
        
        for (int i=0;i<coords.length;i++)
        	input[i] = coords[i].toGeoElement();
        for (int i=0;i<localVar.length;i++){
        	input[coords.length+3*i] = localVar[i];
        	input[coords.length+3*i+1] = from[i].toGeoElement();
        	input[coords.length+3*i+2] = to[i].toGeoElement(); 
        }
        
        setOnlyOutput(surface);        
        setDependencies(); // done by AlgoElement
    }    
    
    public GeoSurfaceCartesianND getSurface() { return surface; }        
    
    @Override
	public final void compute() {    
    	// the coord-functions don't have to be updated,
    	// so we only set the interval
    	double[] min = new double[from.length];
    	double[] max = new double[to.length];
    	for (int i=0; i<from.length; i++){
    		min[i]=from[i].getDouble();
    		max[i]=to[i].getDouble();
    	}  	
    	surface.setIntervals(min,max);
    }   
    
    @Override
    final public String toString(StringTemplate tpl) {
        return getCommandDescription(tpl);
    }

	// TODO Consider locusequability
}
