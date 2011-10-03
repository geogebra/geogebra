/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.kernel.arithmetic;

import geogebra.kernel.Kernel;

import java.util.HashSet;



/**
 * A Parametric is a ValidExpression that
 * represents a Line in parametric form
 * (px, py) + t (vx, vy)
 */
public class Parametric extends ValidExpression {
    private ExpressionNode P, v;
    private String parameter;
    private Kernel kernel;

    /**
     * Creates new Parametric P + parameter * v.
     * (X = P + parameter * v)
     */
    public Parametric(Kernel kernel, ExpressionValue P, ExpressionValue v, String parameter) {
    	if (P.isExpressionNode())
    		this.P = (ExpressionNode) P;
    	else
    		this.P = new ExpressionNode(kernel, P);
    	
    	if (v.isExpressionNode())
    		this.v = (ExpressionNode) v;
    	else
    		this.v = new ExpressionNode(kernel, v);
        
        this.parameter = parameter;  
        this.kernel = kernel;
    }

    public ExpressionNode getP() { return P; }
    public ExpressionNode getv() { return v; }
    public String getParameter() { return parameter; } 
    
  
    public String toString() {
        StringBuilder sb = new StringBuilder();        
        sb.append( getLabel() + " : ");
        sb.append( "X = " + P.evaluate() + " + " + parameter + " " + v.evaluate() );
        return sb.toString();    
    }

	public boolean contains(ExpressionValue ev) {
		return P.contains(ev) || v.contains(ev);
	}

	public ExpressionValue deepCopy(Kernel kernel) {
		return new Parametric(kernel, (ExpressionNode) P.deepCopy(kernel), (ExpressionNode) v.deepCopy(kernel), parameter);
	}

	public ExpressionValue evaluate() {
		return null;
	}

	public HashSet getVariables() {
		HashSet vars = new HashSet();
		vars.addAll(P.getVariables());
		vars.addAll(v.getVariables());
		return vars;
	}

	public boolean isBooleanValue() {		
		return false;
	}

	public boolean isConstant() {
		return P.isConstant() && v.isConstant();
	}

	public boolean isExpressionNode() {
		return false;
	}

	public boolean isLeaf() {
		return false;
	}

	public boolean isListValue() {
		return false;
	}

	public boolean isNumberValue() {
		return false;
	}

	public boolean isPolynomialInstance() {
		return false;
	}

	public boolean isTextValue() {
		return false;
	}

	public boolean isVectorValue() {
		return false;
	}

	public void resolveVariables() {
		P.resolveVariables();
		v.resolveVariables();		
	}

	public String toLaTeXString(boolean symbolic) {
		return toString();
	}

	public String toValueString() {
		return toString();
	}

	public boolean isVector3DValue() {
		// TODO Auto-generated method stub
		return false;
	}      
    
	public String toOutputValueString() {
		return toValueString();
	}

	public Kernel getKernel() {
		return kernel;
	}
}
