package org.geogebra.common.gui.view.algebra;

import org.geogebra.common.kernel.arithmetic.PolyFunction;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.main.Localization;

public class SuggestionRootExtremum extends Suggestion {
	
	public SuggestionRootExtremum(String... labels) {
		super(labels);
	}

	@Override
	public String getCommand(Localization loc) {
		return loc.getCommand("Suggestion.SpecialPoints");
	}
	
	@Override
	public void execute(GeoElementND geo) {
		PolyFunction poly = ((GeoFunction) geo).getFunction()
				.expandToPolyFunction(
				((GeoFunction) geo).getFunctionExpression(), false, true);
		if (poly == null || poly.getDegree() > 0) {
			geo.getKernel().getAlgebraProcessor()
				.processAlgebraCommand("Root[" + getLabels(geo) + "]", true);
		}
		if (poly == null || poly.getDegree() > 1) {
			geo.getKernel().getAlgebraProcessor().processAlgebraCommand(
				"Extremum[" + getLabels(geo) + "]", true);
		} else {
			geo.getKernel().getAlgebraProcessor().processAlgebraCommand(
					"Intersect[" + getLabels(geo) + ",yAxis]", true);
		}
	}

	public static Suggestion get(GeoElement geo) {
		if (geo instanceof GeoFunction
				&& !hasDependentAlgo(geo, Commands.Root, Commands.Extremum)) {
			GeoFunction geoFun = (GeoFunction) geo;
			if (!geoFun.isBooleanFunction()) {
				return new SuggestionRootExtremum(geo.getLabelSimple());
			}
		}
		return null;
	}
}
