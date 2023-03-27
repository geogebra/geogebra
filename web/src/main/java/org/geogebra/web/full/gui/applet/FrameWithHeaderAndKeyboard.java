package org.geogebra.web.full.gui.applet;

import org.geogebra.web.full.gui.HeaderPanelDeck;
import org.gwtproject.user.client.ui.HasWidgets;
import org.gwtproject.user.client.ui.InsertPanel;

/**
 * Frame with header and keyboard.
 */
public interface FrameWithHeaderAndKeyboard extends HeaderPanelDeck, HasWidgets, InsertPanel {

	/**
	 * @return True if the frame is shown in a small window or if the frame has a compact header.
	 */
	boolean hasSmallWindowOrCompactHeader();

	/**
	 * Gets the object's offset height in pixels. This is the total height of the
	 * object, including decorations such as border and padding, but not margin.
	 *
	 * @return the object's offset height
	 */
	int getOffsetHeight();

	/**
	 * Gets the object's offset width in pixels. This is the total width of the
	 * object, including decorations such as border and padding, but not margin.
	 *
	 * @return the object's offset width
	 */
	int getOffsetWidth();

	/**
	 * This method can be called after the full-screen panel is hidden.
	 */
	void onPanelHidden();
}
