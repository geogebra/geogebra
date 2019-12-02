package org.geogebra.common.euclidian.draw;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.kernel.geos.GeoInputBox;
import org.geogebra.common.util.TextObject;
import org.geogebra.test.euclidian.TextFieldCommonJre;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

public class DrawInputBoxTest extends BaseUnitTest {

	@Test
	public void testConsistentHeight() {
		add("f(x) = x");
		GeoInputBox inputBox = (GeoInputBox) add("InputBox(f)");
		DrawInputBox inputBoxDrawer = new DrawInputBox(getApp().getActiveEuclidianView(), inputBox);

		int symbolicInputBoxHeight = getHeightOfInputBox(inputBoxDrawer, true);
		int nonSymbolicInputBoxHeight = getHeightOfInputBox(inputBoxDrawer, false);
		Assert.assertEquals(symbolicInputBoxHeight, nonSymbolicInputBoxHeight);
	}

	@Test
	public void testDefaultHeightForEmptyInput() {
		add("a=1");
		GeoInputBox inputBox = (GeoInputBox) add("InputBox(a)");
		inputBox.setSymbolicMode(true, false);
		TextObject textObject = mockTextObjectWithReturn("");
		inputBox.textObjectUpdated(textObject);

		DrawInputBox inputBoxDrawer = new DrawInputBox(getApp().getActiveEuclidianView(), inputBox);

		int symbolicInputBoxHeight = getHeightOfInputBox(inputBoxDrawer, true);
		int nonSymbolicInputBoxHeight = getHeightOfInputBox(inputBoxDrawer, false);

		Assert.assertEquals(symbolicInputBoxHeight, nonSymbolicInputBoxHeight);
	}

	@Test
	public void testDefaultHeightForFocusedInput() {
		add("a=1");
		GeoInputBox inputBox = (GeoInputBox) add("InputBox(a)");
		inputBox.setSymbolicMode(true, false);

		EuclidianView ev = getApp().getActiveEuclidianView();
		ev.setViewTextField(new TextFieldCommonJre(ev));

		DrawInputBox inputBoxDrawer = new DrawInputBox(getApp().getActiveEuclidianView(), inputBox);
		int symbolicInputBoxHeightNotFocused = getHeightOfInputBox(inputBoxDrawer, true);

		inputBoxDrawer.getTextField().requestFocus();

		int symbolicInputBoxHeightFocused = getHeightOfInputBox(inputBoxDrawer, true);

		Assert.assertEquals(symbolicInputBoxHeightNotFocused, symbolicInputBoxHeightFocused);
	}

	@Test
	public void testHeightWontChangeAfterFirstCharacter() {
		add("a=1");
		GeoInputBox inputBox = (GeoInputBox) add("InputBox(a)");
		inputBox.setSymbolicMode(true, false);

		EuclidianView ev = getApp().getActiveEuclidianView();
		ev.setViewTextField(new TextFieldCommonJre(ev));

		TextObject textObject = mockTextObjectWithReturn("");
		inputBox.textObjectUpdated(textObject);

		DrawInputBox inputBoxDrawer = new DrawInputBox(getApp().getActiveEuclidianView(), inputBox);

		int symbolicInputBoxHeightEmptyInput = getHeightOfInputBox(inputBoxDrawer, true);
		textObject = mockTextObjectWithReturn("2");
		inputBox.textObjectUpdated(textObject);

		int symbolicInputBoxHeight = getHeightOfInputBox(inputBoxDrawer, true);
		Assert.assertEquals(symbolicInputBoxHeightEmptyInput, symbolicInputBoxHeight);
	}

	private int getHeightOfInputBox(DrawInputBox inputBoxDrawer, boolean symbolicMode) {
		setSymbolicMode(inputBoxDrawer, symbolicMode);
		return (int) inputBoxDrawer.getInputFieldBounds().getHeight();
	}

	private void setSymbolicMode(DrawInputBox inputBoxDrawer, boolean symbolicMode) {
		inputBoxDrawer.getGeoInputBox().setSymbolicMode(symbolicMode);
		inputBoxDrawer.update();
	}

	private TextObject mockTextObjectWithReturn(String text) {
		TextObject textObject = Mockito.mock(TextObject.class);
		Mockito.when(textObject.getText()).thenReturn(text);
		return textObject;
	}
}
