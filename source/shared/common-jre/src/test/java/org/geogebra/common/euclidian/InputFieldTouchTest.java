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

package org.geogebra.common.euclidian;

import static org.junit.Assert.assertEquals;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.euclidian.draw.DrawInputBox;
import org.geogebra.common.kernel.geos.GeoInputBox;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.test.euclidian.AutoCompleteTextFieldC;
import org.geogebra.test.euclidian.TextFieldCommonJre;
import org.junit.Before;
import org.junit.Test;

public class InputFieldTouchTest extends BaseUnitTest  {

	private GeoInputBox input1;
	private GeoInputBox input2;
	private ViewTextField viewTextField = new TextFieldCommonJre();
	private AutoCompleteTextFieldC textField;

	@Before
	public void setUp() {
		getApp().getActiveEuclidianView().setViewTextField(viewTextField);

		input1 = addInputBox("inputbox1");
		input2 = addInputBox("inputbox2");
		textField = (AutoCompleteTextFieldC) viewTextField.getTextField();
	}

	@Test
	public void applyOnSwitchInputBoxesTest() {
		DrawInputBox drawInputBox1 = (DrawInputBox) getDrawable(input1);
		DrawInputBox drawInputBox2 = (DrawInputBox) getDrawable(input2);
		viewTextField.focusTo(drawInputBox1);
		textField.setText("ABC");
		viewTextField.focusTo(drawInputBox2);
		textField.setText("DEF");
		viewTextField.focusTo(drawInputBox1);
		assertEquals("ABC", input1.getTextForEditor());
		assertEquals("DEF", input2.getTextForEditor());
	}

	@Test
	public void applyOnClickOutOfInputBoxTest() {
		DrawInputBox drawInputBox1 = (DrawInputBox) getDrawable(input1);
		viewTextField.focusTo(drawInputBox1);
		textField.setText("ABC");
		textField.blur();
		assertEquals("ABC", input1.getTextForEditor());
	}

	private GeoInputBox addInputBox(String name) {
		String command = name + " = InputBox()";
		GeoElementND[] results = getApp().getKernel().getAlgebraProcessor()
				.processAlgebraCommand(command, false);
		return (GeoInputBox) results[0];
	}
}
