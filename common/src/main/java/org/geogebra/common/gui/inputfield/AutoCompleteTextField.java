package org.geogebra.common.gui.inputfield;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GFont;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.euclidian.event.FocusListener;
import org.geogebra.common.euclidian.event.KeyHandler;
import org.geogebra.common.javax.swing.GLabel;
import org.geogebra.common.kernel.geos.GeoTextField;
import org.geogebra.common.main.GeoElementSelectionListener;
import org.geogebra.common.util.AutoCompleteDictionary;
import org.geogebra.common.util.CommandInputField;
import org.geogebra.common.util.TextObject;

public interface AutoCompleteTextField extends GeoElementSelectionListener,
		TextObject, CommandInputField {

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

	// javax.swing.text.JTextComponent
	void setEditable(boolean b);

	// javax.swing.JComponent
	void requestFocus();

	void setLabel(GLabel label);

	void setVisible(boolean b);

	void setColumns(int length);

	void addFocusListener(FocusListener focusListener);

	void addKeyHandler(KeyHandler handler);

	int getCaretPosition();

	void setCaretPosition(int caretPos);

	void setDictionary(boolean forCAS);

	AutoCompleteDictionary getDictionary();

	void setFocusTraversalKeysEnabled(boolean b);

	void setUsedForInputBox(GeoTextField geoTextField);

	boolean hasFocus();

	boolean usedForInputBox();

	/**
	 * Removes the "alpha" button forever
	 */
	void removeSymbolTable();

	void prepareShowSymbolButton(boolean b);

	/**
	 * deferred focus for web
	 */
	boolean hasDeferredFocus();

	void setDeferredFocus(boolean b);

	void drawBounds(GGraphics2D g2, GColor bgColor, int left, int top,
			int width, int height);
}
