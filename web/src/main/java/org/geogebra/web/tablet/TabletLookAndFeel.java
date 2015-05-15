package org.geogebra.web.tablet;

import org.geogebra.common.main.App;
import org.geogebra.common.move.ggtapi.models.Material;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.tablet.gui.browser.TabletMaterialElement;
import org.geogebra.web.web.gui.browser.MaterialListElement;
import org.geogebra.web.web.gui.browser.SignInButton;
import org.geogebra.web.web.gui.browser.TabletSignInButton;
import org.geogebra.web.web.gui.laf.GLookAndFeel;

public class TabletLookAndFeel extends GLookAndFeel {

	public static final int PROVIDER_PANEL_WIDTH = 0;

	@Override
	public void addWindowClosingHandler(AppW app) {
		// no close message on tablet
	}

	@Override
	public void removeWindowClosingHandler() {
		// no close message on tablet
	}

	@Override
	public String getType() {
		return "tablet";
	}

	@Override
	public boolean isTablet() {
		return true;
	}

	@Override
	public boolean copyToClipboardSupported() {
		return true;
	}

	@Override
	public SignInButton getSignInButton(final App app) {
		return new TabletSignInButton(app);
	}

	@Override
	public MaterialListElement getMaterialElement(final Material m,
	        final AppW app, boolean isLocal) {
		return new TabletMaterialElement(m, app, isLocal);
	}

	@Override
	public boolean exportSupported() {
		return false;
	}

	@Override
	public boolean externalDriveSupported() {
		return false;
	}

	@Override
	public boolean supportsGoogleDrive() {
		return false;
	}

	/**
	 * @return style name for app frame
	 */
	public String getFrameStyleName() {
		return "Tablet";
	}

}
