package geogebra.gui.infobar;

import geogebra.common.main.App;
import geogebra.gui.app.GeoGebraFrame;
import geogebra.main.AppD;

/**
 * Put a global announcement on the display. 
 * @author Zoltan Kovacs <zoltan@geogebra.org>
 */

public class InfoBarD extends geogebra.common.gui.infobar.InfoBar {

	/**
	 * Global object for announcements for user's interest.
	 */
	
	String prevInfo = null;
	private App myApp;
	
	/**
	 * Constructor
	 * @param app the current application
	 */
	public InfoBarD(App app) {
		myApp = app;
	}

	private GeoGebraFrame getFrame() {
		return (GeoGebraFrame) ((AppD)myApp).getFrame();
	}
	
    @Override
	public void show(String message) {
		App.info("ANNOUNCEMENT: " + message);
    	prevInfo = getFrame().getTitle();
    	String newName = prevInfo + " - " + message;
    	getFrame().setTitle(newName);
    	
    	// This does not work on Ubuntu 11.04 GNOME.
    	// I tried the hints here but it did not help:
    	// http://stackoverflow.com/questions/10986569/set-title-of-java-application-opensuse
	}
	
    @Override
	public void hide() {
    	if (prevInfo != null) {
    		getFrame().setTitle(prevInfo);
    		App.info("ANNOUNCEMENT - off");
    	}
    		
	}

}
