/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.kernel;

import geogebra.kernel.arithmetic.ExpressionNode;


/**
 * Creates a dependent copy of the given GeoElement.
 */
public class AlgoDependentGeoCopy extends AlgoElement {

	private static final long serialVersionUID = 1L;
	private ExpressionNode origGeoNode;
    private GeoElement origGeo, copyGeo;     // input, ouput              
    
    public AlgoDependentGeoCopy(Construction cons, String label, GeoElement origGeoNode) {
    	super(cons);
    	origGeo = origGeoNode;
    	
    	// just for the toString() method
    	this.origGeoNode = new ExpressionNode(kernel, origGeo.evaluate());
    	
        copyGeo = origGeo.copy();
        setInputOutput(); // for AlgoElement
        
        compute();      
        copyGeo.setLabel(label);
    }
    
    public AlgoDependentGeoCopy(Construction cons, String label, ExpressionNode origGeoNode) {
    	super(cons);
    	this.origGeoNode = origGeoNode;
        origGeo = (GeoElement) origGeoNode.evaluate();
        
        copyGeo = origGeo.copy();
        setInputOutput(); // for AlgoElement
        
        compute();      
        copyGeo.setLabel(label);
    }   
    
	public String getClassName() {
		return "Expression";
	}
    
    // for AlgoElement
	protected void setInputOutput() {
        input = new GeoElement[1];
        input[0] = origGeo;
        
        setOutputLength(1);        
        setOutput(0,copyGeo);        
        setDependencies(); // done by AlgoElement
    }    
    
    public GeoElement getGeo() { return copyGeo; }
    
    // copy geo
    protected final void compute() {	
    	try {
    		copyGeo.set(origGeo);
    	} catch (Exception e) {
    		copyGeo.setUndefined();
    	}
    }   
    
    final public String toString() {
    	// we use the expression as it may add $ signs 
    	// to the label like $A$1
    	return origGeoNode.toString();
    }
}
