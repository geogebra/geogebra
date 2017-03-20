package org.geogebra.web.web.gui.color;

import org.geogebra.common.main.GeoGebraColorConstants;
import org.geogebra.web.html5.main.AppW;

/**
 * Color chooser button for MOW.
 * 
 * @author Laszlo Gal
 *
 */
public class MOWColorButton extends ColorPopupMenuButton {

	/**
	 * @param app
	 *            GGB application.
	 */
	public MOWColorButton(AppW app) {
		super(app, 0, true);
		getMyPopup().addStyleName("mowColorPopup");
	}

	@Override
	protected void setColors() {
		setColorSet(GeoGebraColorConstants.getMOWPopupArray());
	}

	@Override
	protected String getSliderPostfix() {
		return " %";
	}
}
