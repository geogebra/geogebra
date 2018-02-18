package org.geogebra.web.full.gui.menubar;

import org.geogebra.web.html5.main.AppW;

/**
 * Help menu
 */
public class DownloadMenuW extends GMenuBar implements MenuBarI {
	/**
	 * @param app
	 *            application
	 */
	public DownloadMenuW(final AppW app) {
		super("DownloadAs", app);
		if (app.isUnbundledOrWhiteboard()) {
			addStyleName("matStackPanel");
		} else {
			addStyleName("GeoGebraMenuBar");
		}
		ExportMenuW.initActions(this, app);
	}

	@Override
	public void hide() {
		// no hiding needed
	}
}

