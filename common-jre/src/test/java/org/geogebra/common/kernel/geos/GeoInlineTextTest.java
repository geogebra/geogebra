package org.geogebra.common.kernel.geos;

import static org.junit.Assert.assertEquals;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GPoint2D;
import org.geogebra.common.io.XmlTestUtil;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.junit.Test;

public class GeoInlineTextTest extends BaseUnitTest {

	private static final String COMPATIBILITY_XML = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n"
			+ "<geogebra format=\"5.0\" version=\"5.0.570.0\" app=\"notes\" >\n"
			+ "<gui>\n"
			+ "\t<font  size=\"16\"/>\n"
			+ "</gui>\n"
			+ "<construction>\n"
			+ "<expression label=\"text1\" exp=\"&quot;GeoGebra Rocks&quot;\"/>\n"
			+ "<element type=\"text\" label=\"text1\">\n"
			+ "\t<objColor r=\"0\" g=\"0\" b=\"0\" alpha=\"0\"/>\n"
			+ "\t<font serif=\"false\" sizeM=\"1\" size=\"0\" style=\"1\"/>\n"
			+ "\t<startPoint  x=\"5\" y=\"6\" z=\"1\"/>\n"
			+ "\t<boundingBox x=\"500\" y=\"600\" width=\"150\" height=\"50\"/>\n"
			+ "</element>\n"
			+ "</construction>\n"
			+ "</geogebra>";

	@Test
	public void inlineTextCorrectlySavedAndLoaded() {
		final double x = 1.2;
		final double y = 2.5;
		final int width = 1848;
		final int height = 1956;
		final double angle = 2.7182;
		final String content = "this is a sample text; {}";

		Construction cons = getApp().getKernel().getConstruction();

		GPoint2D startPoint = new GPoint2D(x, y);

		GeoInlineText savedInlineText = new GeoInlineText(cons, startPoint);
		savedInlineText.setSize(width, height);
		savedInlineText.setLabel("testText");
		savedInlineText.setAngle(angle);
		savedInlineText.setContent(content);
		savedInlineText.setBorderColor(GColor.DARK_GREEN);
		savedInlineText.setBorderThickness(3);

		String appXML = getApp().getXML();
		XmlTestUtil.testCurrentXML(getApp());
		getApp().setXML(appXML, true);

		GeoInlineText loadedInlineText = (GeoInlineText) lookup("testText");

		assertEquals(x, loadedInlineText.getLocation().getX(), Kernel.MAX_PRECISION);
		assertEquals(y, loadedInlineText.getLocation().getY(), Kernel.MAX_PRECISION);
		assertEquals(width, loadedInlineText.getWidth(), Kernel.MAX_PRECISION);
		assertEquals(height, loadedInlineText.getHeight(), Kernel.MAX_PRECISION);
		assertEquals(angle, loadedInlineText.getAngle(), Kernel.MAX_PRECISION);
		assertEquals(content, loadedInlineText.getContent());
		assertEquals(GColor.DARK_GREEN, loadedInlineText.getBorderColor());
		assertEquals(3, loadedInlineText.getBorderThickness());
	}

	@Test
	public void loadingOldXmlShouldProduceInlineTexts() {
		getApp().setXML(COMPATIBILITY_XML, true);

		GeoInlineText loadedInlineText = (GeoInlineText) lookup("text1");

		assertEquals(5, loadedInlineText.getLocation().getX(), Kernel.MAX_PRECISION);
		assertEquals(6, loadedInlineText.getLocation().getY(), Kernel.MAX_PRECISION);
		assertEquals(150, loadedInlineText.getWidth(), Kernel.MAX_PRECISION);
		assertEquals(50, loadedInlineText.getHeight(), Kernel.MAX_PRECISION);
		assertEquals("[{\"text\":\"GeoGebra Rocks\",\"bold\":true}]",
				loadedInlineText.getContent());
	}
}
