package org.geogebra.web.full.gui.menubar;

import org.geogebra.web.html5.main.AppW;

import com.google.gwt.user.client.Command;

/**
 * Command that also closes the menu
 */
public class MenuCommand implements Command {
	/** application */
	protected AppW app;
	
	/**
	 * @param app
	 *            application
	 */
	public MenuCommand(AppW app) {
		this.app = app;
	}
 
	@Override
	public void execute() {
		app.toggleMenu();
		this.doExecute();
	}

	/**
	 * code that is executed if the menuEntry was clicked
	 */
	protected void doExecute() {
		// this may be not needed if execute is overridden
	}

}
