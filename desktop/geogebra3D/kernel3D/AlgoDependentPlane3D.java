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

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.algos.Algos;
import geogebra.common.kernel.arithmetic.Equation;
import geogebra.common.kernel.arithmetic.ExpressionNode;
import geogebra.common.kernel.arithmetic.ExpressionValue;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.arithmetic.Polynomial;

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
            if (ev[i].isConstant()) ev[i] = ev[i].evaluate(StringTemplate.defaultTemplate);
                       
            // check that coefficient is a number: this may throw an exception
            ExpressionValue eval = ev[i].evaluate(StringTemplate.defaultTemplate);
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
    
    
	@Override
	public Algos getClassName() {
		return Algos.AlgoDependentPlane;
	}
    
    // for AlgoElement
	@Override
	protected void setInputOutput() {
        input = equation.getGeoElementVariables();   
			
        setOnlyOutput(p);        
        setDependencies(); // done by AlgoElement
    }    
    
    /**
     * @return the plane
     */
    public GeoPlane3D getPlane() { return p; }
    
    // calc the current value of the arithmetic tree
    @Override
	public final void compute() {  

    	try {
    		p.setEquation(
    				ev[0].evaluateNum().getDouble(),
    				ev[1].evaluateNum().getDouble(),
    				ev[2].evaluateNum().getDouble(),
    				ev[3].evaluateNum().getDouble()
    		);
    	} catch (Throwable e) {
    		p.setUndefined();
    	}
    }   
          
    @Override
	final public String toString(StringTemplate tpl) { 
    	return equation.toString(tpl);
    } 

	// TODO Consider locusequability
    
}
