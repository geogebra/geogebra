package org.geogebra.web.html5.gui.laf;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.main.AppConfig;
import org.geogebra.common.main.GeoGebraColorConstants;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.html5.gui.FastClickHandler;
import org.geogebra.web.html5.gui.GeoGebraFrameW;
import org.geogebra.web.html5.gui.view.button.StandardButton;
import org.geogebra.web.html5.gui.zoompanel.FullScreenHandler;
import org.geogebra.web.html5.gui.zoompanel.MebisFullscreenHandler;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.Window.Location;

/**
 * Mebis specific settings
 */
public class MebisSettings implements VendorSettings {

	private static final String MEBIS_LICENSE_PATH = "/static/license.html?";

	private ViewPreferences viewPreferences;

	/**
	 * Mebis specific settings
	 */
	public MebisSettings() {
		viewPreferences = new ViewPreferences();
		viewPreferences.setMobileFullScreenButtonEnabled(true);
	}

	@Override
	public String getLicenseURL() {
		if (!Location.getProtocol().startsWith("http")) {
			return "https://tafel.mebis.bayern.de" + MEBIS_LICENSE_PATH;
		}
		return MEBIS_LICENSE_PATH;
	}

	@Override
	public String getAppTitle(AppConfig config) {
		return "Tafel";
	}

	@Override
	public ViewPreferences getViewPreferences() {
		return viewPreferences;
	}

	@Override
	public FullScreenHandler getFullscreenHandler() {
		return new MebisFullscreenHandler();
	}

	@Override
	public String getVideoAccessErrorKey() {
		return "MebisAccessError";
	}

	@Override
	public String getUnsupportedBrowserErrorKey() {
		return "mow.unsupportedBrowserMessage";
	}

	@Override
	public GColor getPrimaryColor() {
		return GeoGebraColorConstants.MEBIS_ACCENT;
	}

	@Override
	public void attachMainMenu(final AppW app, FastClickHandler handler) {
		StandardButton openMenuButton = new StandardButton(
				MaterialDesignResources.INSTANCE.menu_black_whiteBorder(), null,
				24, app);

		final GeoGebraFrameW frame = app.getAppletFrame();

		openMenuButton.addFastClickHandler(handler);

		openMenuButton.addDomHandler(new KeyUpHandler() {
			@Override
			public void onKeyUp(KeyUpEvent event) {
				if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
					app.toggleMenu();
				}
			}
		}, KeyUpEvent.getType());

		openMenuButton.addStyleName("mowOpenMenuButton");
		frame.add(openMenuButton);	}
}
