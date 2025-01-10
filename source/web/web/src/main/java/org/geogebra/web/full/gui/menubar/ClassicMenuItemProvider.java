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
		boolean exam = !GlobalScope.examController.isIdle();
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
