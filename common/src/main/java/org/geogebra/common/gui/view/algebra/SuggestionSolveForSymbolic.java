package org.geogebra.common.gui.view.algebra;

import static org.geogebra.common.gui.view.algebra.SuggestionSolve.SINGLE_SOLVE;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.geogebra.common.kernel.algos.Algos;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.arithmetic.Inspecting;
import org.geogebra.common.kernel.arithmetic.SymbolicMode;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoSymbolic;
import org.geogebra.common.kernel.kernelND.GeoElementND;

public class SuggestionSolveForSymbolic {

	public static boolean isValid(GeoElementND geo) {
		return geo instanceof GeoSymbolic;
	}

	public static Suggestion get(GeoElement geo) {
		GeoSymbolic symbolic = (GeoSymbolic)geo;
		String[] vars = getVariables(symbolic);
		if (isAlgebraEquation(symbolic)) {
			if (vars.length == 1) {
				return SINGLE_SOLVE;
			} else {
				return getMulti(symbolic, vars);
			}
		}

		return null;

	}

	private static String[] getVariables(GeoSymbolic geo) {
		HashSet<GeoElement> varSet = geo.getValue().getVariables(SymbolicMode.SYMBOLIC);
		List<String> varStrings = new ArrayList<>();
		if (varSet != null) {
			for (GeoElement geo0 : varSet) {
				varStrings.add(geo0.getLabelSimple());
			}
		}
		return varStrings.toArray(new String[0]);
	}

	private static boolean isAlgebraEquation(GeoElementND geo) {
		return  (geo.getParentAlgorithm() == null
				|| geo.getParentAlgorithm().getClassName() == Algos.Expression);
	}


	private static Suggestion getMulti(GeoElement geo, final String[] vars) {
		List<String> labels = new ArrayList<>();
		GeoElementND prev = getPrevious(geo, vars);
		while (prev != null) {
			labels.add(prev.getLabelSimple());
			prev  = isValid(prev) ? getPrevious(prev, getVariables((GeoSymbolic) prev))
					:null;
		}
		return new SuggestionSolve(labels.toArray(new String[0]));
	}

	private static GeoElementND getPrevious(GeoElementND geo, final String[] vars) {
		org.geogebra.common.util.debug.Log.debug(vars);
		GeoElementND prev = geo.getConstruction().getPrevious(geo,
				new Inspecting() {

					@Override
					public boolean check(ExpressionValue var) {
						return isAlgebraEquation((GeoElement) var)
//                                && SuggestionSolve.subset((getVariables((GeoSymbolic) var)), vars)
								&& !SuggestionSolve.checkDependentAlgo((GeoElement) var,
								SINGLE_SOLVE, null);
					}
				});
		return prev;
	}
}
