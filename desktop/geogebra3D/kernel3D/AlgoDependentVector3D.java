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
    
	@Override
	public Algos getClassName() {
		return Algos.AlgoDependentVector3D;
	}
	
    // for AlgoElement
	@Override
	protected void setInputOutput() {
        input = root.getGeoElementVariables();  
        
        setOnlyOutput(vec);        
        setDependencies(); // done by AlgoElement
    }    
    
    public GeoVector3D getVector3D() { return vec; }
    
    // calc the current value of the arithmetic tree
    @Override
	public final void compute() {   
    	try {
	        temp = ((Vector3DValue) root.evaluate(StringTemplate.defaultTemplate)).getPointAsDouble();
	        vec.setCoords(temp);
	    } catch (Exception e) {
	    	vec.setUndefined();
	    }    
    }   
    
    @Override
	final public String toString(StringTemplate tpl) {         
            return root.toString(tpl);
    }

	// TODO Consider locusequability
}
