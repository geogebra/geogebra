/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 * 
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 * 
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */
 
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
		}

		return textField;
	}

	@Override
	public void remove() {
		// stub
	}
}
