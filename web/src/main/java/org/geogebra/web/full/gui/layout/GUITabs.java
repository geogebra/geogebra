package org.geogebra.web.full.gui.layout;

import com.google.gwt.dom.client.Element;
import org.geogebra.web.html5.Browser;

/**
 * 
 * @author Laszol Gal
 *
 */
public class GUITabs {
	/** It means that widget is not tab-able */
	public static final int NO_TAB = -1;

	/** Tab index of the AV input row */
	public static final int AV_INPUT = 1000;

	// Here comes the AV Cells.

	/** Tab index of header buttons */
	public static final int MENU = 4000;

	/** Tab index of header close button */
	public static final int HEADER_CLOSE = 4001;

	/** Tab index of undo button */
	public static final int UNDO = 4002;

	/** Tab index of redo button */
	public static final int REDO = 4003;

	/** Tab index of settings button */
	public static final int SETTINGS = 4004;

	/** Tab index of zoom panel */
	public static final int ZOOM = 4005;

	/** Tab index speech recording button */
	public static final int SPEECH_REC = 4020;

	// Then back to first element of AV.

	/**
	 * AV Tree tab index (for keyboard events only, no tab key should go there.
	 */
	public static final int AV_TREE = 6000;

	/**
	 * Tab indices are unhealthy, so let's avoid them, whenever
	 * possible
	 */
	public static void setTabIndex(Element e, int index) {
		if (Browser.isMobile()) {
			e.setTabIndex(index);
		}
	}
}
