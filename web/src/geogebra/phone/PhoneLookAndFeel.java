package geogebra.phone;

import geogebra.common.move.ggtapi.models.Material;
import geogebra.html5.main.AppW;
import geogebra.phone.gui.view.material.browser.MaterialListElementP;
import geogebra.web.gui.browser.MaterialListElement;
import geogebra.web.gui.laf.GLookAndFeel;

public class PhoneLookAndFeel extends GLookAndFeel {

	@Override
	public MaterialListElement getMaterialElement(Material mat, AppW app,
	        boolean isLocal) {
		return new MaterialListElementP(mat, app, isLocal);
	}

	@Override
	public void addWindowClosingHandler(AppW app) {
		// no close message for phones
	}

	@Override
	public void removeWindowClosingHandler() {
		// no close message for phones
	}

	@Override
	public boolean exportSupported() {
		return false;
	}

	@Override
	public boolean externalDriveSupported() {
		return false;
	}
}
