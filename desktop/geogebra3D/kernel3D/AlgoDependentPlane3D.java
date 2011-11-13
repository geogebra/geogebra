/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

/*
 * AlgoDependentLine.java
 *
 * Created on 29. Oktober 2001
 */

package geogebra3D.kernel3D;

import geogebra.kernel.Construction;
import geogebra.kernel.GeoElement;
import geogebra.kernel.arithmetic.Equation;
import geogebra.kernel.arithmetic.ExpressionNode;
import geogebra.kernel.arithmetic.ExpressionValue;
import geogebra.kernel.arithmetic.NumberValue;
import geogebra.kernel.arithmetic.Polynomial;

/**
 *
 * @author  mathieu
 * @version 
 */
public class AlgoDependentPlane3D extends AlgoElement3D {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Equation equation;
	private NumberValue num;
    private ExpressionValue [] ev = new ExpressionValue[4];  // input
    private ExpressionNode root;
    private GeoPlane3D p;     // output       
    
    
        
    /** Creates new AlgoDependentPlane */
    public AlgoDependentPlane3D(Construction cons, String label, Equation equ) {        
       	super(cons, false); // don't add to construction list yet
        equation = equ;  
        Polynomial lhs = equ.getNormalForm();
        
        ev[0] = lhs.getCoefficient("x");        
   		ev[1] = lhs.getCoefficient("y");        
   		ev[2] = lhs.getCoefficient("z");        
   		ev[3] = lhs.getConstantCoefficient(); 
   		
   		// check coefficients
        for (int i=0; i<4; i++) {
            if (ev[i].isConstant()) ev[i] = ev[i].evaluate();
                       
            // check that coefficient is a number: this may throw an exception
            ExpressionValue eval = ev[i].evaluate();
            ((NumberValue) eval).getDouble();            
        }
        
        // if we get here, all is ok: let's add this algorithm to the construction list
        cons.addToConstructionList(this, false);
        
        p = new GeoPlane3D(cons); 
        setInputOutput(); // for AlgoElement
        
        // compute value of dependent number        
        compute();      
        p.setLabel(label);        
    }   
    
    
	public String getClassName() {
		return "AlgoDependentPlane";
	}
    
    // for AlgoElement
	protected void setInputOutput() {
        input = equation.getGeoElementVariables();   
			
        output = new GeoElement[1];        
        output[0] = p;        
        setDependencies(); // done by AlgoElement
    }    
    
    /**
     * @return the plane
     */
    public GeoPlane3D getPlane() { return p; }
    
    // calc the current value of the arithmetic tree
    protected final void compute() {  

    	try {
    		p.setEquation(
    				((NumberValue) ev[0].evaluate()).getDouble(),
    				((NumberValue) ev[1].evaluate()).getDouble(),
    				((NumberValue) ev[2].evaluate()).getDouble(),
    				((NumberValue) ev[3].evaluate()).getDouble()
    		);
    	} catch (Throwable e) {
    		p.setUndefined();
    	}
    }   
          
    final public String toString() { 
    	return equation.toString();
    } 
    
}
