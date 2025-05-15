package org.geogebra.web.html5.gui.inputfield;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GFont;
import org.geogebra.common.kernel.geos.properties.HorizontalAlignment;
import org.gwtproject.event.dom.client.KeyDownEvent;
import org.gwtproject.event.dom.client.KeyPressEvent;

/**
 * Text field controller.
 */
public interface TextFieldController {

	/**
	 * Update the text field.
	 */
	void update();

	/**
	 * Select all the text
	 */
	void selectAll();

	/**
	 * Add the cursor directly
	 */
	void addCursor();

	/**
	 * Remove the cursor directly
	 */
	void removeCursor();

	/**
	 * Sets the font of the textfield
	 * @param font to set
	 */
	void setFont(GFont font);

	/**
	 * Sets the horizontal alignment of the textfield.
	 * @param alignment to set.
	 */
	void setHorizontalAlignment(HorizontalAlignment alignment);

	/**
	 * Unselects all in the textfield
	 */
	void unselectAll();

	/**
	 * Sets the foreground color of the textfield.
	 * @param color to set.
	 */
	void setForegroundColor(GColor color);

	/**
	 * Handles the keyboard
	 * @param e the event to handle.
	 */
	void handleKeyboardEvent(KeyDownEvent e);

	/**
	 *
	 * @param event to check.
	 * @return if keypress event should be inserted.
	 */
	boolean shouldBeKeyPressInserted(KeyPressEvent event);

	/**
	 *
	 * @return the start of the selection of the textfield
	 */
	int getSelectionStart();

	/**
	 *
	 * @return the end of the selection of the textfield
	 */
	int getSelectionEnd();

	/**
	 * clears selection.
	 */
	void clearSelection();
}