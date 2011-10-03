/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

/*
 * AlgoDependentVector.java
 *
 * Created on 30. August 2001, 21:37
 */

package geogebra.kernel;

import geogebra.kernel.arithmetic.ExpressionNode;
import geogebra.kernel.arithmetic.VectorValue;

/**
 *
 * @author  Markus
 * @version 
 */
public class AlgoDependentVector extends AlgoElement {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ExpressionNode root;  // input
    private GeoVector v;     // output         
    
    private GeoVec2D temp;
        
    /** Creates new AlgoDependentVector */
    public AlgoDependentVector(Construction cons, String label, ExpressionNode root) {
    	this(cons, root);     
        v.setLabel(label);
    }   
    
    public AlgoDependentVector(Construction cons, ExpressionNode root) {
    	super(cons);
        this.root = root;        
        
        v = new GeoVector(cons); 
        setInputOutput(); // for AlgoElement
        
        // compute value of dependent number
        v.z = 0.0d;  
        compute();      
    }   
    
	public String getClassName() {
		return "AlgoDependentVector";
	}
	
    // for AlgoElement
	protected void setInputOutput() {
        input = root.getGeoElementVariables();  
        
        output = new GeoElement[1];        
        output[0] = v;        
        setDependencies(); // done by AlgoElement
    }    
    
    public GeoVector getVector() { return v; }
    
    // calc the current value of the arithmetic tree
    protected final void compute() {   
    	try {
	        temp = ((VectorValue) root.evaluate()).getVector();
	        v.x = temp.x;
	        v.y = temp.y;    
	    } catch (Exception e) {
	    	v.setUndefined();
	    }    
    }   
    
    final public String toString() {         
            return root.toString();
    }
    
    final public String toRealString() {         
        return root.toRealString();
}
}
