package org.geogebra.common.kernel.geos;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.awt.GPoint2D;
import org.geogebra.common.factories.AwtFactory;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.junit.Assert;
import org.junit.Test;

public class GeoInlineTextTest extends BaseUnitTest {

	@Test
	public void inlineTextCorrectlySavedAndLoaded() {
		final double x = 1.2;
		final double y = 2.5;
		final int width = 1848;
		final int height = 1956;
		final String content = "this is a sample text; {}";

		Construction cons = getApp().getKernel().getConstruction();

		GPoint2D startPoint = AwtFactory.getPrototype().newPoint2D(x, y);

		GeoInlineText savedInlineText = new GeoInlineText(cons,	startPoint, width, height);
		savedInlineText.setLabel("testText");
		savedInlineText.setContent(content);

		String appXML = getApp().getXML();
		getApp().setXML(appXML, true);

		GeoInlineText loadedInlineText = (GeoInlineText) lookup("testText");

		Assert.assertEquals(x, loadedInlineText.getLocation().getX(), Kernel.MAX_PRECISION);
		Assert.assertEquals(y, loadedInlineText.getLocation().getY(), Kernel.MAX_PRECISION);
		Assert.assertEquals(width, loadedInlineText.getWidth(), Kernel.MAX_PRECISION);
		Assert.assertEquals(height, loadedInlineText.getHeight(), Kernel.MAX_PRECISION);
		Assert.assertEquals(content, loadedInlineText.getContent());
	}
}
