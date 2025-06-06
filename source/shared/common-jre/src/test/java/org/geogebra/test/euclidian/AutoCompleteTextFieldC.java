package org.geogebra.test.euclidian;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GFont;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.awt.GRectangle;
import org.geogebra.common.euclidian.draw.DrawInputBox;
import org.geogebra.common.euclidian.event.FocusListenerDelegate;
import org.geogebra.common.euclidian.event.KeyHandler;
import org.geogebra.common.gui.inputfield.AutoCompleteTextField;
import org.geogebra.common.gui.inputfield.InputMode;
import org.geogebra.common.kernel.geos.GeoInputBox;
import org.geogebra.common.kernel.geos.properties.HorizontalAlignment;

import com.himamis.retex.editor.share.util.KeyCodes;

/**
 * Mock of a textfield, provides consistent getter/setter for content and
 * alignment.
 */
public class AutoCompleteTextFieldC implements AutoCompleteTextField {

	private String textField = "";
	private HorizontalAlignment alignment;
	private boolean focus = false;
	private GeoInputBox geoInputBox = null;
	private List<FocusListenerDelegate> focusListeners = new ArrayList<>();
	private List<KeyHandler> keyHandlers = new ArrayList<>();

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
		this.focus = b;
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
		focus = true;
	}

	@Override
	public void addFocusListener(FocusListenerDelegate focusListener) {
		focusListeners.add(focusListener);
	}

	@Override
	public void addKeyHandler(KeyHandler handler) {
		keyHandlers.add(handler);
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
	public void setUsedForInputBox(GeoInputBox geoInputBox) {
		this.geoInputBox = geoInputBox;
	}

	@Override
	public boolean hasFocus() {
		return focus;
	}

	@Override
	public boolean usedForInputBox() {
		return geoInputBox != null;
	}

	@Override
	public GeoInputBox getInputBox() {
		return geoInputBox;
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
	public void prepareShowSymbolButton(boolean show) {
		// for test, not needed
	}

	@Override
	public void drawBounds(GGraphics2D g2, GColor bgColor, int left, int top,
			int width, int height) {
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
	public void setTextAlignmentsForInputBox(HorizontalAlignment alignment) {
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
	public HorizontalAlignment getAlignment() {
		return alignment;
	}

	/**
	 * Notify all listeners
	 */
	public void blur() {
		for (FocusListenerDelegate listener : focusListeners) {
			listener.focusLost();
		}
	}

	/**
	 * Notify all handlers about Enter key release
	 */
	public void onEnter() {
		for (KeyHandler listener : new ArrayList<>(keyHandlers)) {
			listener.keyReleased(new KeyEventC(KeyCodes.ENTER));
		}
	}

	@Override
	public void setInputMode(InputMode type) {
		// not needed
	}
}
