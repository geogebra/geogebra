package geogebra.common.gui.infobar;

/**
 * Information bar to show announcements.
 * @author Zoltan Kovacs <zoltan@geogebra.org>
 */
public class InfoBar {
	
	/**
	 * Puts a global announcement on the display.
	 * @app the application (to know the window)
	 * @param message the information to show
	 */
	public void show(String message) {
		// override in the various platforms
	}
	
	/**
	 * Hide (remove) the global announcement.
	 */
	public void hide() {
		// override in the various platforms
	}

}
