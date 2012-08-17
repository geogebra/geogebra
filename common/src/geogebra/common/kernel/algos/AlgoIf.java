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
import geogebra.common.kernel.geos.GeoBoolean;
import geogebra.common.kernel.geos.GeoElement;


/**
 *
 * @author  Markus
 * @version 
 */
public class AlgoIf extends AlgoElement {

	private GeoBoolean condition;     // input
	private GeoElement ifGeo, elseGeo;  // input
	private GeoElement result; // output
    
	/**
	 * Algorithm for handling of an if-then-else construct
	 * @param cons
	 * @param label
	 * @param condition
	 * @param ifGeo
	 * @param elseGeo may be null
	 */        
    public AlgoIf(Construction cons, String label, 
    		GeoBoolean condition, GeoElement ifGeo, GeoElement elseGeo) {
    	super(cons);
    	this.condition = condition;
        this.ifGeo = ifGeo;
        this.elseGeo = elseGeo;               
        
        // create output GeoElement of same type as ifGeo
        result = ifGeo.copyInternal(cons);       	
        
        setInputOutput(); // for AlgoElement
        
        // compute value of dependent number
        compute();      
        result.setLabel(label);
    }   
    
	@Override
	public Algos getClassName() {
		return Algos.AlgoIf;
	}
    
    // for AlgoElement
	@Override
	protected void setInputOutput() {
    	if (elseGeo != null)
    		input = new GeoElement[3];
    	else
    		input = new GeoElement[2];    	
        input[0] = condition;
        input[1] = ifGeo;
        if (elseGeo != null)
        	input[2] = elseGeo;        	
         
        super.setOutputLength(1);
        super.setOutput(0, result);
        setDependencies(); // done by AlgoElement
    }    
    
    public GeoElement getGeoElement() { return result; }
    
    // calc the current value of the arithmetic tree
    @Override
	public final void compute() {	
    	
    	/* TODO do we want this?
    	if (!ifGeo.getClass().isAssignableFrom(elseGeo.getClass()) &&
    			!elseGeo.getClass().isAssignableFrom(ifGeo.getClass())) {
    		result.setUndefined();
    		return;
    	}*/
    	
    	try {
	    	if (condition.getBoolean()) {	    		
	    		result.set(ifGeo);
	    	} else {
	    		if (elseGeo == null)
	    			result.setUndefined();
	    		else
	    			result.set(elseGeo);
	    	}    	
    	} catch (Exception e) {
    		//e.printStackTrace();
    		result.setUndefined();
    	}
    }   
    
    @Override
	final public String toString(StringTemplate tpl) {        
        return getCommandDescription(tpl);
    }

	// TODO Consider locusequability
}
