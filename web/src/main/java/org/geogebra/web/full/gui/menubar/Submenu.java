package org.geogebra.web.full.gui.menubar;

import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.main.LocalizationW;

import com.google.gwt.resources.client.ResourcePrototype;

/**
 * Menu that can be embedded in higher-level menu
 * 
 * @author Zbynek
 *
 * @param <T>
 *            image type (SVG or PNG)
 */
public abstract class Submenu<T extends ResourcePrototype> extends GMenuBar {

	/**
	 * @param menuTitle
	 *            internal title (for event logging)
	 * @param app
	 *            application
	 */
	public Submenu(String menuTitle, AppW app) {
		super(menuTitle, app);
	}

	/**
	 * @return icon
	 */
	public abstract T getImage();

	/**
	 * @param localization
	 *            localization
	 * @return localized title
	 */
	public String getTitle(LocalizationW localization) {
		return localization.getMenu(getTitleTranslationKey());
	}

	/**
	 * @return translation key
	 */
	protected abstract String getTitleTranslationKey();

}
