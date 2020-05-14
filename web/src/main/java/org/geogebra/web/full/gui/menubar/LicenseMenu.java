package org.geogebra.web.full.gui.menubar;

import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.full.gui.menubar.action.ShowLicenseAction;
import org.geogebra.web.full.main.AppWFull;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.resources.SVGResource;

/**
 * Single item menu for license
 */
public class LicenseMenu extends Submenu {

	/**
	 * @param app
	 *            application
	 */
	public LicenseMenu(AppW app) {
		super("license", app);
	}

	@Override
	public SVGResource getImage() {
		return MaterialDesignResources.INSTANCE.info_black();
	}

	@Override
	protected String getTitleTranslationKey() {
		return "AboutLicense";
	}

	@Override
	public void handleHeaderClick() {
		new ShowLicenseAction().execute(null, (AppWFull) getApp());
	}

}
