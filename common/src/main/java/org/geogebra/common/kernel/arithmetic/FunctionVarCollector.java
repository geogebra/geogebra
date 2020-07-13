package org.geogebra.common.kernel.arithmetic;

import java.util.Iterator;
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
public class FunctionVarCollector implements Traversing {
	private Set<String> variableNames;
	private static FunctionVarCollector collector = new FunctionVarCollector();

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
	 * @param kernel
	 *            kernel
	 * @return variables with collected names
	 */
	public FunctionVariable[] buildVariables(Kernel kernel) {
		FunctionVariable[] fvArray = new FunctionVariable[variableNames.size()];
		Iterator<String> it = variableNames.iterator();
		int i = 0;
		while (it.hasNext()) {
			fvArray[i++] = new FunctionVariable(kernel, it.next());
		}
		return fvArray;
	}
}