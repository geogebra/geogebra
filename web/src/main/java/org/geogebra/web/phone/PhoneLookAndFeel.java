package org.geogebra.web.phone;

import org.geogebra.common.move.ggtapi.models.Material;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.phone.gui.view.material.browser.MaterialListElementP;
import org.geogebra.web.web.gui.browser.MaterialListElement;
import org.geogebra.web.web.gui.laf.GLookAndFeel;

public class PhoneLookAndFeel extends GLookAndFeel {

	/** Height of the header */
	public static final int PHONE_HEADER_HEIGHT = 43;

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
