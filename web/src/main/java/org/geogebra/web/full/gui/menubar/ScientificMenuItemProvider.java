package org.geogebra.web.full.gui.menubar;

import java.util.ArrayList;

import org.geogebra.web.html5.main.AppW;

/**
 * Menu item provider for Scientific Calc
 */
public class ScientificMenuItemProvider implements MainMenuItemProvider {

	private AppW app;

	/**
	 * @param app
	 *            application
	 */
	public ScientificMenuItemProvider(AppW app) {
		this.app = app;
	}

	@Override
	public void addMenus(ArrayList<Submenu> menus) {
		menus.add(new ClearAllMenuitem(app));
		menus.add(new AppsSubmenu(app));
		menus.add(new SettingsMenu(app));
		menus.add(new HelpMenuW(app));
	}

}
