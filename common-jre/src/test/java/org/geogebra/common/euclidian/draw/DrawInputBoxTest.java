package org.geogebra.common.euclidian.draw;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.kernel.geos.GeoInputBox;
import org.geogebra.common.plugin.GeoClass;
import org.geogebra.common.plugin.script.GgbScript;
import org.geogebra.common.util.TextObject;
import org.geogebra.test.euclidian.AutoCompleteTextFieldC;
import org.geogebra.test.euclidian.TextFieldCommonJre;
import org.junit.Test;
import org.mockito.Mockito;

public class DrawInputBoxTest extends BaseUnitTest {

	@Test
	public void testConsistentHeight() {
		add("f(x) = x");
		add("a=1");
		GeoInputBox inputBox = add("InputBox(f)");
		GeoInputBox emptyInputBox = add("InputBox(a)");

		TextObject textObject = mockTextObjectWithReturn("");
		emptyInputBox.textObjectUpdated(textObject);

		DrawInputBox inputBoxDrawer
				= new DrawInputBox(getApp().getActiveEuclidianView(), inputBox);
		DrawInputBox emptyInputBoxDrawer
				= new DrawInputBox(getApp().getActiveEuclidianView(), emptyInputBox);

		int inputBoxHeight = getHeightOfInputBox(inputBoxDrawer, true);
		int emptyInputBoxHeight = getHeightOfInputBox(emptyInputBoxDrawer, true);

		assertEquals(inputBoxHeight, emptyInputBoxHeight);
	}

	@Test
	public void testDefaultHeightForFocusedInput() {
		add("a=1");
		GeoInputBox inputBox = (GeoInputBox) add("InputBox(a)");
		inputBox.setSymbolicMode(true, false);

		EuclidianView ev = getApp().getActiveEuclidianView();
		ev.setViewTextField(new TextFieldCommonJre());

		DrawInputBox inputBoxDrawer = new DrawInputBox(getApp().getActiveEuclidianView(), inputBox);
		int symbolicInputBoxHeightNotFocused = getHeightOfInputBox(inputBoxDrawer, true);

		inputBoxDrawer.getTextField().requestFocus();

		int symbolicInputBoxHeightFocused = getHeightOfInputBox(inputBoxDrawer, true);

		assertEquals(symbolicInputBoxHeightNotFocused,
				symbolicInputBoxHeightFocused);
	}

	@Test
	public void testHeightWontChangeAfterFirstCharacter() {
		add("a=1");
		GeoInputBox inputBox = add("InputBox(a)");
		inputBox.setSymbolicMode(true, false);

		EuclidianView ev = getApp().getActiveEuclidianView();
		ev.setViewTextField(new TextFieldCommonJre());

		TextObject textObject = mockTextObjectWithReturn("");
		inputBox.textObjectUpdated(textObject);

		DrawInputBox inputBoxDrawer = new DrawInputBox(getApp().getActiveEuclidianView(), inputBox);

		int symbolicInputBoxHeightEmptyInput = getHeightOfInputBox(inputBoxDrawer, true);
		textObject = mockTextObjectWithReturn("2");
		inputBox.textObjectUpdated(textObject);

		int symbolicInputBoxHeight = getHeightOfInputBox(inputBoxDrawer, true);
		assertEquals(symbolicInputBoxHeightEmptyInput, symbolicInputBoxHeight);
	}

	@Test
	public void inputBoxShouldNotStealContent() {
		EuclidianView ev = getApp().getActiveEuclidianView();
		ev.setViewTextField(new TextFieldCommonJre());
		add("a=1");
		GeoInputBox inputBoxNumber = add("InputBox(a)");
		add("B=(1,1)");
		add("InputBox(B)");
		inputBoxNumber
				.setClickScript(
						new GgbScript(getApp(), "UpdateConstruction()"));
		AutoCompleteTextFieldC tf = (AutoCompleteTextFieldC) ((DrawInputBox) ev
				.getDrawableFor(inputBoxNumber)).getTextField();
		tf.setUsedForInputBox(inputBoxNumber);
		tf.requestFocus();
		tf.setText("2");
		tf.blur();
		tf.onEnter();
		assertEquals(GeoClass.NUMERIC, lookup("a").getGeoClassType());
		// the textfield is now hidden, can be empty or contain "2", but not
		// definition of B
		assertNotEquals("(1, 1)", tf.getText());
	}

	private int getHeightOfInputBox(DrawInputBox inputBoxDrawer, boolean symbolicMode) {
		setSymbolicMode(inputBoxDrawer, symbolicMode);
		return (int) inputBoxDrawer.getInputFieldBounds().getHeight();
	}

	private void setSymbolicMode(DrawInputBox inputBoxDrawer, boolean symbolicMode) {
		inputBoxDrawer.getGeoInputBox().setSymbolicMode(symbolicMode);
		inputBoxDrawer.update();
	}

	private static TextObject mockTextObjectWithReturn(String text) {
		TextObject textObject = Mockito.mock(TextObject.class);
		Mockito.when(textObject.getText()).thenReturn(text);
		return textObject;
	}
}
