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
import geogebra.kernel.arithmetic.ExpressionValue;
import geogebra.kernel.arithmetic.MyBoolean;

/**
 *
 * @author  Markus
 * @version 
 */
public class AlgoDependentBoolean extends AlgoElement {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ExpressionNode root;  // input
    private GeoBoolean bool;     // output              
        
    public AlgoDependentBoolean(Construction cons, String label, ExpressionNode root) {
    	super(cons);
        this.root = root;  
        
        bool = new GeoBoolean(cons);
        setInputOutput(); // for AlgoElement
        
        // compute value of dependent number
        compute();      
        bool.setLabel(label);
    }   
    
	public String getClassName() {
		return "AlgoDependentBoolean";
	}
    
    // for AlgoElement
	protected void setInputOutput() {
        input = root.getGeoElementVariables();
        
        output = new GeoElement[1];        
        output[0] = bool;        
        setDependencies(); // done by AlgoElement
    }    
    
    public GeoBoolean getGeoBoolean() { return bool; }
    
    // calc the current value of the arithmetic tree
    protected final void compute() {	
    	try {
    		
    		// needed for eg Sequence[If[liste1(i) < a
    		boolean oldLabelStatus = cons.isSuppressLabelsActive();
    		kernel.getConstruction().setSuppressLabelCreation(true);
    		
    		ExpressionValue ev = root.evaluate();
    		kernel.getConstruction().setSuppressLabelCreation(oldLabelStatus);
    		
    		if (ev.isGeoElement())
        		bool.setValue(((GeoBoolean) ev).getBoolean());
    		else
    			bool.setValue(((MyBoolean) ev).getBoolean());
    	} catch (Exception e) {
    		bool.setUndefined();
    	}
    }   
    
    final public String toString() {
        // was defined as e.g.  c = a & b
        return root.toString();
    }
    
    final public String toRealString() {
        // was defined as e.g.  c = a & b
        return root.toRealString();
    }
}
