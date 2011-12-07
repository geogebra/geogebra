/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

/*
 * AlgoDependentPoint.java
 *
 * Created on 30. August 2001, 21:37
 */

package geogebra.kernel.algos;

import geogebra.common.kernel.algos.AlgoElement;
import geogebra.common.kernel.arithmetic.ExpressionNode;
import geogebra.common.kernel.arithmetic.VectorValue;
import geogebra.common.kernel.geos.GeoVec2D;
import geogebra.common.kernel.AbstractConstruction;
import geogebra.common.kernel.AbstractKernel;
import geogebra.kernel.geos.GeoPoint2;

/**
 *
 * @author  Markus
 * @version 
 */
public class AlgoDependentPoint extends AlgoElement {

    private ExpressionNode root;  // input
    private GeoPoint2 P;     // output         
    
    private GeoVec2D temp;
        
    /** Creates new AlgoJoinPoints 
     * @param cons 
     * @param label 
     * @param root expression defining the result
     * @param complex true if result is complex number*/
    public AlgoDependentPoint(AbstractConstruction cons, String label, ExpressionNode root, boolean complex) {
    	this(cons, root, complex);
    	P.setLabel(label);
    }   
    
    public AlgoDependentPoint(AbstractConstruction cons, ExpressionNode root, boolean complex) {
    	super(cons);
        this.root = root;        
        P = new GeoPoint2(cons); 
        
        
        setInputOutput(); // for AlgoElement

        if (complex)
    		P.setMode(AbstractKernel.COORD_COMPLEX);

    	// compute value of dependent number
        compute();      
    }   
    
	@Override
	public String getClassName() {
		return "AlgoDependentPoint";
	}
	
    // for AlgoElement
	@Override
	protected void setInputOutput() {
        input = root.getGeoElementVariables();  
        
        setOutputLength(1);        
        setOutput(0,P);        
        setDependencies(); // done by AlgoElement
    }    
    
    public GeoPoint2 getPoint() { return P; }
    
    public ExpressionNode getExpressionNode() {
    	return root;
    }
    
    // calc the current value of the arithmetic tree
    @Override
	public final void compute() {   
    	try {
	        temp = (GeoVec2D)((VectorValue) root.evaluate()).getVector();
	        if (Double.isInfinite(temp.x) || Double.isInfinite(temp.y)) {
	        	P.setUndefined();
	        } else {
				P.setCoords( temp.x, temp.y, 1.0); 
	        }		
	        
	        //P.setMode(temp.getMode());
	        
    	} catch (Exception e) {
	    	P.setUndefined();
	    }
    }   
    
    @Override
	final public String toString() {              
        return root.toString();
    }
    
    @Override
	final public String toRealString() {              
        return root.toRealString();
    }
}
