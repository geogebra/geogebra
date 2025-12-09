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
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.eq;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.awt.GGraphicsCommon;
import org.geogebra.common.euclidian.draw.DrawInputBox;
import org.geogebra.common.kernel.geos.GeoInputBox;
import org.geogebra.common.kernel.geos.properties.HorizontalAlignment;
import org.geogebra.test.euclidian.AutoCompleteTextFieldC;
import org.geogebra.test.euclidian.TextFieldCommonJre;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class DrawInputBoxAlignmentTest extends BaseUnitTest {
	private static final double INPUT_LEFT = 38;
	private static final double TEXT_WIDTH = 36;
	private static final double INPUT_WIDTH = 194;
	private GeoInputBox inputBox;

	@Before
	public void setupInput() {
		EuclidianView ev = getApp().getActiveEuclidianView();
		ev.setViewTextField(new TextFieldCommonJre());
		getKernel().getAlgebraProcessor().processAlgebraCommand("ZoomIn(-1,-1, 1,1) ", false);
		getKernel().getAlgebraProcessor().processAlgebraCommand("f = \"123456\" ", false);
		inputBox = (GeoInputBox) getKernel().getAlgebraProcessor()
				.processAlgebraCommand("A = InputBox(f)", false)[0];
		inputBox.setSymbolicMode(false);
	}

	@Test
	public void inputBoxTextAlignmentTest() {
		inputBox.setAlignment(HorizontalAlignment.LEFT);
		inputBox.update();
		verifyDrawString("A", 30.0d);
		verifyDrawString("123456", INPUT_LEFT + 2);
		verifyAlignment(HorizontalAlignment.LEFT);
	}

	@Test
	public void inputBoxTextAlignmentTestCenter() {
		inputBox.setAlignment(HorizontalAlignment.CENTER);
		inputBox.update();
		verifyDrawString(
				"123456",
				INPUT_LEFT + INPUT_WIDTH * .5 - TEXT_WIDTH * .5);
		verifyAlignment(HorizontalAlignment.CENTER);
	}

	@Test
	public void inputBoxTextAlignmentTestRight() {
		inputBox.setAlignment(HorizontalAlignment.RIGHT);
		inputBox.update();
		verifyDrawString(
				"123456",
				INPUT_LEFT + INPUT_WIDTH - TEXT_WIDTH
						- DrawInputBox.TF_PADDING_HORIZONTAL);
		verifyAlignment(HorizontalAlignment.RIGHT);
	}

	private void verifyAlignment(HorizontalAlignment left) {
		AutoCompleteTextFieldC autoCompleteTextFieldC = (AutoCompleteTextFieldC) getApp()
				.getActiveEuclidianView().getTextField();
		autoCompleteTextFieldC.setUsedForInputBox(inputBox);
		inputBox.update();
		assertEquals(left, autoCompleteTextFieldC.getAlignment());
	}

	private void verifyDrawString(String string, double x) {
		GGraphicsCommon graphics = Mockito.spy(new GGraphicsCommon());
		Drawable drawable = getDrawable(inputBox);
		drawable.draw(graphics);
		Mockito.verify(graphics).drawString(eq(string), eq(x), anyDouble());
	}
}