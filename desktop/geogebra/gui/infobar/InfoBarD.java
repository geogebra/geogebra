package geogebra.gui.infobar;

import javax.swing.JFrame;

import geogebra.common.main.App;
import geogebra.main.AppD;

/**
 * Put a global announcement on the display. 
 * @author Zoltan Kovacs <zoltan@geogebra.org>
 */

public class InfoBarD extends geogebra.common.gui.infobar.InfoBar {

	/**
	 * Global object for announcements for user's interest.
	 */
	
	String origInfo = null;
	private App myApp;
	
	/**
	 * Constructor
	 * @param app the current application
	 */
	public InfoBarD(App app) {
		myApp = app;
	}
	// we can't cast this to GeoGebraFrame as it would break tests
	private JFrame getFrame() {
		return ((AppD)myApp).getFrame();
	}
	
    @Override
	public void show(String message) {
		App.info("ANNOUNCEMENT: " + message);
		
		if (myApp.isApplet()) // Avoid getting an exception below this point.
			return; // TODO: Find a way to inform the applet user.
		
		if (origInfo == null)
			origInfo = getFrame().getTitle();
    	String newName = origInfo + " - " + myApp.getPlain(message);
    	getFrame().setTitle(newName);
    	
    	// This does not work on Ubuntu 11.04 GNOME.
    	// I tried the hints here but it did not help:
    	// http://stackoverflow.com/questions/10986569/set-title-of-java-application-opensuse
	}
	
    @Override
	public void hide() {
    	if (origInfo != null) {
    		getFrame().setTitle(origInfo);
    		App.info("ANNOUNCEMENT - off");
    	}
    		
	}

}
