package org.geogebra.web.full.gui.menubar.action;

import org.geogebra.common.GeoGebraConstants;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.full.gui.view.algebra.MenuAction;
import org.geogebra.web.full.main.AppWFull;
import org.geogebra.web.html5.main.AppW;

/**
 * Clears construction and initializes a new one
 *
 */
public class LicenseAction extends MenuAction<Void> {
	/**
	 * Settings for version/about window
	 */
	protected static final String ABOUT_WINDOW_PARAMS = "width=720,height=600,"
			+ "scrollbars=yes,toolbar=no,location=no,directories=no,"
			+ "menubar=no,status=no,copyhistory=no";
	private AppW app;

	/**
	 * @param app
	 *            application
	 */
	public LicenseAction(AppW app) {
		super("AboutLicense",
				MaterialDesignResources.INSTANCE.info_black());
		this.app = app;
	}

	@Override
	public void execute(Void geo, AppWFull appW) {
		appW.getFileManager()
				.open(app.getLAF().getLicenseURL() + "&version="
						+ app.getVersionString() + "&date="
						+ GeoGebraConstants.BUILD_DATE, ABOUT_WINDOW_PARAMS);
	}
}
