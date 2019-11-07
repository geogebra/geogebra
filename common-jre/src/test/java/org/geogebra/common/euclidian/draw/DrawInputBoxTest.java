package org.geogebra.common.euclidian.draw;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.kernel.geos.GeoInputBox;
import org.junit.Assert;
import org.junit.Test;

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

	private int getHeightOfInputBox(DrawInputBox inputBoxDrawer, boolean symbolicMode) {
		setSymbolicMode(inputBoxDrawer, symbolicMode);
		return (int) inputBoxDrawer.getInputFieldBounds().getHeight();
	}

	private void setSymbolicMode(DrawInputBox inputBoxDrawer, boolean symbolicMode) {
		inputBoxDrawer.getGeoInputBox().setSymbolicMode(symbolicMode);
		inputBoxDrawer.update();
	}
}
