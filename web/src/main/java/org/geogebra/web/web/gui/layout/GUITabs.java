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
	public static final int EV_TAB_START = 2000;

	/** Start tab index of zoom buttons */
	public static final int ZOOMPANEL_TAB_START = 3000;

	/** Start tab index of algebra widgets */
	public static final int AV_TAB_START = 4000;

	/** Maximum tab count within one AV item */
	public static final int AV_MAX_TABS_IN_ITEM = 50;

	/** Tab index of floating move button in Tools tab. */
	public static final int TOOLS_MOVE_TAB = 4900;

}
