package org.geogebra.web.web.gui;

import org.geogebra.web.html5.gui.ToolBarInterface;
import org.geogebra.web.html5.main.HasAppletProperties;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.HeaderPanel;

/**
 * Interface for app frame
 *
 */
public interface HeaderPanelDeck extends HasAppletProperties {
	/**
	 * Hide the full-sized GUI, e.g. material browser
	 * 
	 * @param myHeaderPanel
	 *            full-sized GUI
	 */
	void hideBrowser(MyHeaderPanel myHeaderPanel);

	/** @return toolbar */
	ToolBarInterface getToolbar();

	/**
	 * Update component heights to account for input bar
	 * 
	 * @param inputShowing
	 *            whether horizontal input bar is shown
	 */
	void setMenuHeight(boolean inputShowing);

	/**
	 * @param bg
	 *            full-sized GUI
	 */
	void showBrowser(HeaderPanel bg);

	/**
	 * @return frame element
	 */
	Element getElement();

}
