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
import geogebra.common.kernel.arithmetic.ExpressionNode;
import geogebra.common.kernel.arithmetic.ExpressionValue;
import geogebra.common.kernel.arithmetic.MyBoolean;
import geogebra.common.kernel.geos.GeoBoolean;

/**
 *
 * @author  Markus
 * @version 
 */
public class AlgoDependentBoolean extends AlgoElement {

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
    
	@Override
	public Algos getClassName() {
		return Algos.AlgoDependentBoolean;
	}
    
    // for AlgoElement
	@Override
	protected void setInputOutput() {
        input = root.getGeoElementVariables();
        
        super.setOutputLength(1);
        super.setOutput(0, bool);
        setDependencies(); // done by AlgoElement
    }    
    
    public GeoBoolean getGeoBoolean() { return bool; }
    
    // calc the current value of the arithmetic tree
    @Override
	public final void compute() {	
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
    
    @Override
	final public String toString() {
        // was defined as e.g.  c = a & b
        return root.toString();
    }
    
    @Override
	final public String toRealString() {
        // was defined as e.g.  c = a & b
        return root.toRealString();
    }
}
