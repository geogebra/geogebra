package org.geogebra.common.gui.view.algebra;

import org.geogebra.common.kernel.algos.GetCommand;
import org.geogebra.common.kernel.arithmetic.Equation;
import org.geogebra.common.kernel.arithmetic.EquationValue;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.arithmetic.Inspecting;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.main.Localization;
import org.geogebra.common.util.StringUtil;

public class SuggestionSolve extends Suggestion {
	
	private static final Suggestion SINGLE_SOLVE = new SuggestionSolve();
	private String[] labels;

	public SuggestionSolve(String... labels) {
		this.labels = labels;
	}

	@Override
	public String getCommand(Localization loc) {
		return loc.getMenu("Solve");
	}

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

	public static Suggestion get(GeoElement geo) {
		if (Equation.isAlgebraEquation(geo)
				&& !hasDependentAlgo(geo, SINGLE_SOLVE, null)) {
			String[] vars = ((EquationValue) geo).getEquationVariables();
			if (vars.length == 1) {
				return SINGLE_SOLVE;
			}
			if (vars.length == 2) {
				return getMulti(geo, vars);
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
								&& !hasDependentAlgo((GeoElement) var,
										SINGLE_SOLVE, null);
					}
				});

		if (prev != null) {
			return new SuggestionSolve(prev.getLabelSimple());
		}
		return null;
	}

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
