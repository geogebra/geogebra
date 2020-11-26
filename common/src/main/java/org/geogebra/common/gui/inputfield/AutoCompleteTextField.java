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
import org.geogebra.common.util.AutoCompleteDictionary;
import org.geogebra.common.util.TextObject;

public interface AutoCompleteTextField
		extends TextObject {

	void showPopupSymbolButton(boolean b);

	void setAutoComplete(boolean b);

	void setFocus(boolean b);

	// javax.swing.JTextField
	void setFont(GFont font);

	// javax.swing.JComponent
	void setForeground(GColor color);

	// javax.swing.JComponent
	void setBackground(GColor color);

	// javax.swing.JComponent
	void requestFocus();

	void addFocusListener(FocusListenerDelegate focusListener);

	void addKeyHandler(KeyHandler handler);

	int getCaretPosition();

	void setCaretPosition(int caretPos);

	void setDictionary(boolean forCAS);

	AutoCompleteDictionary getDictionary();

	void setUsedForInputBox(GeoInputBox geoTextField);

	boolean hasFocus();

	boolean usedForInputBox();

	// GGB-986
	GeoInputBox getInputBox();

	DrawInputBox getDrawTextField();

	void setDrawTextField(DrawInputBox df);

	/**
	 * Removes the "alpha" button forever
	 */
	void removeSymbolTable();

	void prepareShowSymbolButton(boolean b);

	void drawBounds(GGraphics2D g2, GColor bgColor, int left, int top,
			int width, int height);

	public String getCommand();

	void setPrefSize(int width, int height);

	void wrapSetText(String text);

	/**
	 * Set text for screen readers.
	 * 
	 * @param text to read.
	 */
	void setAuralText(String text);

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
