package org.geogebra.web.full.gui.menubar;

import java.util.ArrayList;

import org.geogebra.web.html5.main.AppW;

/**
 * Menu item provider for Classic
 */
public class ClassicMenuItemProvider implements MainMenuItemProvider {

	private AppW app;

	/**
	 * @param app
	 *            application
	 */
	public ClassicMenuItemProvider(AppW app) {
		this.app = app;
	}

	@Override
	public void addMenus(ArrayList<Submenu> menus) {
		boolean exam = app.isExam();
		if (!exam) {
			if (app.enableFileFeatures()) {
				menus.add(new FileMenuW(app));
			}
			menus.add(new EditMenuW(app));
			menus.add(new PerspectivesMenuW(app));
			menus.add(new ViewMenuW(app));
			menus.add(new SettingsMenu(app));
			if (!app.getLAF().isSmart()) {
				menus.add(new ToolsMenuW(app));
			}
			menus.add(new HelpMenuW(app));
		} else {
			menus.add(new FileMenuW(app));
			menus.add(new SettingsMenu(app));
		}

	}

}
