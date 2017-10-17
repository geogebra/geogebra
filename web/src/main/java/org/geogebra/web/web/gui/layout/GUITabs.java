package org.geogebra.web.web.gui.layout;

/**
 * 
 * @author Laszol Gal
 *
 */
public class GUITabs {
	/** It means that widget is not tab-able */
	public static final int NO_TAB = -1;

	/** Start tab index of header buttons */
	public static final int HEADER_TAB_START = 1000;

	/** Start tab index of EV buttons (Settings) */
	public static final int EV_SETTINGS = 2000;

	/** Start tab index of zoom buttons */
	public static final int ZOOMPANEL_TAB_START = 3000;

	/** Tab index of floating move button in Tools tab. */
	public static final int TOOLS_MOVE_TAB = 4900;

	/** Tab index of the + button in AV Input. */
	public static final int AV_PLUS = 5500;

	/**
	 * AV Tree tab index (for keyboard events only, no tab key should go there).
	 */
	public static final int AV_TREE = 6000;

}
