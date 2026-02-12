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

import java.util.ArrayList;

import org.geogebra.common.ownership.GlobalScope;
import org.geogebra.web.html5.main.AppW;

/**
 * Menu item provider for Classic
 */
public class ClassicMenuItemProvider {

	private AppW app;

	/**
	 * @param app
	 *            application
	 */
	public ClassicMenuItemProvider(AppW app) {
		this.app = app;
	}

	/**
	 * @param menus list of menus
	 */
	public void addMenus(ArrayList<Submenu> menus) {
		boolean exam = GlobalScope.isExamActive(app);
		if (app.enableFileFeatures() && !app.isLockedExam()) {
			menus.add(new FileMenuW(app));
		}
		menus.add(new EditMenuW(app));
		menus.add(new PerspectivesMenuW(app));
		menus.add(new ViewMenuW(app));
		menus.add(new SettingsMenu(app));
		if (!app.getLAF().isSmart()) {
			menus.add(new ToolsMenuW(app));
		}
		if (!exam) {
			// exam -> assume offline, no help
			menus.add(new HelpMenuW(app));
		}
	}
}
