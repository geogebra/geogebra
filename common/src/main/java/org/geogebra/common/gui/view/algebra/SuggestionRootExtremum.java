package org.geogebra.common.gui.view.algebra;

import org.geogebra.common.kernel.algos.GetCommand;
import org.geogebra.common.kernel.arithmetic.Equation;
import org.geogebra.common.kernel.arithmetic.Function;
import org.geogebra.common.kernel.arithmetic.PolyFunction;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoAxis;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunctionable;
import org.geogebra.common.kernel.geos.GeoLine;
import org.geogebra.common.kernel.geos.GeoSymbolic;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.main.Localization;
import org.geogebra.common.util.DoubleUtil;

/**
 * @author Mathieu
 *
 */
public class SuggestionRootExtremum extends Suggestion {

	private static Suggestion INSTANCE = new SuggestionRootExtremum();

	@Override
	public String getCommand(Localization loc) {
		return loc.getMenu("Suggestion.SpecialPoints");
	}

	@Override
	public void runCommands(GeoElementND geo) {
		// reported typecast error
		// reported NPE
		boolean[] neededAlgos = getNeededAlgos(geo);
		checkDependentAlgo(geo, INSTANCE, neededAlgos);
		if (neededAlgos[0]) {
			geo.getKernel().getAlgebraProcessor()
					.processAlgebraCommand("Root[" + geo.getLabelSimple() + "]",
							false);
		}
		if (neededAlgos[1]) {
			geo.getKernel().getAlgebraProcessor().processAlgebraCommand(
					"Extremum[" + geo.getLabelSimple() + "]", false);
		}
		if (neededAlgos[2]) {
			geo.getKernel().getAlgebraProcessor().processAlgebraCommand(
					"Intersect[" + geo.getLabelSimple() + ","
							+ geo.getKernel().getLocalization().getMenu("yAxis")
							+ "]",
					false);
		}
	}

	private static boolean[] getNeededAlgos(GeoElementND geo) {
		// intersection with y needed always
		boolean[] algosMissing = { false, false, true };
		if (!(geo instanceof GeoFunctionable)) {
			return algosMissing;
		}
		Function function = ((GeoFunctionable) geo).getFunction();
		if (function == null) {
			return algosMissing;
		}

		PolyFunction poly = function.expandToPolyFunction(
				function.getFunctionExpression(), false, true);
		if (poly == null || poly.getDegree() > 0) {
			algosMissing[0] = true;
		}
		if (poly == null || poly.getDegree() > 1) {
			algosMissing[1] = true;
		}

		if (isVerticalLine(geo)) {
			algosMissing[2] = false;
		}
		return algosMissing;
	}

	private static boolean isVerticalLine(GeoElementND geo) {
		return Equation.isAlgebraEquation(geo) && geo instanceof GeoLine
				&& DoubleUtil.isZero(((GeoLine) geo).getY());
	}

	/**
	 * @param geo
	 *            construction element
	 * @return solve suggestion if applicable
	 */
	public static Suggestion get(GeoElement geo) {
		if (mayHaveSpecialPoints(geo)
				&& !checkDependentAlgo(geo, INSTANCE, getNeededAlgos(geo))) {
			return INSTANCE;
		}
		return null;
	}

	/**
	 * @param geo
	 *            element
	 * @return whether the element can be seen as a function (is a function,
	 *         line or conic)
	 */
	private static boolean mayHaveSpecialPoints(GeoElement geo) {
		return geo.isRealValuedFunction() && !geo.isNumberValue()
				&& !(geo instanceof GeoSymbolic);
	}

	@Override
	protected boolean allAlgosExist(GetCommand className, GeoElement[] input,
			boolean[] algosMissing) {
		if (className == Commands.Roots || className == Commands.Root) {
			algosMissing[0] = false;
		}
		if (className == Commands.Extremum) {
			algosMissing[1] = false;
		}
		if (className == Commands.Intersect && input[1] instanceof GeoAxis) {
			algosMissing[2] = false;
		}
		return !algosMissing[0] && !algosMissing[1] && !algosMissing[2];
	}
}
