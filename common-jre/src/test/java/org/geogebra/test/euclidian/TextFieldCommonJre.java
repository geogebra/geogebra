package org.geogebra.test.euclidian;

import org.geogebra.common.awt.GRectangle;
import org.geogebra.common.euclidian.ViewTextField;
import org.geogebra.common.euclidian.draw.DrawInputBox;
import org.geogebra.common.gui.inputfield.AutoCompleteTextField;

public class TextFieldCommonJre extends ViewTextField {

	AutoCompleteTextField textField;

	@Override
	public AutoCompleteTextField getTextField() {
		return textField;
	}

	@Override
	public void setBoxVisible(boolean isVisible) {

	}

	@Override
	public void setBoxBounds(GRectangle labelRectangle) {

	}

	@Override
	protected AutoCompleteTextField getTextField(int length, DrawInputBox drawInputBox) {
		if (textField == null) {
			textField = new AutoCompleteTextFieldC();
		}

		return textField;
	}

	@Override
	public void remove() {

	}
}
