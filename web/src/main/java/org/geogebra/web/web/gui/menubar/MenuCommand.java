package org.geogebra.web.web.gui.menubar;

import org.geogebra.web.html5.main.AppW;

import com.google.gwt.user.client.Command;


public class MenuCommand implements Command {

	private AppW app;
	
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
	    // TODO Auto-generated method stub
	    
    }

}
