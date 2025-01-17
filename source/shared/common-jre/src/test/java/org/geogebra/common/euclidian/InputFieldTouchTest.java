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
