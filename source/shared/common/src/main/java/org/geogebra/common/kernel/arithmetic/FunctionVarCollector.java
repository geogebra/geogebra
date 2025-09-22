package org.geogebra.common.kernel.arithmetic;

import java.util.Set;
import java.util.TreeSet;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.geos.GeoSymbolic;
import org.geogebra.common.plugin.Operation;

/**
 * Collects all function variables
 * 
 * @author Zbynek Konecny
 */
public final class FunctionVarCollector implements Traversing {
	private Set<String> variableNames;
	private static final FunctionVarCollector collector = new FunctionVarCollector();

	private FunctionVarCollector() {
		// singleton constructor
	}

	@Override
	public ExpressionValue process(ExpressionValue ev) {
		if (ev instanceof FunctionVariable) {
			variableNames.add(((FunctionVariable) ev).getSetVarString());
		}
		if (ev instanceof ExpressionNode) {
			ExpressionNode en = ev.wrap();
			if (en.getOperation() != Operation.FUNCTION
					&& en.getOperation() != Operation.FUNCTION_NVAR
					&& en.getOperation() != Operation.VEC_FUNCTION) {

				checkFunctional(en.getLeft());
				checkFunctional(en.getRight());
			}
		}
		if (ev instanceof GeoSymbolic) {
			GeoSymbolic symbolic = (GeoSymbolic) ev;
			for (FunctionVariable variable : symbolic.getFunctionVariables()) {
				variableNames.add(variable.getSetVarString());
			}
		}
		return ev;
	}

	private void checkFunctional(ExpressionValue right) {
		if (right instanceof FunctionalNVar) {
			for (FunctionVariable fv : ((FunctionalNVar) right)
					.getFunctionVariables()) {
				variableNames.add(fv.getSetVarString());
			}
		}
	}

	/**
	 * Resets and returns the collector
	 * 
	 * @return function variable collector
	 */
	public static FunctionVarCollector getCollector() {
		collector.variableNames = new TreeSet<>();
		return collector;
	}

	/**
	 * If all variables are registered, just map their names to FunctionVariable objects.
	 * If some variables are not registered, sort collected variables in their registration order
	 * (unregistered variables go last, alphabetically).
	 * @param kernel
	 *            kernel
	 * @return variables with collected names
	 */
	public FunctionVariable[] buildVariables(Kernel kernel) {
		boolean allRegistered = true;
		for (String variableName : variableNames) {
			if (!kernel.getConstruction().isRegisteredFunctionVariable(variableName)) {
				allRegistered = false;
			}
		}
		String[] registeredFV = kernel.getConstruction().getRegisteredFunctionVariables();

		int size = allRegistered ? registeredFV.length : variableNames.size();
		FunctionVariable[] fvArray = new FunctionVariable[size];
		int i = 0;
		for (String known: registeredFV) {
			if (allRegistered || variableNames.contains(known)) {
				fvArray[i++] = new FunctionVariable(kernel, known);
			}
		}
		if (allRegistered) {
			return fvArray;
		}
		for (String variableName : variableNames) {
			if (!kernel.getConstruction().isRegisteredFunctionVariable(variableName)) {
				fvArray[i++] = new FunctionVariable(kernel, variableName);
			}
		}
		return fvArray;
	}
}