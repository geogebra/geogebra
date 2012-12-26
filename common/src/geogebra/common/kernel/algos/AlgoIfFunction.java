/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.common.kernel.algos;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.commands.Commands;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoFunction;
import geogebra.common.kernel.geos.GeoFunctionConditional;


/**
 * Special case of If for functions.
 * Example:  If[ x < 2, x^2, x + 2 ]
 * 
 * @author  Markus
 * @version 
 */
public class AlgoIfFunction extends AlgoElement {
	
	private GeoFunction boolFun;     // input
	private GeoFunction ifFun, elseFun;  // input
	private GeoFunctionConditional result; // output
    	   
    public AlgoIfFunction(Construction cons, String label, 
    		GeoFunction boolFun, 
    		GeoFunction ifFun, GeoFunction elseFun) 
    {    
    	this(cons, boolFun, ifFun, elseFun);
    	result.setLabel(label);
    }
    	
    AlgoIfFunction(Construction cons,  
    		GeoFunction boolFun, 
    		GeoFunction ifFun, GeoFunction elseFun) 
    {
    	super(cons);
    	this.boolFun = boolFun;
        this.ifFun = ifFun;
        this.elseFun = elseFun;               
        
        // create output GeoElement of same type as ifGeo
        result = new GeoFunctionConditional(cons, boolFun, ifFun, elseFun);
        
        setInputOutput(); // for AlgoElement
        
        // compute value of dependent number
        compute();        
    }   
    
    @Override
	public Commands getClassName() {
		return Commands.If;
	}
    
    // for AlgoElement
	@Override
	protected void setInputOutput() {
    	if (elseFun != null)
    		input = new GeoElement[3];
    	else
    		input = new GeoElement[2];    	
        input[0] = boolFun;
        input[1] = ifFun;
        if (elseFun != null)
        	input[2] = elseFun;        	

        super.setOutputLength(1);
        super.setOutput(0, result);
        setDependencies(); // done by AlgoElement
    }    
    
    public GeoFunction getGeoFunction() { return result; }
       
    @Override
	public final void compute() {
    	for (int i=0; i < input.length; i++) {
    		if (!input[i].isDefined())
    			result.setUndefined();
    	}
    	
    	// set fun to dummy value to show that the function has changed
		// note: result is a GeoFunctionConditional object 
		result.setFunction(ifFun.getFunction()); 		    
    	result.setDefined(true);
    }   
    
    @Override
	final public String toString(StringTemplate tpl) {        
        return getCommandDescription(tpl);
    }

	// TODO Consider locusequability
}
