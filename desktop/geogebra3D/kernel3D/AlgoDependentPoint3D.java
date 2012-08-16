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

package geogebra3D.kernel3D;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.algos.Algos;
import geogebra.common.kernel.arithmetic.ExpressionNode;
import geogebra.common.kernel.arithmetic3D.Vector3DValue;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.locusequ.EquationElement;
import geogebra.common.kernel.locusequ.EquationScope;

/**
 *
 * @author  Markus
 * @version 
 */
public class AlgoDependentPoint3D extends AlgoElement3D {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ExpressionNode root;  // input
    private GeoPoint3D P;     // output         
    
    private double[] temp;
        
    /** Creates new AlgoJoinPoints */
    public AlgoDependentPoint3D(Construction cons, String label, ExpressionNode root) {
    	super(cons);
        this.root = root;        
        
        P = new GeoPoint3D(cons); 
        
        
        setInputOutput(); // for AlgoElement

    	// compute value of dependent number
        compute();      
        

    	P.setLabel(label);
    }   
    
	@Override
	public Algos getClassName() {
		return Algos.AlgoDependentPoint3D;
	}
	
    // for AlgoElement
	@Override
	protected void setInputOutput() {
        input = root.getGeoElementVariables();  
        
        setOnlyOutput(P);        
        setDependencies(); // done by AlgoElement
    }    
    
    public GeoPoint3D getPoint3D() { return P; }
    
    public ExpressionNode getExpressionNode() {
    	return root;
    }
    
    // calc the current value of the arithmetic tree
    @Override
	public final void compute() {   
    	try {
	        temp = ((Vector3DValue) root.evaluate(StringTemplate.defaultTemplate)).getPointAsDouble();
	        if (Double.isInfinite(temp[0]) || Double.isInfinite(temp[1]) || Double.isInfinite(temp[2])) {
	        	P.setUndefined();
	        } else {
				P.setCoords( temp[0], temp[1], temp[2], 1.0); 
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

	// TODO Consider locusequability
}
