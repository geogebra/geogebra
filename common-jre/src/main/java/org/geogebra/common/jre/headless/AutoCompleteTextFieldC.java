package org.geogebra.common.jre.headless;

import com.himamis.retex.renderer.share.TeXConstants;
import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GFont;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.awt.GRectangle;
import org.geogebra.common.euclidian.Drawable;
import org.geogebra.common.euclidian.draw.DrawInputBox;
import org.geogebra.common.euclidian.event.FocusListener;
import org.geogebra.common.euclidian.event.KeyHandler;
import org.geogebra.common.gui.inputfield.AutoCompleteTextField;
import org.geogebra.common.javax.swing.GBox;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoInputBox;
import org.geogebra.common.kernel.geos.properties.TextAlignment;
import org.geogebra.common.main.App;
import org.geogebra.common.util.AutoCompleteDictionary;

public class AutoCompleteTextFieldC implements AutoCompleteTextField {

	String textField = "";
	TextAlignment alignment;

	public AutoCompleteTextFieldC(int columns, App app,
								  Drawable drawTextField, boolean showSymbolButton) {

	}

	@Override
	public void showPopupSymbolButton(boolean b) {

	}

	@Override
	public void setAutoComplete(boolean b) {

	}

	@Override
	public void enableColoring(boolean b) {

	}

	@Override
	public void setFocus(boolean b) {

	}

	@Override
	public void setOpaque(boolean b) {

	}

	@Override
	public void setFont(GFont font) {

	}

	@Override
	public void setForeground(GColor color) {

	}

	@Override
	public void setBackground(GColor color) {

	}

	@Override
	public void setFocusable(boolean b) {

	}

	@Override
	public void requestFocus() {

	}

	@Override
	public void addFocusListener(FocusListener focusListener) {

	}

	@Override
	public void addKeyHandler(KeyHandler handler) {

	}

	@Override
	public int getCaretPosition() {
		return 0;
	}

	@Override
	public void setCaretPosition(int caretPos) {

	}

	@Override
	public void setDictionary(boolean forCAS) {

	}

	@Override
	public AutoCompleteDictionary getDictionary() {
		return null;
	}

	@Override
	public void setFocusTraversalKeysEnabled(boolean b) {

	}

	@Override
	public void setUsedForInputBox(GeoInputBox geoTextField) {

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

	}

	@Override
	public void removeSymbolTable() {

	}

	@Override
	public void prepareShowSymbolButton(boolean b) {

	}

	@Override
	public void hideDeferred(GBox box) {

	}

	@Override
	public void drawBounds(GGraphics2D g2, GColor bgColor, int left, int top, int width, int height) {

	}

	@Override
	public String getCommand() {
		return null;
	}

	@Override
	public void setPrefSize(int width, int height) {

	}

	@Override
	public void wrapSetText(String text) {

	}

	@Override
	public void setAuralText(String text) {

	}

	@Override
	public void drawBounds(GGraphics2D g2, GColor bgColor, GRectangle inputFieldBounds) {

	}

	@Override
	public void setSelection(int start, int end) {

	}

	@Override
	public void setTextAlignmentsForInputBox(TextAlignment alignment) {
		this.alignment = alignment;
	}

	@Override
	public void geoElementSelected(GeoElement geo, boolean addToSelection) {

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
	public void setColumns(int fieldWidth) {

	}

	@Override
	public void setVisible(boolean b) {

	}

	@Override
	public void setEditable(boolean b) {

	}

	public TextAlignment getAlignment() {
		return alignment;
	}

}
