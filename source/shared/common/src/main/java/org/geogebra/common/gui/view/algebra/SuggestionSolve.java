package org.geogebra.common.gui.view.algebra;

import org.geogebra.common.kernel.algos.GetCommand;
import org.geogebra.common.kernel.arithmetic.Equation;
import org.geogebra.common.kernel.arithmetic.EquationValue;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.arithmetic.Inspecting;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.util.StringUtil;

public class SuggestionSolve extends Suggestion {
	
	static final Suggestion SINGLE_SOLVE = new SuggestionSolve();
	private String[] labels;

	public SuggestionSolve(String... labels) {
		this.labels = labels;
	}

	/**
	 * @param geo
	 *            last element
	 * @return list of labels {previous equations, last element}
	 */
	public String getLabels(GeoElementND geo) {
		if (labels == null || labels.length < 1) {
			return geo.getLabelSimple();
		}
		return "{" + StringUtil.join(", ", labels) + "," + geo.getLabelSimple()
				+ "}";
	}

	@Override
	protected void runCommands(GeoElementND geo) {
		geo.getKernel().getAlgebraProcessor().processAlgebraCommand(
				"Solve[" + getLabels(geo) + "]", false);
	}

	/**
	 * Check if Solve is available for the geo and return suitable suggestion
	 * 
	 * @param geo
	 *            construction element
	 * @return suggestion if applicable
	 */
	public static Suggestion get(GeoElement geo) {
		if (checkDependentAlgo(geo, SINGLE_SOLVE, null)) {
			return null;
		}

		if (Equation.isAlgebraEquation(geo)) {
			String[] vars = ((EquationValue) geo).getEquationVariables();
			if (vars != null) {
				if (vars.length == 1) {
					return SINGLE_SOLVE;
				}
				if (vars.length == 2) {
					return getMulti(geo, vars);
				}
			}
		}
		return null;
	}

	private static Suggestion getMulti(GeoElement geo, final String[] vars) {

		GeoElementND prev = geo.getConstruction().getPrevious(geo,
				new Inspecting() {

					@Override
					public boolean check(ExpressionValue var) {
						return Equation.isAlgebraEquation((GeoElement) var)
								&& subset(((EquationValue) var)
										.getEquationVariables(), vars)
								&& !checkDependentAlgo((GeoElement) var,
										SINGLE_SOLVE, null);
					}
				});

		if (prev != null) {
			return new SuggestionSolve(prev.getLabelSimple());
		}
		return null;
	}

	/**
	 * @param testSet
	 *            potential subset
	 * @param superset
	 *            superset
	 * @return whether potential subset is really a subset of superset
	 */
	public static boolean subset(String[] testSet, String[] superset) {
		if (testSet.length < 1) {
			return false;
		}
		for (String check : testSet) {
			boolean found = false;
			for (String compare : superset) {
				found |= compare.equals(check);
			}
			if (!found) {
				return false;
			}
		}
		return true;
	}

	@Override
	protected boolean allAlgosExist(GetCommand className, GeoElement[] input,
			boolean[] algosMissing) {
		return className == Commands.Solve || className == Commands.NSolve;
	}
}
