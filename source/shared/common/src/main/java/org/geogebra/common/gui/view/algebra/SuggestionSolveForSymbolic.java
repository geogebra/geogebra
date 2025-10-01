package org.geogebra.common.gui.view.algebra;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.geogebra.common.gui.view.algebra.scicalc.LabelHiderCallback;
import org.geogebra.common.kernel.arithmetic.Equation;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
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
	public void execute(GeoElementND geo) {
		labelGeosIfNeeded();
		String command = getCommandText();
		geo.getApp().getAsyncManager().scheduleCallback(() -> {
			geo.getKernel().getAlgebraProcessor().processAlgebraCommand(
					command, false, new LabelHiderCallback());
			geo.getKernel().storeUndoInfo();
		});
	}

	private void labelGeosIfNeeded() {
		for (int i = geos.size() - 1; i >= 0; i--) {
			labelIfNeeded(geos.get(i));
		}
	}

	private void labelIfNeeded(GeoElementND geo) {
		if (!geo.isAlgebraLabelVisible()) {
			labelController.showLabel(geo);
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
		if (geos.isEmpty()) {
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
		String[] vars = getVariables(symbolic).toArray(new String[0]);
		if (isAlgebraEquation(symbolic)) {
			if (vars.length == 1) {
				return new SuggestionSolveForSymbolic(geo, vars);
			} else {
				return getMulti(symbolic);
			}
		}
		return null;
	}

	private static Set<String> getVariables(GeoElementND geo) {
		if (!isValid(geo)) {
			return Set.of();
		}
		Set<String> varStrings = new TreeSet<>();
		addVars(varStrings, geo);
		return varStrings;
	}

	private static void addVars(Set<String> varStrings, GeoElementND geo) {
		ExpressionValue symbolicValue = ((GeoSymbolic) geo).getValue();
		Set<GeoElement> varSet = symbolicValue != null
				? symbolicValue.getVariables(SymbolicMode.SYMBOLIC) : Set.of();

		for (GeoElement var : varSet) {
			String varName = var instanceof GeoDummyVariable
					? ((GeoDummyVariable) var).getVarName()
					: var.getLabelSimple();
			varStrings.add(varName);
		}
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
		List<GeoElementND> geos = new ArrayList<>();
		GeoElementND prev = geo;
		while (isValid(prev) && geos.size() < EQUATION_LIMIT) {
			geos.add(0, prev);
			prev = getPrevious(prev);
		}
		int prevGeos = geos.size();
		GeoElementND next = getNext(geo);

		while (isValid(next) && geos.size() - prevGeos < EQUATION_LIMIT) {
			geos.add(next);
			next = getNext(next);
		}
		Set<String> variables = new TreeSet<>();
		for (int last =  prevGeos - 1; last < geos.size(); last++) {
			for (int first = last - 1; first >= 0; first--) {
				variables.clear();
				List<GeoElementND> sublist = geos.subList(first, last + 1);
				sublist.forEach(el -> addVars(variables, el));
				if (variables.size() > EQUATION_LIMIT) {
					break;
				}
				if (sublist.size() == variables.size()) {
					return new SuggestionSolveForSymbolic(sublist,
							variables.toArray(new String[0]));
				}
			}
		}
		return null;
	}

	private static GeoElementND getPrevious(final GeoElementND geo) {
		return geo.getConstruction().getPrevious(geo,
				SuggestionSolveForSymbolic::isUnsolvedEquation);
	}

	private static GeoElementND getNext(final GeoElementND geo) {
		return geo.getConstruction().getNext(geo,
				SuggestionSolveForSymbolic::isUnsolvedEquation);
	}

	private static boolean isUnsolvedEquation(GeoElementND var) {
		return isAlgebraEquation(var)
				&& !SuggestionSolve.checkDependentAlgo(var, SINGLE_SOLVE, null);
	}
}