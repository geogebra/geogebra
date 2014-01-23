package geogebra.web;

import geogebra.web.main.AppW;

import com.google.gwt.user.client.ui.Panel;

/**
 * Parts of Web.java copied here for making it possible
 * to create more entry point classes in the future
 * 
 * @author arpad
 */
public class WebStatic {

	/**
	 * @author gabor
	 * Describes the Gui type that needed to load
	 *
	 */
	public enum GuiToLoad {
		/**
		 * Gui For an App.
		 */
		APP, 
		/**
		 * Gui for a mobile
		 */
		MOBILE,
		/**
		 * No Gui, only euclidianView
		 */
		VIEWER
	}
	
	/**
	 * GUI currently Loaded
	 */
	public static Panel panelForApplets;
	public static String urlToOpen;
	public static AppW lastApp;

}
