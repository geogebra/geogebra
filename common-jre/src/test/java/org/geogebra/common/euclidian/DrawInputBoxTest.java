package org.geogebra.common.euclidian;

import org.geogebra.common.awt.GGraphicsCommon;
import org.geogebra.common.factories.AwtFactoryCommon;
import org.geogebra.common.jre.headless.AppCommon;
import org.geogebra.common.jre.headless.AutoCompleteTextFieldC;
import org.geogebra.common.jre.headless.LocalizationCommon;
import org.geogebra.common.kernel.geos.GeoInputBox;
import org.geogebra.common.kernel.geos.properties.TextAlignment;
import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.assertEquals;


public class DrawInputBoxTest {
	private static AppCommon app;

	@Test
	public void inputBoxTextAlignmentTest() {
		AwtFactoryCommon factoryCommon = new AwtFactoryCommon();
		GGraphicsCommon graphics = Mockito.spy(new GGraphicsCommon());
		AutoCompleteTextFieldC autoCompleteTextFieldC = null;

		app = new AppCommon(new LocalizationCommon(2), factoryCommon);
		app.getActiveEuclidianView().initAxesValues();
		app.getKernel().getAlgebraProcessor().processAlgebraCommand("ZoomIn(-1,-1, 1,1) ", false);
		app.getKernel().getAlgebraProcessor().processAlgebraCommand("f = \"123456\" ", false);
		GeoInputBox inputBox = (GeoInputBox) app.getKernel().getAlgebraProcessor().processAlgebraCommand("A = InputBox(f)", false)[0];
		autoCompleteTextFieldC =  (AutoCompleteTextFieldC) app.getActiveEuclidianView().getTextField();
		((Drawable) app.getActiveEuclidianView().getDrawableFor(inputBox)).draw(graphics);

		Mockito.verify(graphics).drawString("A", 30.0d, 42.0d);
		Mockito.verify(graphics).drawString("123456", 40.0, 42.0);

		inputBox.update();
		assertEquals(TextAlignment.LEFT, autoCompleteTextFieldC.getAlignment());

		inputBox.setAlignment(TextAlignment.CENTER);
		((Drawable) app.getActiveEuclidianView().getDrawableFor(inputBox)).draw(graphics);
		Mockito.verify(graphics).drawString("123456", 116.0, 42.0);
		inputBox.update();
		assertEquals(TextAlignment.CENTER,autoCompleteTextFieldC.getAlignment());

		inputBox.setAlignment(TextAlignment.RIGHT);
		((Drawable) app.getActiveEuclidianView().getDrawableFor(inputBox)).draw(graphics);
		Mockito.verify(graphics).drawString("123456", 188.0, 42.0);
		inputBox.update();
		assertEquals(TextAlignment.RIGHT,autoCompleteTextFieldC.getAlignment());

	}
}