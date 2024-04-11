package org.geogebra.common.kernel.arithmetic.traversing;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.arithmetic.Inspecting;
import org.geogebra.common.kernel.arithmetic.variable.Variable;

public class RegisterUndefinedVariables implements Inspecting {
	private static RegisterUndefinedVariables replacer = null;
	private final Construction cons;

	public RegisterUndefinedVariables(Construction cons) {
		this.cons = cons;
	}

	@Override
	public boolean check(ExpressionValue ev) {
		if (ev instanceof Variable) {
			Variable variable = (Variable) ev;
			cons.registerFunctionVariable(variable.getName());
			return true;
		}
		return false;
	}

	/**
	 * @return checker (see class Javadoc)
	 */
	public static RegisterUndefinedVariables getInstance(Construction cons) {
		if (replacer == null) {
			replacer = new RegisterUndefinedVariables(cons);
		}
		return replacer;
	}
}