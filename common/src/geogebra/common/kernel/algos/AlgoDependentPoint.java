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

package geogebra.common.kernel.algos;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.arithmetic.ExpressionNode;
import geogebra.common.kernel.arithmetic.VectorValue;
import geogebra.common.kernel.geos.GeoPoint;
import geogebra.common.kernel.geos.GeoVec2D;

/**
 *
 * @author  Markus
 * @version 
 */
public class AlgoDependentPoint extends AlgoElement implements AlgoDependent {

    private ExpressionNode root;  // input
    private GeoPoint P;     // output         
    
    private GeoVec2D temp;
        
    /** Creates new AlgoJoinPoints 
     * @param cons 
     * @param label 
     * @param root expression defining the result
     * @param complex true if result is complex number*/
    public AlgoDependentPoint(Construction cons, String label, ExpressionNode root, boolean complex) {
    	this(cons, root, complex);
    	P.setLabel(label);
    }   
    
    public AlgoDependentPoint(Construction cons, ExpressionNode root, boolean complex) {
    	super(cons);
        this.root = root;        
        P = new GeoPoint(cons); 
        
        
        setInputOutput(); // for AlgoElement

        if (complex)
    		P.setMode(Kernel.COORD_COMPLEX);

    	// compute value of dependent number
        compute();      
    }   
    
	@Override
	public Algos getClassName() {
		return Algos.AlgoDependentPoint;
	}
	
    // for AlgoElement
	@Override
	protected void setInputOutput() {
        input = root.getGeoElementVariables();  
        
        setOutputLength(1);        
        setOutput(0,P);        
        setDependencies(); // done by AlgoElement
    }    
    
    public GeoPoint getPoint() { return P; }
    
    public ExpressionNode getExpressionNode() {
    	return root;
    }
    
    // calc the current value of the arithmetic tree
    @Override
	public final void compute() {   
    	try {
	        temp = ((VectorValue) root.evaluate(StringTemplate.defaultTemplate)).getVector();
	        if (Double.isInfinite(temp.getX()) || Double.isInfinite(temp.getY())) {
	        	P.setUndefined();
	        } else {
				P.setCoords( temp.getX(), temp.getY(), 1.0); 
	        }		
	        
	        //P.setMode(temp.getMode());
	        
    	} catch (Exception e) {
	    	P.setUndefined();
	    }
    }   
    
    @Override
	final public String toString(StringTemplate tpl) {              
        return root.toString(tpl);
    }
    
    @Override
	final public String toRealString(StringTemplate tpl) {              
        return root.toRealString(tpl);
    }

	// TODO Consider locusequability
}
