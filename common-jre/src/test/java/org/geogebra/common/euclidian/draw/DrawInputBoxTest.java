package org.geogebra.common.euclidian.draw;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.kernel.geos.GeoInputBox;
import org.geogebra.common.util.TextObject;
import org.geogebra.test.euclidian.TextFieldCommonJre;
import org.junit.Assert;
import org.junit.Test;

public class DrawInputBoxTest extends BaseUnitTest {

	private TextObject textObject = new TextObject() {
		String content;

		@Override
		public String getText() {
			return content;
		}

		@Override
		public void setText(String s) {
			content = s;
		}

		@Override
		public void setVisible(boolean b) {

		}

		@Override
		public void setEditable(boolean b) {

		}
	};

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
		textObject.setText("");
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

		textObject.setText("");
		inputBox.textObjectUpdated(textObject);

		DrawInputBox inputBoxDrawer = new DrawInputBox(getApp().getActiveEuclidianView(), inputBox);

		int symbolicInputBoxHeightEmptyInput = getHeightOfInputBox(inputBoxDrawer, true);
		textObject.setText("2");
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
}
