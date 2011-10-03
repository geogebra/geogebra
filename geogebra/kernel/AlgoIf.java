/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.kernel;



/**
 *
 * @author  Markus
 * @version 
 */
public class AlgoIf extends AlgoElement {

	private static final long serialVersionUID = 1L;
	private GeoBoolean condition;     // input
	private GeoElement ifGeo, elseGeo;  // input
	private GeoElement result; // output
    
	/**
	 * Algorithm for handling of an if-then-else construct
	 * @param cons
	 * @param label
	 * @param condition
	 * @param ifGeo
	 * @param elseGeo: may be null
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
    
	public String getClassName() {
		return "AlgoIf";
	}
    
    // for AlgoElement
	protected void setInputOutput() {
    	if (elseGeo != null)
    		input = new GeoElement[3];
    	else
    		input = new GeoElement[2];    	
        input[0] = condition;
        input[1] = ifGeo;
        if (elseGeo != null)
        	input[2] = elseGeo;        	
        
        output = new GeoElement[1];        
        output[0] = result;        
        setDependencies(); // done by AlgoElement
    }    
    
    public GeoElement getGeoElement() { return result; }
    
    // calc the current value of the arithmetic tree
    protected final void compute() {	
    	
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
    
    final public String toString() {        
        return getCommandDescription();
    }
}
