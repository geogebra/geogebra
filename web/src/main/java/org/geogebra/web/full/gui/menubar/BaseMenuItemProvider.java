package org.geogebra.web.full.gui.menubar;

import java.util.ArrayList;

import org.geogebra.web.html5.main.AppW;

/**
 * Menu item provider for unbundled apps
 */
public class BaseMenuItemProvider implements MainMenuItemProvider {

	private AppW app;

	/**
	 * @param app
	 *            application
	 */
	public BaseMenuItemProvider(AppW app) {
		this.app = app;
	}

	@Override
	public void addMenus(ArrayList<Submenu> menus) {
		boolean exam = app.isExam();
		if (!exam) {
			if (app.enableFileFeatures()) {
				menus.add(new FileMenuW(app));
			}
			menus.add(new DownloadMenuW(app));
			menus.add(new AppsSubmenu(app));
			menus.add(new SettingsMenu(app));
			menus.add(new HelpMenuW(app));
		} else {
			menus.add(new FileMenuW(app));
			menus.add(new SettingsMenu(app));
		}

	}

}
