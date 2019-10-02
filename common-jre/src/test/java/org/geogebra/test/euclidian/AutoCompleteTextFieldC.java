package org.geogebra.test.euclidian;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GFont;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.awt.GRectangle;
import org.geogebra.common.euclidian.draw.DrawInputBox;
import org.geogebra.common.euclidian.event.FocusListener;
import org.geogebra.common.euclidian.event.KeyHandler;
import org.geogebra.common.gui.inputfield.AutoCompleteTextField;
import org.geogebra.common.kernel.geos.GeoInputBox;
import org.geogebra.common.kernel.geos.properties.TextAlignment;
import org.geogebra.common.util.AutoCompleteDictionary;

/**
 * Mock of a textfield, provides consistent getter/setter for content and
 * alignment.
 */
public class AutoCompleteTextFieldC implements AutoCompleteTextField {

	private String textField = "";
	private TextAlignment alignment;

	@Override
	public void showPopupSymbolButton(boolean b) {
		// for test, not needed
	}

	@Override
	public void setAutoComplete(boolean b) {
		// for test, not needed
	}

	@Override
	public void setFocus(boolean b) {
		// for test, not needed
	}

	@Override
	public void setFont(GFont font) {
		// for test, not needed
	}

	@Override
	public void setForeground(GColor color) {
		// for test, not needed
	}

	@Override
	public void setBackground(GColor color) {
		// for test, not needed
	}

	@Override
	public void requestFocus() {
		// for test, not needed
	}

	@Override
	public void addFocusListener(FocusListener focusListener) {
		// for test, not needed
	}

	@Override
	public void addKeyHandler(KeyHandler handler) {
		// for test, not needed
	}

	@Override
	public int getCaretPosition() {
		return 0;
	}

	@Override
	public void setCaretPosition(int caretPos) {
		// for test, not needed
	}

	@Override
	public void setDictionary(boolean forCAS) {
		// for test, not needed
	}

	@Override
	public AutoCompleteDictionary getDictionary() {
		return null;
	}

	@Override
	public void setUsedForInputBox(GeoInputBox geoTextField) {
		// for test, not needed
	}

	@Override
	public boolean hasFocus() {
		return false;
	}

	@Override
	public boolean usedForInputBox() {
		return false;
	}

	@Override
	public GeoInputBox getInputBox() {
		return null;
	}

	@Override
	public DrawInputBox getDrawTextField() {
		return null;
	}

	@Override
	public void setDrawTextField(DrawInputBox df) {
		// for test, not needed
	}

	@Override
	public void removeSymbolTable() {
		// for test, not needed
	}

	@Override
	public void prepareShowSymbolButton(boolean b) {
		// for test, not needed
	}

	@Override
	public void drawBounds(GGraphics2D g2, GColor bgColor, int left, int top, int width, int height) {
		// for test, not needed
	}

	@Override
	public String getCommand() {
		return null;
	}

	@Override
	public void setPrefSize(int width, int height) {
		// for test, not needed
	}

	@Override
	public void wrapSetText(String text) {
		// for test, not needed
	}

	@Override
	public void setAuralText(String text) {
		// for test, not needed
	}

	@Override
	public void drawBounds(GGraphics2D g2, GColor bgColor, GRectangle inputFieldBounds) {
		// for test, not needed
	}

	@Override
	public void setSelection(int start, int end) {
		// for test, not needed
	}

	@Override
	public void setTextAlignmentsForInputBox(TextAlignment alignment) {
		this.alignment = alignment;
	}

	@Override
	public String getText() {
		return textField;
	}

	@Override
	public void setText(String s) {
		textField = s;
	}

	@Override
	public void setVisible(boolean b) {
		// for test, not needed
	}

	@Override
	public void setEditable(boolean b) {
		// for test, not needed
	}

	/**
	 * @return last value from setAlignment
	 */
	public TextAlignment getAlignment() {
		return alignment;
	}

}
