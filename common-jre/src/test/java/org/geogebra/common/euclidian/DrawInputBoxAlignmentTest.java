package org.geogebra.common.euclidian;

import static org.junit.Assert.assertEquals;

import org.geogebra.common.awt.GGraphicsCommon;
import org.geogebra.common.euclidian.draw.DrawInputBox;
import org.geogebra.common.factories.AwtFactoryCommon;
import org.geogebra.common.jre.headless.AppCommon;
import org.geogebra.common.jre.headless.EuclidianViewNoGui;
import org.geogebra.common.jre.headless.LocalizationCommon;
import org.geogebra.common.kernel.geos.GeoInputBox;
import org.geogebra.common.kernel.geos.properties.TextAlignment;
import org.geogebra.test.euclidian.AutoCompleteTextFieldC;
import org.geogebra.test.euclidian.TextFieldCommonJre;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;


public class DrawInputBoxAlignmentTest {
	private static final double INPUT_TOP = 42.0d;
	private static final double INPUT_LEFT = 38;
	private static final double TEXT_WIDTH = 36;
	private static final double INPUT_WIDTH = 194;
	private static AppCommon app;
	private GeoInputBox inputBox;

	@Before
	public void setup() {
		AwtFactoryCommon factoryCommon = new AwtFactoryCommon();
		app = new AppCommon(new LocalizationCommon(2), factoryCommon);
		EuclidianView ev = app.getActiveEuclidianView();
		ev.initAxesValues();
		((EuclidianViewNoGui) ev).setViewTextField(new TextFieldCommonJre(ev));
		app.getKernel().getAlgebraProcessor().processAlgebraCommand("ZoomIn(-1,-1, 1,1) ", false);
		app.getKernel().getAlgebraProcessor().processAlgebraCommand("f = \"123456\" ", false);
		inputBox = (GeoInputBox) app.getKernel().getAlgebraProcessor()
				.processAlgebraCommand("A = InputBox(f)", false)[0];
	}

	@Test
	public void inputBoxTextAlignmentTest() {
		inputBox.setAlignment(TextAlignment.LEFT);
		inputBox.update();
		verifyDrawString("A", 30.0d, INPUT_TOP);
		verifyDrawString("123456", INPUT_LEFT + 2, INPUT_TOP);
		verifyAlignment(TextAlignment.LEFT);
	}

	@Test
	public void inputBoxTextAlignmentTestCenter() {
		inputBox.setAlignment(TextAlignment.CENTER);
		inputBox.update();
		verifyDrawString("123456",
				INPUT_LEFT + INPUT_WIDTH * .5 - TEXT_WIDTH * .5,
				INPUT_TOP);
		verifyAlignment(TextAlignment.CENTER);
	}

	@Test
	public void inputBoxTextAlignmentTestRight() {
		inputBox.setAlignment(TextAlignment.RIGHT);
		inputBox.update();
		verifyDrawString("123456", INPUT_LEFT + INPUT_WIDTH - TEXT_WIDTH
				- DrawInputBox.TF_PADDING_HORIZONTAL,
				INPUT_TOP);
		verifyAlignment(TextAlignment.RIGHT);
	}

	private void verifyAlignment(TextAlignment left) {
		AutoCompleteTextFieldC autoCompleteTextFieldC = (AutoCompleteTextFieldC) app
				.getActiveEuclidianView().getTextField();
		inputBox.update();
		assertEquals(left, autoCompleteTextFieldC.getAlignment());
	}

	private void verifyDrawString(String string, double d, double e) {
		GGraphicsCommon graphics = Mockito.spy(new GGraphicsCommon());
		Drawable drawable = (Drawable) app.getActiveEuclidianView().getDrawableFor(inputBox);
		drawable.draw(graphics);
		Mockito.verify(graphics).drawString(string, d, e);
	}
}