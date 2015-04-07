package org.geogebra.web.web.gui;

import org.geogebra.common.main.Localization;

public class LanguageHeaderPanel extends AuxiliaryHeaderPanel {

	LanguageHeaderPanel(Localization loc, MyHeaderPanel gui) {
		super(loc, gui);
		setLabels();
	}

	@Override
	public void setLabels() {
		this.setText(loc.getMenu("Language"));
	}

}
