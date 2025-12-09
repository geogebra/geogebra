/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.web.full.gui.menubar;

import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.main.LocalizationW;
import org.geogebra.web.resources.SVGResource;

/**
 * Menu for account related items (logout)
 */
public class UserSubmenu extends Submenu {

	/**
	 * @param app
	 *            application
	 */
	public UserSubmenu(AppW app) {
		super("user", app);
		addItem(MainMenu.getMenuBarItem(MaterialDesignResources.INSTANCE.signout_black(),
				app.getLocalization().getMenu("SignOut"), new MenuCommand(app) {

					@Override
					public void doExecute() {
						app.getLoginOperation().showLogoutUI();
						app.getLoginOperation().performLogOut();
					}
				}));
		if (app.isUnbundledOrWhiteboard()) {
			addStyleName("matStackPanel");
		} else {
			addStyleName("GeoGebraMenuBar");
		}
	}

	@Override
	public SVGResource getImage() {
		return MaterialDesignResources.INSTANCE.person_black();
	}

	@Override
	public String getTitle(LocalizationW localization) {
		if (getApp().getLoginOperation() == null) {
			return "";
		}
		return getApp().getLoginOperation().getUserName();
	}

	@Override
	protected String getTitleTranslationKey() {
		return ""; // not needed, override getTitle instead
	}

}
