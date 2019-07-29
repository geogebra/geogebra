package org.geogebra.common.gui.inputfield;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GFont;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.awt.GRectangle;
import org.geogebra.common.euclidian.draw.DrawInputBox;
import org.geogebra.common.euclidian.event.FocusListener;
import org.geogebra.common.euclidian.event.KeyHandler;
import org.geogebra.common.javax.swing.GBox;
import org.geogebra.common.kernel.geos.GeoInputBox;
import org.geogebra.common.main.GeoElementSelectionListener;
import org.geogebra.common.util.AutoCompleteDictionary;
import org.geogebra.common.util.TextObject;

public interface AutoCompleteTextField
		extends GeoElementSelectionListener, TextObject {

	void showPopupSymbolButton(boolean b);

	void setAutoComplete(boolean b);

	// inputfield.MyTextfield
	void enableColoring(boolean b);

	void setFocus(boolean b);

	void setOpaque(boolean b);

	// javax.swing.JTextField
	void setFont(GFont font);

	// javax.swing.JComponent
	void setForeground(GColor color);

	// javax.swing.JComponent
	void setBackground(GColor color);

	// java.awt.Component
	void setFocusable(boolean b);

	// javax.swing.JComponent
	void requestFocus();

	void addFocusListener(FocusListener focusListener);

	void addKeyHandler(KeyHandler handler);

	int getCaretPosition();

	void setCaretPosition(int caretPos);

	void setDictionary(boolean forCAS);

	AutoCompleteDictionary getDictionary();

	void setFocusTraversalKeysEnabled(boolean b);

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

	void hideDeferred(GBox box);

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
}
