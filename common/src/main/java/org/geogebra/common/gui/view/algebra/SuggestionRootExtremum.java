package org.geogebra.common.gui.view.algebra;

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
		geo.getKernel().getAlgebraProcessor().processAlgebraCommand(
				"Root[" + getLabels(geo) + "]", true);
		geo.getKernel().getAlgebraProcessor().processAlgebraCommand(
				"Extremum[" + getLabels(geo) + "]", true);
	}
}
