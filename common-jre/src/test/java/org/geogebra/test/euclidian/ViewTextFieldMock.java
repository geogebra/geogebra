package org.geogebra.test.euclidian;

import org.geogebra.common.awt.GRectangle;
import org.geogebra.common.euclidian.ViewTextField;
import org.geogebra.common.euclidian.draw.DrawInputBox;
import org.geogebra.common.gui.inputfield.AutoCompleteTextField;

public class ViewTextFieldMock extends ViewTextField {
	private AutoCompleteTextField textField = new AutoCompleteTextFieldC();

	@Override
	public AutoCompleteTextField getTextField() {
		return textField;
	}

	@Override
	public void setBoxVisible(boolean isVisible) {
		// stub
	}

	@Override
	public void setBoxBounds(GRectangle labelRectangle) {
		// stub
	}

	@Override
	protected AutoCompleteTextField getTextField(int length, DrawInputBox drawInputBox) {
		if (textField == null) {
			textField = new AutoCompleteTextFieldC();
			textField.setAutoComplete(false);
		}
		textField.setDrawTextField(drawInputBox);

		return textField;
	}

	@Override
	public void remove() {
		textField = null;
	}
}
