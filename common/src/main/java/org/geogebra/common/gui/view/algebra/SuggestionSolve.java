package org.geogebra.common.gui.view.algebra;

import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.main.Localization;

public class SuggestionSolve extends Suggestion {
	
	public SuggestionSolve(String... labels) {
		super(labels);
	}

	@Override
	public String getCommand(Localization loc) {
		return loc.getCommand("Solve");
	}
	
	@Override
	public void execute(GeoElementND geo) {
		geo.getKernel().getAlgebraProcessor().processAlgebraCommand(
				"Solve[" + getLabels(geo) + "]", true);
	}
}
