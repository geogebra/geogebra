package geogebra.touch.gui.dialogs;

import geogebra.common.awt.GColor;
import geogebra.common.awt.GFont;
import geogebra.common.euclidian.Drawable;
import geogebra.common.euclidian.event.FocusListener;
import geogebra.common.euclidian.event.KeyHandler;
import geogebra.common.javax.swing.GLabel;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoTextField;
import geogebra.common.main.App;
import geogebra.common.util.AutoCompleteDictionary;
import geogebra.html5.gui.inputfield.AutoCompleteTextFieldW;
import geogebra.touch.model.GuiModel;

import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.ui.TextBox;

public class AutoCompleteTextFieldT extends AutoCompleteTextFieldW {

	TextBox textBox = new TextBox();

	public AutoCompleteTextFieldT(int columns, App app,
			final Drawable drawTextField) {
		super(columns, app, drawTextField);

		this.textBox.getElement().setId(super.textField.getElement().getId());
		remove(super.textField);

		this.textBox.addKeyUpHandler(new KeyUpHandler() {
			@Override
			public void onKeyUp(KeyUpEvent event) {
				if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
					AutoCompleteTextFieldT.this.setFocus(false);
					if (((GeoTextField) drawTextField.getGeoElement())
							.getLinkedGeo() != null) {
						((GeoTextField) drawTextField.getGeoElement())
								.updateLinkedGeo(AutoCompleteTextFieldT.this.textBox
										.getText());
					}
				}
			}
		});

		if (columns > 0) {
			this.textBox.setWidth(columns + "em");
		}
		this.add(this.textBox);
	}

	@Override
	public void geoElementSelected(GeoElement geo, boolean addToSelection) {
	}

	@Override
	public String getText() {
		return this.textBox.getText();
	}

	@Override
	public void setText(String s) {
		this.textBox.setText(s);
	}

	@Override
	public void wrapSetText(String s) {
	}

	@Override
	public String getCommand() {
		return null;
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
		if (b) {
			GuiModel.activeTextField = this;
		}
		this.textBox.setFocus(b);
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
	public void setEditable(boolean b) {
	}

	@Override
	public void requestFocus() {
		this.setFocus(true);
	}

	@Override
	public void setLabel(GLabel label) {
	}

	@Override
	public void setColumns(int length) {
	}

	@Override
	public void addFocusListener(FocusListener focusListener) {
	}

	@Override
	public void addKeyHandler(KeyHandler handler) {
	}

	@Override
	public int getCaretPosition() {
		return this.textBox.getCursorPos();
	}

	@Override
	public void setCaretPosition(int caretPos) {
		this.textBox.setCursorPos(caretPos);
	}

	@Override
	public void setDictionary(AutoCompleteDictionary dict) {
	}

	@Override
	public AutoCompleteDictionary getDictionary() {
		return null;
	}

	@Override
	public void setFocusTraversalKeysEnabled(boolean b) {
	}

	@Override
	public void setUsedForInputBox(GeoTextField geoTextField) {
	}

	@Override
	public boolean hasFocus() {
		return false;
	}

	@Override
	public boolean usedForInputBox() {
		return false;
	}
}
