package org.geogebra.common.gui.view.algebra;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.geogebra.common.kernel.algos.Algos;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.arithmetic.Inspecting;
import org.geogebra.common.kernel.arithmetic.SymbolicMode;
import org.geogebra.common.kernel.geos.GeoDummyVariable;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoSymbolic;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.util.debug.Log;

public class SuggestionSolveForSymbolic extends SuggestionSolve {

	private final List<GeoElementND> geos;
	private final String[] vars;
	public SuggestionSolveForSymbolic(List<GeoElementND> geos, String[] vars) {
		super();
		this.geos = geos;
		this.vars = vars;
	}

	@Override
	protected void runCommands(GeoElementND geo) {
		String command = getCommandText(geo);
		Log.debug("!!! SolveCommand: " + command);
		geo.getKernel().getAlgebraProcessor().processAlgebraCommand(
				command, false);
	}

	private String getCommandText(GeoElementND geo) {
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
		if (vars.length == 0) {
			return "";
		}
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		int lastIdx = vars.length - 1;
		for (int i=0; i < lastIdx;i++) {
			sb.append(vars[i]);
			sb.append(", ");
		}
		sb.append(vars[lastIdx]);
		sb.append("}");
		return sb.toString();
	}

	private String getGeoList() {
		if (geos.size() == 0) {
			return "";
		}
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		int lastIdx = geos.size() - 1;
		for (int i=0; i < lastIdx;i++) {
			sb.append(geos.get(0).getLabelSimple());
			sb.append(", ");
		}
		sb.append(geos.get(lastIdx).getLabelSimple());
		sb.append("}");
		return sb.toString();
	}

	public static boolean isValid(GeoElementND geo) {
		return geo instanceof GeoSymbolic;
	}

	public static Suggestion get(GeoElement geo) {
		if (!isValid(geo)) {
			return null;
		}

		GeoSymbolic symbolic = (GeoSymbolic)geo;
		String[] vars = getVariables(symbolic);
		if (isAlgebraEquation(symbolic)) {
			if (vars.length == 1) {
				return SINGLE_SOLVE;
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

		HashSet<GeoElement> varSet = ((GeoSymbolic)geo).getValue().getVariables(SymbolicMode.SYMBOLIC);
		List<String> varStrings = new ArrayList<>();
		if (varSet != null) {
			for (GeoElement var : varSet) {
				String varName = (var instanceof GeoDummyVariable) ? ((GeoDummyVariable)var).getVarName()
						: var.getLabelSimple();
				varStrings.add(varName);
			}
		}
		return varStrings.toArray(new String[0]);
	}

	private static boolean isAlgebraEquation(GeoElementND geo) {
		return  (geo.getParentAlgorithm() == null
				|| geo.getParentAlgorithm().getClassName() == Algos.Expression);
	}


	private static Suggestion getMulti(GeoElement geo) {
		String[] vars = getVariables((GeoSymbolic)geo);
		List<GeoElementND> geos = new ArrayList<>();
		geos.add(geo);
		GeoElementND prev = getPrevious(geo);
		while (prev != null) {
			geos.add(prev);
			prev  = isValid(prev) ? getPrevious(prev)
					:null;
		}
		return new SuggestionSolveForSymbolic(geos, vars);
	}

	private static GeoElementND getPrevious(GeoElementND geo) {
		final String[] vars = getVariables(geo);
		final GeoElementND prev = geo.getConstruction().getPrevious(geo,
				new Inspecting() {

					@Override
					public boolean check(ExpressionValue var) {
						return isAlgebraEquation((GeoElement) var)
								&& subset(getVariables((GeoSymbolic)var), vars)
								&& !SuggestionSolve.checkDependentAlgo((GeoElement) var,
								SINGLE_SOLVE, null);
					}
				});
		return prev;
	}
}
