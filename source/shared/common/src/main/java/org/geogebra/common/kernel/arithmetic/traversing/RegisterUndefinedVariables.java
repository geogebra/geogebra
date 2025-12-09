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