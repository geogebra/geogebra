package org.geogebra.web.html5.gui.accessibility;

import org.geogebra.web.html5.gui.TabHandler;

/**
 * Interface for widgets that supports accessibility.
 *   
 * @author laszlo
 *
 */
public interface AccessibilityInterface {
	
	/**
	 * Use this method in the same way you would use addKeyHandler for Tab keys
	 * only.
	 * 
	 * @param handler
	 *            handler
	 */
	void addTabHandler(TabHandler handler);
	
	/**
	 * Makes sure that TAB key is ignored.
	 * FocusWidget does not allow -1 as tabIndex by default. 
	 */
	void setIgnoreTab();
	
	/**
	 * Set alternate text that is proper for screen readers.
	 * @param text to set.
	 */
	void setAltText(String text);

}
