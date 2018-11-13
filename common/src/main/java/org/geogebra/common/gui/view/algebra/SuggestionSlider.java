package org.geogebra.common.gui.view.algebra;

import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.kernel.algos.GetCommand;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.main.Localization;
import org.geogebra.common.util.debug.Log;

/**
 * Suggestion class for sliders.
 * 
 * @author laszlo
 *
 */
public class SuggestionSlider extends Suggestion {

	@Override
	public String getCommand(Localization loc) {
		return loc.getMenu("Suggestion.CreateSlider");
	}

	@Override
	protected void runCommands(GeoElementND geo) {
		Log.debug("creating slider");
	}

	/**
	 * 
	 * @return instance
	 */
	public static Suggestion get() {
		return new SuggestionSlider();
	}

	@Override
	public boolean isAutoSlider() {
		return true;
	}

	@Override
	public int getMode() {
		return EuclidianConstants.MODE_SLIDER;
	}

	@Override
	protected boolean allAlgosExist(GetCommand className, GeoElement[] input,
			boolean[] algosFound) {
		return false;
	}

}
