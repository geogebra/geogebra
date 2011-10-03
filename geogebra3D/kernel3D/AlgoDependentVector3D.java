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

package geogebra3D.kernel3D;

import geogebra.kernel.Construction;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoVector;
import geogebra.kernel.arithmetic.ExpressionNode;
import geogebra.kernel.arithmetic3D.Vector3DValue;
import geogebra.main.Application;

/**
 *
 * @author  Michael
 * @version 
 */
public class AlgoDependentVector3D extends AlgoElement3D {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ExpressionNode root;  // input
    private GeoVector3D vec;     // output         
    
    private double[] temp;
        
    /** Creates new AlgoDependentVector */
    public AlgoDependentVector3D(Construction cons, String label, ExpressionNode root) {
    	super(cons);
        this.root = root;        
        
        vec = new GeoVector3D(cons); 
        setInputOutput(); // for AlgoElement
        
        // compute value of dependent number
        //v.z = 0.0d;  
        compute();      
        vec.setLabel(label);
    }   
    
	public String getClassName() {
		return "AlgoDependentVector3D";
	}
	
    // for AlgoElement
	protected void setInputOutput() {
        input = root.getGeoElementVariables();  
        
        output = new GeoElement[1];        
        output[0] = vec;        
        setDependencies(); // done by AlgoElement
    }    
    
    public GeoVector3D getVector3D() { return vec; }
    
    // calc the current value of the arithmetic tree
    protected final void compute() {   
    	try {
	        temp = ((Vector3DValue) root.evaluate()).getPointAsDouble();
	        vec.setCoords(temp);
	    } catch (Exception e) {
	    	vec.setUndefined();
	    }    
    }   
    
    final public String toString() {         
            return root.toString();
    }
}
