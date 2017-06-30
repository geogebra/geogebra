package org.geogebra.common.gui.view.algebra;

import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.main.Localization;

public class SuggestionSlider extends Suggestion {
	public SuggestionSlider() {
	}

	@Override
	public String getCommand(Localization loc) {
		return loc.getCommand("Slider");
	}

	@Override
	public void execute(GeoElementND geo) {
	}

	public static Suggestion get() {
		return new SuggestionSlider();
	}

}
