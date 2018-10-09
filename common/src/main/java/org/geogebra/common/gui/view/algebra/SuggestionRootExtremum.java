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
		if (!(geo instanceof GeoFunction)
				|| (((GeoFunction) geo).getFunction() == null)) {
			return;
		}

		PolyFunction poly = ((GeoFunction) geo).getFunction()
				.expandToPolyFunction(
				((GeoFunction) geo).getFunctionExpression(), false, true);
		if (poly == null || poly.getDegree() > 0) {
			geo.getKernel().getAlgebraProcessor()
					.processAlgebraCommand("Root[" + geo.getLabelSimple() + "]",
							false);
		}
		if (poly == null || poly.getDegree() > 1) {
			geo.getKernel().getAlgebraProcessor().processAlgebraCommand(
					"Extremum[" + geo.getLabelSimple() + "]", false);
		}
		geo.getKernel().getAlgebraProcessor().processAlgebraCommand(
					"Intersect[" + geo.getLabelSimple() + ","
							+ geo.getKernel().getLocalization().getMenu("yAxis")
							+ "]",
					false);
	}

	/**
	 * @param geo
	 *            construction element
	 * @return solve suggestion if applicable
	 */
	public static Suggestion get(GeoElement geo) {
		if (geo instanceof GeoFunction
				&& !hasDependentAlgo(geo, INSTANCE)) {
			GeoFunction geoFun = (GeoFunction) geo;
			if (!geoFun.isBooleanFunction()) {
				return INSTANCE;
			}
		}
		return null;
	}

	@Override
	protected boolean sameAlgoType(GetCommand className, GeoElement[] input) {
		// TODO Auto-generated method stub
		return className == Commands.Roots || className == Commands.Extremum
				|| (className == Commands.Intersect
						&& input[1] instanceof GeoAxis);
	}
}
