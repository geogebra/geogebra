package org.geogebra.web.full.gui.menubar;

import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.full.main.AppWFull;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.resources.SVGResource;

/**
 * Single item submenu with settings action
 */
public class SettingsMenu extends Submenu {

	/**
	 * @param app
	 *            application
	 */
	public SettingsMenu(AppW app) {
		super("settings", app);
	}

	@Override
	public SVGResource getImage() {
		return MaterialDesignResources.INSTANCE.gear();
	}

	@Override
	protected String getTitleTranslationKey() {
		return "Settings";
	}

	@Override
	public void handleHeaderClick() {
		((AppWFull) getApp()).getCurrentActivity().showSettingsView(getApp());
	}
}
