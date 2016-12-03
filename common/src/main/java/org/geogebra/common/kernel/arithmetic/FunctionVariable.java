/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package org.geogebra.common.kernel.arithmetic;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.plugin.Operation;

/**
 * @author Markus Hohenwarter
 */
public class FunctionVariable extends MyDouble {

	private String varStr = "x";

	/**
	 * Creates new function variable
	 * 
	 * @param kernel
	 *            kernel
	 */
	public FunctionVariable(Kernel kernel) {
		super(kernel);
	}

	/**
	 * Creates new function variable
	 * 
	 * @param kernel
	 *            kernel
	 * @param varStr
	 *            variable name
	 */
	public FunctionVariable(Kernel kernel, String varStr) {
		super(kernel);
		setVarString(varStr);
	}

	/**
	 * Returns true to avoid deep copies in an ExpressionNode tree.
	 */
	@Override
	final public boolean isConstant() {
		return false;
	}

	@Override
	final public FunctionVariable deepCopy(Kernel k) {
		return new FunctionVariable(k, varStr);
	}

	/**
	 * Changes variable name
	 * 
	 * @param varStr
	 *            new variable name
	 */
	public void setVarString(String varStr) {
		this.varStr = varStr;
	}

	/**
	 * @return variable name
	 */
	public String getSetVarString() {
		return varStr;
	}

	@Override
	final public String toString(final StringTemplate tpl) {
		return tpl.printVariableName(varStr);
	}

	/*
	 * interface NumberValue removed Michael Borcherds 2008-05-20 (see MyDouble)
	 */
	// final public MyDouble getNumber() {
	// used in expression node tree: be careful
	// return new MyDouble(this);
	// }

	@Override
	public ExpressionValue derivative(FunctionVariable fv, Kernel kernel0) {
		if (fv == this) {
			return new MyDouble(kernel0, 1);
		}
		return new MyDouble(kernel0, 0);
	}

	@Override
	public ExpressionValue integral(FunctionVariable fv, Kernel kernel0) {
		if (fv == this) {
			return new ExpressionNode(kernel0, this, Operation.POWER,
					new MyDouble(kernel0, 2)).divide(2);
		}
		return new ExpressionNode(kernel0, this, Operation.MULTIPLY, fv);
	}

	@Override
	/**
	 * checks if set to same value, not if it's the same FunctionVariable object
	 */
	public boolean equals(Object o) {
		return super.equals(o);
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}

}
