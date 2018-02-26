package org.geogebra.common.gui.view.algebra;

import org.geogebra.common.kernel.algos.GetCommand;
import org.geogebra.common.kernel.arithmetic.Equation;
import org.geogebra.common.kernel.arithmetic.EquationValue;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.main.Localization;
import org.geogebra.common.util.StringUtil;

public class SuggestionSolve extends Suggestion {
	
	private static Suggestion SINGLE_SOLVE = new SuggestionSolve();
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
	public void execute(GeoElementND geo) {
		geo.getKernel().getAlgebraProcessor().processAlgebraCommand(
				"Solve[" + getLabels(geo) + "]", true);
	}

	public static Suggestion get(GeoElement geo) {
		if (Equation.isAlgebraEquation(geo)
				&& !hasDependentAlgo(geo, SINGLE_SOLVE)) {
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

	private static Suggestion getMulti(GeoElement geo, String[] vars) {
		GeoElementND prev = geo;
		do {

			GeoElementND newPrev = geo.getConstruction().getPrevious(prev);

			if (newPrev == prev) {
				// eg happens for polygons
				return null;
			}

			prev = newPrev;

			if (Equation.isAlgebraEquation(prev)
					&& subset(
					((EquationValue) prev).getEquationVariables(), vars)
					&& !hasDependentAlgo(prev, SINGLE_SOLVE)) {
				return new SuggestionSolve(prev.getLabelSimple());
			}
		} while (prev != null);
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
	protected boolean sameAlgoType(GetCommand className, GeoElement[] input) {
		return className == Commands.Solve || className == Commands.NSolve;
	}
}
