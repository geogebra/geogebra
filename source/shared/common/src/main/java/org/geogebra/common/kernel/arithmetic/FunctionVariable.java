/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
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
	public boolean isDigits() {
		return false;
	}

}
