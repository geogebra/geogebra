package org.geogebra.web.web.gui.menubar;

import org.geogebra.common.plugin.EventType;
import org.geogebra.web.html5.main.AppW;

/**
 * Help menu
 */
public class DownloadMenuW extends GMenuBar implements MenuBarI {
	/**
	 * app
	 */
	AppW app;

	/**
	 * @param app
	 *            - application
	 */
	public DownloadMenuW(final AppW app) {
		super(true, "DownloadAs", app);
		this.app = app;
		if (app.isUnbundledOrWhiteboard()) {
			addStyleName("matStackPanel");
		} else {
			addStyleName("GeoGebraMenuBar");
		}
		ExportMenuW.initActions(this, app);
	}



	/**
	 * Fire dialog open event
	 * 
	 * @param string
	 *            dialog name
	 */
	protected void dialogEvent(String string) {
		app.dispatchEvent(new org.geogebra.common.plugin.Event(
				EventType.OPEN_DIALOG, null, string));
	}

	public void hide() {
		// no hiding needed

	}
}

