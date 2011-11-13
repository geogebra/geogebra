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

/**
 * @author Markus Hohenwarter
 */
public class FunctionVariable extends MyDouble {
	
	private String varStr = "x";
	
	public FunctionVariable(Kernel kernel) {
		super(kernel);
	}
	
	public FunctionVariable(Kernel kernel, String varStr) {
		super(kernel);
		setVarString(varStr);
	}
	
	/**
	 * Returns true to avoid deep copies in an ExpressionNode tree.
	 */
	final public boolean isConstant() {
		return false;
	}
	
	public void setVarString(String varStr) {
		this.varStr = varStr;
	}
	
	public String getSetVarString() {
		return varStr;
	}
	
	final public String toString() {
		return kernel.printVariableName(varStr);
	}

	/*
     * interface NumberValue
     * removed Michael Borcherds 2008-05-20
     * (see MyDouble)
     */    
    //final public MyDouble getNumber() {    	
		// used in expression node tree: be careful
	//	 return new MyDouble(this);		      
    //}
}
