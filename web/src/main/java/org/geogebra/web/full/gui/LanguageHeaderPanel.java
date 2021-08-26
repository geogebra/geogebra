package org.geogebra.web.full.gui;

import org.geogebra.common.main.Localization;

/**
 * Language selection panel
 *
 */
public class LanguageHeaderPanel extends AuxiliaryHeaderPanel {

	/**
	 * @param loc
	 *            localization
	 * @param gui
	 *            app frame
	 */
	LanguageHeaderPanel(Localization loc, MyHeaderPanel gui) {
		super(loc, gui);
		setLabels();
	}

	@Override
	public void setLabels() {
		this.setText(loc.getMenu("Language"));
	}

}
