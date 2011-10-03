/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

/*
 * AlgoDependentNumber.java
 *
 * Created on 30. August 2001, 21:37
 */

package geogebra.kernel;

import geogebra.kernel.arithmetic.ExpressionNode;
import geogebra.kernel.arithmetic.NumberValue;

/**
 * Algorithm for dependent numbers, e.g. c = a + b.
 * 
 * @author Markus Hohenwarter
 * @version 
 */
public class AlgoDependentNumber extends AlgoElement {

	private static final long serialVersionUID = 1L;
	private ExpressionNode root;  // input
    private GeoNumeric number;     // output              
        
    /** Creates new AlgoJoinPoints 
     * @param cons 
     * @param label 
     * @param root expression defining the result
     * @param isAngle true for angles 
     * */
    public AlgoDependentNumber(Construction cons, String label, ExpressionNode root, boolean isAngle) {
    	this(cons,root,isAngle);
        number.setLabel(label);
    }   
    public AlgoDependentNumber(Construction cons, ExpressionNode root, boolean isAngle) {
    	super(cons);
        this.root = root;  
        
        // simplify constant integers, e.g. -1 * 300 becomes -300
        root.simplifyConstantIntegers();
        
        if (isAngle) {
            number = new GeoAngle(cons);
        } else {
            number = new GeoNumeric(cons); 
        }
        setInputOutput(); // for AlgoElement
        
        // compute value of dependent number
        compute();      
        
    }   
    
	public String getClassName() {
		return "AlgoDependentNumber";
	}
    
    // for AlgoElement
	protected void setInputOutput() {
        input = root.getGeoElementVariables();
        
        setOutputLength(1);        
        setOutput(0,number);        
        setDependencies(); // done by AlgoElement
    }    
    
    public GeoNumeric getNumber() { return number; }
    
    public ExpressionNode getExpression() { return root; }
    
    // calc the current value of the arithmetic tree
    protected final void compute() {    
    	try {
    		NumberValue nv = (NumberValue) root.evaluate();
	        number.setValue(nv.getDouble());
	    } catch (Exception e) {
	    	number.setUndefined();
		}
    }   
    
    final public String toString() {
        // was defined as e.g.  r = 5a - 3b
        // return 5a - 3b
        return root.toString();
    }
    
    final public String toRealString() {        
        return root.toRealString();
    }
}
