package org.geogebra.common.gui.view.algebra;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.geogebra.common.gui.view.algebra.scicalc.LabelHiderCallback;
import org.geogebra.common.kernel.arithmetic.Equation;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.arithmetic.Inspecting;
import org.geogebra.common.kernel.arithmetic.SymbolicMode;
import org.geogebra.common.kernel.geos.GeoDummyVariable;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoSymbolic;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.scientific.LabelController;
import org.geogebra.common.util.StringUtil;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

public final class SuggestionSolveForSymbolic extends SuggestionSolve {
	private static final int EQUATION_LIMIT = 4;
	private final List<GeoElementND> geos;
	private final String[] vars;
	private LabelController labelController = new LabelController();

	private SuggestionSolveForSymbolic(List<GeoElementND> geos, String[] vars) {
		super();
		this.geos = geos;
		this.vars = vars;
	}

	private SuggestionSolveForSymbolic(GeoElementND geo, String[] vars) {
		this(Collections.singletonList(geo), vars);
	}

	@Override
	protected void runCommands(GeoElementND geo) {
		labelGeosIfNeeded();
		String command = getCommandText();
		geo.getKernel().getAlgebraProcessor().processAlgebraCommand(
				command, false, new LabelHiderCallback());
	}

	private void labelGeosIfNeeded() {
		for (int i = geos.size() - 1; i >= 0; i--) {
			labelIfNeeded(geos.get(i));
		}
	}

	private void labelIfNeeded(GeoElementND geo) {
		if (!geo.isAlgebraLabelVisible()) {
			labelController.showLabel((GeoElement) geo);
		}
	}

	private String getCommandText() {
		StringBuilder sb = new StringBuilder();
		String varList = getVariableList();
		sb.append("Solve[");
		sb.append(getGeoList());
		if (!varList.isEmpty()) {
			sb.append(", ");
			sb.append(varList);
		}
		sb.append("]");
		return sb.toString();
	}

	private String getVariableList() {
		if (vars == null || vars.length == 0) {
			return "";
		} else if (vars.length == 1) {
			return vars[0];
		}

		StringBuilder sb = new StringBuilder();
		String varList = StringUtil.join(", ", vars);
		sb.append("{");
		sb.append(varList);
		sb.append("}");
		return sb.toString();
	}

	private String getGeoList() {
		if (geos.size() == 0) {
			return "";
		}

		List<String> labels = new ArrayList<>();
		for (GeoElementND geo: geos) {
			labels.add(geo.getLabelSimple());
		}

		if (labels.size() == 1) {
			return labels.get(0);
		}

		return  "{" + StringUtil.join(", ", labels) + "}";
	}

	/**
	 * @param geo construction element
	 * @return whether this suggestion is applicable to the element
	 */
	public static boolean isValid(GeoElementND geo) {
		return isEquation(geo);
	}

	/**
	 * Check if Solve is available for the geo and return suitable suggestion
	 *
	 * @param geo
	 *            construction element
	 * @return suggestion if applicable
	 */
	@SuppressFBWarnings(value = "HSM_HIDING_METHOD",
			justification = "Move getting suggestions to common first.")
	public static Suggestion get(GeoElement geo) {
		if (!isValid(geo)) {
			return null;
		}

		GeoSymbolic symbolic = (GeoSymbolic) geo;
		String[] vars = getVariables(symbolic);
		if (isAlgebraEquation(symbolic)) {
			if (vars.length == 1) {
				return new SuggestionSolveForSymbolic(geo, vars);
			} else {
				return getMulti(symbolic);
			}
		}
		return null;
	}

	private static String[] getVariables(GeoElementND geo) {
		if (!isValid(geo)) {
			return new String[0];
		}

		ExpressionValue symbolicValue = ((GeoSymbolic) geo).getValue();
		Set<GeoElement> varSet = symbolicValue != null
				? symbolicValue.getVariables(SymbolicMode.SYMBOLIC) : Set.of();
		List<String> varStrings = new ArrayList<>();
		for (GeoElement var : varSet) {
			String varName = var instanceof GeoDummyVariable
					? ((GeoDummyVariable) var).getVarName()
					: var.getLabelSimple();
			if (!varStrings.contains(varName)) {
				varStrings.add(varName);
			}
		}
		return varStrings.toArray(new String[0]);
	}

	private static boolean isAlgebraEquation(GeoElementND geo) {
		return isEquation(geo) && geo.isFreeOrExpression();
	}

	private static boolean isEquation(GeoElementND geo) {
		if (!(geo instanceof GeoSymbolic)) {
			return false;
		}
		ExpressionValue value = ((GeoSymbolic) geo).getValue();
		return value != null && value.unwrap() instanceof Equation;
	}

	private static Suggestion getMulti(GeoElement geo) {
		String[] vars = getVariables(geo);
		List<GeoElementND> geos = new ArrayList<>();
		GeoElementND prev = geo;
		if (vars.length < 1 || vars.length > EQUATION_LIMIT) {
			return null;
		}
		while (prev != null && geos.size() < vars.length) {
			geos.add(prev);
			prev  = isValid(prev) ? getPrevious(prev, vars)
					: null;

		}

		GeoElementND next = isValid(geo) ? getNext(geo, vars) : null;
		while (next != null && geos.size() < vars.length) {
			geos.add(next);
			next  = isValid(next) ? getNext(next, vars) : null;
		}

		if (geos.size() != vars.length) {
			return null;
		}

		return new SuggestionSolveForSymbolic(geos, vars);
	}

	private static GeoElementND getPrevious(final GeoElementND geo, final String[] vars) {
		return geo.getConstruction().getPrevious(geo, newEquationInspecting(vars));
	}

	private static GeoElementND getNext(final GeoElementND geo, final String[] vars) {
		return geo.getConstruction().getNext(geo, newEquationInspecting(vars));
	}

	private static Inspecting newEquationInspecting(final String[] vars) {
		return new Inspecting() {

			@Override
			public boolean check(ExpressionValue var) {
				return isAlgebraEquation((GeoElement) var)
						&& subset(getVariables((GeoSymbolic) var), vars)
						&& !SuggestionSolve
						.checkDependentAlgo((GeoElement) var, SINGLE_SOLVE, null);
			}
		};
	}
}