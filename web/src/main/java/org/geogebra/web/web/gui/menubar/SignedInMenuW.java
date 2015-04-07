package org.geogebra.web.web.gui.menubar;

import org.geogebra.common.move.ggtapi.operations.LogInOperation;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;

/**
 * @author gabor
 *  Creates a menu for signed in state
 */
public class SignedInMenuW extends MenuBar {
	
	/**
	 * Appw instance
	 */
	AppW app;
	private MenuItem logOut;
	private MenuItem profile;

	/**
	 * @param app
	 * Creates a new signedIn menu
	 */
	SignedInMenuW(AppW app) {
		super(true);
		this.app = app;
		addStyleName("GeoGebraMenuBar");
		initActions();
	}

	private void initActions() {
		// this method should only be called from
		// AppWapplication or AppWapplet
		String userName = ((LogInOperation)app.getLoginOperation()).getUserName();
		logOut = new MenuItem(userName, getSignOutCommand());
		logOut.setTitle(app.getMenu("ClickHereToSignOut"));
		profile = new MenuItem(app.getMenu("userProfile"), getUserProfileCommand());
		addItem(profile);
		addItem(logOut);
    }

	private ScheduledCommand getUserProfileCommand() {
	    return new ScheduledCommand() {
			
			public void execute() {
				Window.alert("profile shown somehow");
			}
		};
    }

	private ScheduledCommand getSignOutCommand() {
		return new ScheduledCommand() {
			
			public void execute() {
				// this method should only be called from
				// AppWapplication or AppWapplet
			    app.getLoginOperation().performLogOut();
			}
		};
    }

	/**
	 * refresh the state with the new stored credentials
	 */
	public void refreshstate() {
	    logOut.setText(((LogInOperation)app.getLoginOperation()).getUserName());
    }

}
