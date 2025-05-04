package org.geogebra.common.gui.inputfield;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GFont;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.awt.GRectangle;
import org.geogebra.common.euclidian.draw.DrawInputBox;
import org.geogebra.common.euclidian.event.FocusListenerDelegate;
import org.geogebra.common.euclidian.event.KeyHandler;
import org.geogebra.common.kernel.geos.GeoInputBox;
import org.geogebra.common.kernel.geos.properties.HorizontalAlignment;
import org.geogebra.common.util.TextObject;

/**
 * Text input component with autocompletion.
 */
public interface AutoCompleteTextField
		extends TextObject {

	/**
	 * Show or hide button for symbols popup or virtual keyboard
	 * @param b whether to show it
	 */
	void showPopupSymbolButton(boolean b);

	/**
	 * Turn autocompletion on or off.
	 * @param b whether to allow autocompletion
	 */
	void setAutoComplete(boolean b);

	/**
	 * Focus or blur this component.
	 * @param b whether to focus
	 */
	void setFocus(boolean b);

	/**
	 * Set font.
	 * @param font font
	 */
	void setFont(GFont font);

	/**
	 * Set text color.
	 * @param color color
	 */
	void setForeground(GColor color);

	/**
	 * Set background color.
	 * @param color color
	 */
	void setBackground(GColor color);

	/**
	 * Focus the component.
	 */
	void requestFocus();

	/**
	 * Add focus listener
	 * @param focusListener focus listener.
	 */
	void addFocusListener(FocusListenerDelegate focusListener);

	/**
	 * Add keyboard event handler.
	 * @param handler keyboard event handler
	 */
	void addKeyHandler(KeyHandler handler);

	/**
	 * @return caret position
	 */
	int getCaretPosition();

	/**
	 * @param caretPos caret position
	 */
	void setCaretPosition(int caretPos);

	/**
	 * @param geoTextField related input box
	 */
	void setUsedForInputBox(GeoInputBox geoTextField);

	boolean hasFocus();

	boolean usedForInputBox();

	// GGB-986
	GeoInputBox getInputBox();

	DrawInputBox getDrawTextField();

	/**
	 * @param df related input box drawable representation
	 */
	void setDrawTextField(DrawInputBox df);

	/**
	 * Removes the "alpha" button forever
	 */
	void removeSymbolTable();

	/**
	 * Note: in Web, the button opens the keyboard instead of a symbol popup.
	 * @param show whether to show the symbol popup button
	 */
	void prepareShowSymbolButton(boolean show);

	/**
	 * @deprecated use the Rectangle variant instead
	 */
	@Deprecated
	void drawBounds(GGraphics2D g2, GColor bgColor, int left, int top,
			int width, int height);

	String getCommand();

	/**
	 * Set preferred size.
	 * @param width width
	 * @param height height
	 */
	void setPrefSize(int width, int height);

	/**
	 * Deferred set text.
	 * @param text text field content
	 */
	void wrapSetText(String text);

	/**
	 * Set text for screen readers.
	 * 
	 * @param text to read.
	 */
	void setAuralText(String text);

	/**
	 * Draw bounds in graphics.
	 * @param g2 graphics
	 * @param bgColor background color
	 * @param inputFieldBounds bounds rectangle
	 */
	void drawBounds(GGraphics2D g2, GColor bgColor, GRectangle inputFieldBounds);

	/**
	 * @param start from which character to highlight the text
	 * @param end to which character to highlight the text
	 */
	void setSelection(int start, int end);

	/**
	 * @param alignment the text alignment in the input box
	 */
	void setTextAlignmentsForInputBox(HorizontalAlignment alignment);

	/**
	 * Set the input mode of the text field.
	 *
	 * @param mode mode
	 */
	void setInputMode(InputMode mode);
}
