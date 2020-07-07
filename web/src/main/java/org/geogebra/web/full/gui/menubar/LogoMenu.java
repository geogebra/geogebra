package org.geogebra.web.full.gui.menubar;

import org.geogebra.web.full.main.AppWFull;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.resources.SVGResource;

/**
 * Top of main menu with a logo; technically not a submenu
 *
 */
public class LogoMenu extends Submenu {

	/**
	 * @param app
	 *            application
	 */
	public LogoMenu(AppW app) {
		super("", app);
		setStyleName("logoMenu");
	}

	@Override
	public SVGResource getImage() {
		return ((AppWFull) getApp()).getActivity().getIcon();
	}

	@Override
	protected String getTitleTranslationKey() {
		return getApp().getConfig().getAppNameShort();
	}

}
