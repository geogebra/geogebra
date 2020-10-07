package org.geogebra.web.html5.main;

import static org.junit.Assert.assertEquals;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.main.settings.EuclidianSettings;
import org.geogebra.web.full.main.AppWFull;
import org.geogebra.web.html5.util.AppletParameters;
import org.geogebra.web.test.AppMocker;
import org.geogebra.web.test.GgbMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwtmockito.WithClassesToStub;
import com.himamis.retex.renderer.web.graphics.JLMContext2d;


@RunWith(GgbMockitoTestRunner.class)
@WithClassesToStub({JLMContext2d.class, RootPanel.class})
public class TemplateLoadTest {
	private static AppWFull app;
	private static EuclidianSettings settings;

	@Before
	public void init() {
		AppletParameters articleElement = new AppletParameters("notes");
		app = AppMocker.mockApplet(articleElement);
		app.setXML("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n<geogebra format=\"5.0\" " +
				"version=\"5.0.570.0\" app=\"notes\" platform=\"w\" " +
				"id=\"F3D7E07C-758A-4337-A238-9A7EC72361E1\"  " +
				"xsi:noNamespaceSchemaLocation=\"http://www.geogebra.org/ggb.xsd\" xmlns=\"\" " +
				"xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" >\n<gui>\n\t<window " +
				"width=\"953\" height=\"787\" />\n\t<labelingStyle  val=\"0\"/>\n\t<font  " +
				"size=\"16\"/>\n</gui>\n<euclidianView>\n\t<viewNumber viewNo=\"1\"/>\n\t<size  " +
				"width=\"953\" height=\"787\"/>\n\t<coordSystem xZero=\"476.4999999999999\" " +
				"yZero=\"393.5\" scale=\"49.99999999999999\" yscale=\"50\"/>\n\t" +
				"<evSettings axes=\"false\" grid=\"false\" gridIsBold=\"false\"" +
				" pointCapturing=\"3\" rightAngleStyle=\"1\" checkboxSize=\"26\" gridType=\"3\"/>" +
				"\n\t<bgColor r=\"255\" g=\"255\" b=\"255\"/>\n\t" +
				"<axesColor r=\"0\" g=\"0\" b=\"0\"/>\n\t<gridColor r=\"192\" g=\"192\" b=\"192\"/>" +
				"\n\t<rulerType val=\"2\" bold=\"true\"/>\n\t" +
				"<rulerColor r=\"128\" g=\"0\" b=\"128\"/>\n\t<penSize val=\"30\"/>" +
				"\n\t<penColor r=\"204\" g=\"0\" b=\"153\"/>\n\t<highlighterSize val=\"1\"/>" +
				"\n\t<highlighterColor r=\"219\" g=\"97\" b=\"20\"/>\n\t<eraserSize val=\"61\"/>" +
				"\n\t<language val=\"hu\"/>\n</euclidianView>\n<kernel>\n\t" +
				"<continuous val=\"false\"/>\n\t<usePathAndRegionParameters val=\"true\"/>\n\t" +
				"<decimals val=\"2\"/>\n\t<angleUnit val=\"degree\"/>\n\t" +
				"<algebraStyle val=\"0\" spreadsheet=\"0\"/>\n\t<coordStyle val=\"0\"/>\n</kernel>" +
				"\n<tableview min=\"-2\" max=\"2\" step=\"1\"/>\n" +
				"<scripting blocked=\"false\" disabled=\"false\"/>\n" +
				"<construction title=\"templateTest\" author=\"\" date=\"\">\n</construction>\n" +
				"</geogebra>",false);
		settings = app.getActiveEuclidianView().getSettings();
	}

	@Test
	public void testLoadTemplatePenThickness() {
        assertEquals(settings.getLastPenThickness(),30);
	}

	@Test
	public void testLoadTemplatePenColor() {
        assertEquals(settings.getLastSelectedPenColor(), GColor.newColor(204,0,153));
	}

	@Test
	public void testLoadTemplateHighlighterThickness() {
        assertEquals(settings.getLastHighlighterThinckness(),1);
	}

	@Test
	public void testLoadTemplateHighlighterColor() {
        assertEquals(settings.getLastSelectedHighlighterColor(),
				GColor.newColor(219,97,20));
	}

	@Test
	public void testLoadTemplateEraserSize() {
        assertEquals(settings.getDeleteToolSize(),61);
	}
}
