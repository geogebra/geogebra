package org.geogebra.web.full.gui;

import org.geogebra.keyboard.web.UpdateKeyBoardListener;
import org.geogebra.web.html5.gui.ToolBarInterface;
import org.geogebra.web.html5.gui.util.MathKeyboardListener;
import org.geogebra.web.html5.main.HasAppletProperties;

import com.google.gwt.dom.client.Element;

/**
 * Interface for app frame
 *
 */
public interface HeaderPanelDeck
		extends HasAppletProperties, UpdateKeyBoardListener {
	/**
	 * Hide the full-sized GUI, e.g. material browser
	 * 
	 * @param panel
	 *            full-sized GUI
	 */
	void hidePanel(MyHeaderPanel panel);

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
	 * @return frame element
	 */
	Element getElement();

	/**
	 * Make sure keyboard visibility corresponds to both app.isKeyboardNeeded()
	 * and appNeedsKeyboard() TODO rename one of those functions
	 */
	void refreshKeyboard();

	/**
	 * @param show
	 *            whether to show it
	 * @param textField
	 *            listening text field
	 * @param forceShow
	 *            whether to force showing
	 * @return whether keyboard visibility changed
	 */
	boolean showKeyBoard(boolean show, MathKeyboardListener textField,
			boolean forceShow);

}
