package org.geogebra.main;

import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.kernel.commands.AlgebraTest;
import org.geogebra.common.main.App;
import org.geogebra.common.plugin.Event;
import org.geogebra.common.plugin.EventType;
import org.geogebra.common.plugin.GgbAPI;
import org.geogebra.common.plugin.ScriptManager;
import org.geogebra.desktop.headless.AppDNoGui;
import org.geogebra.desktop.plugin.ScriptManagerD;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.util.List;
import java.util.Map;

public class APITest {
	private static AppDNoGui app;
	private static GgbAPI api;

	/**
	 * Initialize app.
	 */
	@Before
	public void setupApp() {
		app = AlgebraTest.createApp();
		api = app.getGgbApi();
	}

	@Test
	public void testEvalMathML() {
		api.evalMathML(
				"<mrow><mi> x</mi><mo> +</mo><mrow><mi> 1</mi><mo>/</mo><mi> 2</mi></mrow></mrow>");
		Assert.assertEquals(api.getLaTeXString("f"), "x + \\frac{1}{2}");
		Assert.assertEquals(api.getValueString("f"), "f(x) = x + 1 / 2");
	}

	@Test
	public void testEvalLaTeX() {
		api.evalLaTeX("latex(x)=\\sqrt{x}", 0);
		Assert.assertEquals(api.getLaTeXString("latex"), "\\sqrt{x}");
		Assert.assertEquals(api.getValueString("latex"), "latex(x) = sqrt(x)");
	}

	@Test
	public void testLabelStyle() {
		api.evalCommand("a=7");
		api.setLabelStyle("a", 1);
		Assert.assertEquals(api.getLabelStyle("a"), 1);
		api.setLabelStyle("a", 0);
		Assert.assertEquals(api.getLabelStyle("a"), 0);
		api.setLabelStyle("a", 100);
		Assert.assertEquals(api.getLabelStyle("a"), 0);
	}

	@Test
	public void testGrid() {
		api.setGridVisible(false);
		Assert.assertFalse(api.getGridVisible());
		Assert.assertFalse(api.getGridVisible(1));
		api.setGridVisible(true);
		Assert.assertTrue(api.getGridVisible());
		Assert.assertTrue(api.getGridVisible(1));
	}

	@Test
	public void testAxes() {
		api.evalCommand("SetVisibleInView[xAxis,1,true]");
		api.evalCommand("SetVisibleInView[yAxis,1,true]");
		Assert.assertTrue(api.getVisible("xAxis", 1));
		Assert.assertTrue(api.getVisible("yAxis", 1));

		api.evalCommand("SetVisibleInView[xAxis,1,false]");
		api.evalCommand("SetVisibleInView[yAxis,1,false]");
		Assert.assertFalse(api.getVisible("xAxis", 1));
		Assert.assertFalse(api.getVisible("yAxis", 1));

	}

	@Test
	public void testCaption() {
		api.evalCommand("b=1");
		api.evalCommand("SetCaption[b,\"%n rocks\"]");
		Assert.assertEquals(api.getCaption("b", false), "%n rocks");
		Assert.assertEquals(api.getCaption("b", true), "b rocks");
	}

	@Test
	public void perspectiveTest() {
		api.setPerspective("G");
		Assert.assertFalse(app.showView(App.VIEW_ALGEBRA));
		String geometryXML = api.getPerspectiveXML();
		api.setPerspective("AG");
		Assert.assertTrue(app.showView(App.VIEW_ALGEBRA));
		api.setPerspective(geometryXML);
		Assert.assertFalse(app.showView(App.VIEW_ALGEBRA));
		Assert.assertTrue(app.showView(App.VIEW_EUCLIDIAN));
	}

	@Test
	public void casEvalTest() {
		String assignResult = api.evalCommandCAS("$1:=a+a");
		Assert.assertEquals("2a", assignResult);
		String solveResult = api.evalGeoGebraCAS(
				"Solve[{ a=2, 12*sqrt(3)* a* b^2*exp(-3* b)-6*sqrt(3)* a* b*exp(-3* b)=0},{ a, b}]");
		Assert.assertEquals("{{a = 2, b = 0}, {a = 2, b = 1 / 2}}",
				solveResult);
		// OK in GUI, causes problems in the API - sent to Giac as
		// evalfa(ggbsort(normal(zeros((ggbtmpvart)^(2)=(4)*(ggbtmpvart),x))))
		String solveResult2 = api.evalGeoGebraCAS("Solutions[t^2 = 4t]");
		Assert.assertEquals("{0, 4}", solveResult2);
	}

	@Test
	public void viewChanged2DTest() {
		ScriptManager scriptManager = Mockito.spy(new ScriptManagerD(app));
		app.getEventDispatcher().addEventListener(scriptManager);
		app.setScriptManager(scriptManager);

		EuclidianView euclidianView = app.getActiveEuclidianView();
		euclidianView.setCoordSystem(30, 40, 5, 6);

		ArgumentCaptor<Event> eventCaptor = ArgumentCaptor.forClass(Event.class);

		Mockito.verify(scriptManager, Mockito.times(1))
				.sendEvent(eventCaptor.capture());

		List<Event> capturedEvents = eventCaptor.getAllValues();
		Assert.assertEquals(1, capturedEvents.size());

		Event event = capturedEvents.get(0);
		Assert.assertEquals(EventType.VIEW_CHANGED_2D, event.type);

		Map<String, Object> jsonArgument = event.jsonArgument;
		Assert.assertEquals(jsonArgument.get("xZero"), 30d);
		Assert.assertEquals(jsonArgument.get("yZero"), 40d);
		Assert.assertEquals(jsonArgument.get("scale"), 5d);
		Assert.assertEquals(jsonArgument.get("yscale"), 6d);
	}
}
