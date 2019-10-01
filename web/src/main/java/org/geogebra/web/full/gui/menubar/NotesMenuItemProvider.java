package org.geogebra.web.full.gui.menubar;

import java.util.ArrayList;

import org.geogebra.web.html5.main.AppW;

/**
 * Menu items for notes
 */
public class NotesMenuItemProvider implements MainMenuItemProvider {

	private AppW app;

	/**
	 * @param app
	 *            application
	 */
	public NotesMenuItemProvider(AppW app) {
		this.app = app;
	}

	@Override
	public void addMenus(ArrayList<Submenu> menus) {
		menus.add(new FileMenuW(app));
		menus.add(new DownloadMenuW(app));
        menus.add(new AppsSubmenu(app));
		menus.add(new SettingsMenu(app));
        menus.add(new HelpMenuW(app));
	}

	@Override
	public boolean hasSigninMenu() {
		return app.getLoginOperation() == null || app.getLAF().hasLoginButton();
	}

}
