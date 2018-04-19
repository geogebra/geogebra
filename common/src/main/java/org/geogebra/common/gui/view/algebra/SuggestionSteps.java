package org.geogebra.common.gui.view.algebra;

import org.geogebra.common.kernel.algos.GetCommand;
import org.geogebra.common.kernel.cas.HasSteps;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.main.Localization;

public class SuggestionSteps extends Suggestion {
	
	private static Suggestion INSTANCE = new SuggestionSteps();

	@Override
	public String getCommand(Localization loc) {
		return "Show Steps";
	}


	@Override
	public void execute(GeoElementND geo) {
		StepGuiBuilder builder = geo.getKernel().getApplication()
				.getGuiManager().getStepGuiBuilder();
		((HasSteps) geo.getParentAlgorithm()).getSteps(builder);
	}

	public static Suggestion get(GeoElement geo) {
		if (geo.getParentAlgorithm() instanceof HasSteps &&
				((HasSteps) geo.getParentAlgorithm()).canShowSteps()) {
				return INSTANCE;

		}
		return null;
	}

	@Override
	protected boolean sameAlgoType(GetCommand className, GeoElement[] input) {
		return false;
	}
}
