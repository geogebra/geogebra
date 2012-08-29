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

package geogebra.common.kernel.algos;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.arithmetic.Equation;
import geogebra.common.kernel.arithmetic.ExpressionNode;
import geogebra.common.kernel.arithmetic.ExpressionValue;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.arithmetic.Polynomial;
import geogebra.common.kernel.geos.GeoLine;
import geogebra.common.kernel.geos.GeoPoint;

/**
 *
 * @author  Markus
 * @version 
 */
public class AlgoDependentLine extends AlgoElement implements EvaluateAtPoint, DependentAlgo {

    protected Equation equation;
	protected GeoLine line1;
	protected GeoLine line2;
    protected ExpressionValue [] ev = new ExpressionValue[3];  // input
    protected ExpressionNode root;
    protected GeoLine g;     // output       
    
        
    /** Creates new AlgoDependentLine 
     * @param cons 
     * @param label 
     * @param equ */
    public AlgoDependentLine(Construction cons, String label, Equation equ) {        
       	super(cons, false); // don't add to construction list yet
        equation = equ;  
        Polynomial lhs = equ.getNormalForm();
        
        ev[0] = lhs.getCoefficient("x");        
   		ev[1] = lhs.getCoefficient("y");        
   		ev[2] = lhs.getConstantCoefficient(); 
   		
   		// check coefficients
        for (int i=0; i<3; i++) {
            if (ev[i].isConstant()) ev[i] = ev[i].evaluate(StringTemplate.defaultTemplate);
                       
            // check that coefficient is a number: this may throw an exception
            ExpressionValue eval = ev[i].evaluate(StringTemplate.defaultTemplate);
            ((NumberValue) eval).getDouble();            
        }
        
        // if we get here, all is ok: let's add this algorithm to the construction list
        cons.addToConstructionList(this, false);
        
        g = new GeoLine(cons);
        setInputOutput(); // for AlgoElement
        
        // compute value of dependent number        
        compute();      
        g.setLabel(label);        
    }   
    
	@Override
	public Algos getClassName() {
		return Algos.AlgoDependentLine;
	}
    
    // for AlgoElement
	@Override
	protected void setInputOutput() {
        input = equation.getGeoElementVariables();     
			
			
        setOutputLength(1);        
        setOutput(0,g);        
        setDependencies(); // done by AlgoElement
    }    
    
    public GeoLine getLine() { return g; }
    
    // calc the current value of the arithmetic tree
    @Override
	public final void compute() {  
    	
	    	try {
		        g.x = ev[0].evaluateNum().getDouble();
		        g.y = ev[1].evaluateNum().getDouble();
		        g.z = ev[2].evaluateNum().getDouble();
		        
		        // other algos might use the startPoint so we have to update it
		        if (g.getStartPoint() != null)
		        	g.setStandardStartPoint();
		    } catch (Throwable e) {
				g.setUndefined();
			}
    }   
          
    @Override
	final public String toString(StringTemplate tpl) { 
    	return equation.toString(tpl);
    }
    
    @Override
	final public String toRealString(StringTemplate tpl) { 
    	return equation.toRealString(tpl);
    }
    
    final public double evaluate(GeoPoint P) {
    	double mat0 = ev[0].evaluateNum().getDouble(); // x\u00b2
		double mat1 = ev[1].evaluateNum().getDouble(); // y\u00b2
		double mat2 = ev[2].evaluateNum().getDouble(); // constant
		return P.x *mat0 + P.y * mat1 + P.z * mat2;
	}

	// TODO Consider locusequability
    
}
