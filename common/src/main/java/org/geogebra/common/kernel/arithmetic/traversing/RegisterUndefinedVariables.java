package org.geogebra.common.kernel.arithmetic.traversing;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.arithmetic.FunctionVariable;
import org.geogebra.common.kernel.arithmetic.Inspecting;
import org.geogebra.common.kernel.arithmetic.variable.Variable;

/**
 * Class to detect and register undefined variables and function variables.
 * It is necessary for undo/redoing commands, like NSolve(V(h))
 * see APPS-5507
 */
public class RegisterUndefinedVariables implements Inspecting {
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
		} else if (ev instanceof FunctionVariable) {
			FunctionVariable functionVariable = (FunctionVariable) ev;
			cons.registerFunctionVariable(functionVariable.getSetVarString());
			return true;
		}
		return false;
	}
}