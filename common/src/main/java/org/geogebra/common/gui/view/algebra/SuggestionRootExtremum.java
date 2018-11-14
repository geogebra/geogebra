package org.geogebra.common.gui.view.algebra;

import org.geogebra.common.kernel.algos.GetCommand;
import org.geogebra.common.kernel.arithmetic.PolyFunction;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoAxis;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.main.Localization;

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
		if(neededAlgos[2]) {
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
		if (!(geo instanceof GeoFunction)
				|| (((GeoFunction) geo).getFunction() == null)) {
			return algosMissing;
		}

		PolyFunction poly = ((GeoFunction) geo).getFunction()
				.expandToPolyFunction(
						((GeoFunction) geo).getFunctionExpression(), false,
						true);
		if (poly == null || poly.getDegree() > 0) {
			algosMissing[0] = true;
		}
		if (poly == null || poly.getDegree() > 1) {
			algosMissing[1] = true;
		}
		return algosMissing;
	}

	/**
	 * @param geo
	 *            construction element
	 * @return solve suggestion if applicable
	 */
	public static Suggestion get(GeoElement geo) {
		if (geo instanceof GeoFunction
				&& !checkDependentAlgo(geo, INSTANCE, getNeededAlgos(geo))) {
			GeoFunction geoFun = (GeoFunction) geo;
			if (!geoFun.isBooleanFunction()) {
				return INSTANCE;
			}
		}
		return null;
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
