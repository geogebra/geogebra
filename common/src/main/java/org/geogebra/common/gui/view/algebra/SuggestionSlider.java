package org.geogebra.common.gui.view.algebra;

import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.main.Localization;
import org.geogebra.common.util.debug.Log;

public class SuggestionSlider extends Suggestion {

	@Override
	public String getCommand(Localization loc) {
		return loc.getCommand("Slider");
	}

	@Override
	public void execute(GeoElementND geo) {
		Log.debug("creating slider");
	}

	public static Suggestion get() {
		return new SuggestionSlider();
	}

	public boolean isAutoSlider() {
		return true;
	}

}
