package org.geogebra.common.main;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.times;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.geogebra.common.euclidian.EuclidianController;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.factories.AwtFactoryCommon;
import org.geogebra.common.jre.headless.AppCommon;
import org.geogebra.common.jre.headless.LocalizationCommon;
import org.geogebra.common.jre.plugin.ScriptManagerJre;
import org.geogebra.common.kernel.Path;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoInputBox;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.plugin.Event;
import org.geogebra.common.plugin.EventType;
import org.geogebra.common.plugin.GgbAPI;
import org.geogebra.common.plugin.ScriptManager;
import org.geogebra.test.TestEvent;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import com.himamis.retex.editor.share.util.Greek;

public class GgbApiTest {
	private AppCommon app;
	private GgbAPI api;

	/**
	 * Initialize app.
	 */
	@Before
	public void setupApp() {
		app = new AppCommon3D(new LocalizationCommon(3), new AwtFactoryCommon());
		api = app.getGgbApi();
	}

	@Test
	public void testCaption() {
		api.evalCommand("b=1");
		api.evalCommand("SetCaption[b,\"%n rocks\"]");
		assertThat(api.getCaption("b", false), is("%n rocks"));
		assertThat(api.getCaption("b", true), is("b rocks"));
	}

	@Test
	public void testEvalMathML() {
		api.evalMathML(
				"<mrow><mi> x</mi><mo> +</mo><mrow><mi> 1</mi><mo>/</mo><mi> 2</mi></mrow></mrow>");
		assertThat(api.getLaTeXString("f"), is("x + \\frac{1}{2}"));
		assertThat(api.getValueString("f", true), is("f(x) = x + 1 / 2"));
	}

	@Test
	public void testEvalLaTeX() {
		api.evalLaTeX("latex(x)=\\sqrt{x}", 0);
		assertThat(api.getLaTeXString("latex"), is("\\sqrt{x}"));
		assertThat(api.getValueString("latex", true), is("latex(x) = sqrt(x)"));
	}

	@Test
	public void testEvalLaTeXBinom() {
		api.evalLaTeX("b=\\binom{10}{2}", 0);
		assertThat(api.getLaTeXString("b"), is("45"));
	}

	@Test
	public void testEvalLaTeXLog() {
		api.evalLaTeX("l=\\log_5(25)", 0);
		assertThat(api.getLaTeXString("l"), is("2"));
	}

	@Test
	public void testEvalLaTeXGreek() {
		List<String> set = Arrays.asList("Alpha", "Beta", "Epsilon", "Zeta", "Eta",
				"Iota", "Kappa", "Mu", "Nu", "Omicron", "Rho", "Tau", "Chi",
				"phi", "epsilon");
		for (Greek letter: Greek.values()) {
			if (!set.contains(letter.name())) {
				assertLaTeXGreekEval(letter.name(), String.valueOf(letter.unicode));
			}
		}
		assertLaTeXGreekEval("varphi", "\u03C6");
		assertLaTeXGreekEval("varpi", "\u03D6");
		assertLaTeXGreekEval("vartheta", "\u03D1");
		assertLaTeXGreekEval("varepsilon", "\u03B5");
		assertLaTeXGreekEval("varrho", "\u03F1");
		assertLaTeXGreekEval("phi", "\u03D5");
		assertLaTeXGreekEval("epsilon", "\u03F5");
	}

	private void assertLaTeXGreekEval(String latex, String unicode) {
		api.newConstruction();
		api.evalLaTeX("x\\" + latex + "=42", 0);
		assertEquals(latex + " not parsed as " + unicode, "42",
				api.getLaTeXString("x" + unicode));
	}

	@Test
	public void testLabelStyle() {
		api.evalCommand("a=7");
		api.setLabelStyle("a", 1);
		assertEquals(api.getLabelStyle("a"), 1);
		api.setLabelStyle("a", 0);
		assertEquals(api.getLabelStyle("a"), 0);
		api.setLabelStyle("a", 100);
		assertEquals(api.getLabelStyle("a"), 0);
	}

	@Test
	public void testGrid() {
		api.setGridVisible(false);
		assertFalse(api.getGridVisible());
		assertFalse(api.getGridVisible(1));
		api.setGridVisible(true);
		assertTrue(api.getGridVisible());
		assertTrue(api.getGridVisible(1));
	}

	@Test
	public void testAxes() {
		api.evalCommand("SetVisibleInView[xAxis,1,true]");
		api.evalCommand("SetVisibleInView[yAxis,1,true]");
		assertTrue(api.getVisible("xAxis", 1));
		assertTrue(api.getVisible("yAxis", 1));

		api.evalCommand("SetVisibleInView[xAxis,1,false]");
		api.evalCommand("SetVisibleInView[yAxis,1,false]");
		assertFalse(api.getVisible("xAxis", 1));
		assertFalse(api.getVisible("yAxis", 1));
	}

	@Test
	public void perspectiveTest() {
		api.setPerspective("G");
		assertFalse(app.showView(App.VIEW_ALGEBRA));
		String geometryXML = api.getPerspectiveXML();
		api.setPerspective("AG");
		assertTrue(app.showView(App.VIEW_ALGEBRA));
		api.setPerspective(geometryXML);
		assertFalse(app.showView(App.VIEW_ALGEBRA));
		assertTrue(app.showView(App.VIEW_EUCLIDIAN));
	}

	@Test
	public void viewChanged2DTest() {
		ScriptManager scriptManager = prepareScriptManager();

		EuclidianView euclidianView = app.getActiveEuclidianView();
		euclidianView.setCoordSystem(30, 40, 5, 6);

		ArgumentCaptor<Event> eventCaptor = ArgumentCaptor.forClass(Event.class);

		Mockito.verify(scriptManager, times(1))
				.sendEvent(eventCaptor.capture());

		List<Event> capturedEvents = eventCaptor.getAllValues();
		assertEquals(1, capturedEvents.size());

		Event event = capturedEvents.get(0);
		assertEquals(EventType.VIEW_CHANGED_2D, event.type);

		Map<String, Object> jsonArgument = event.jsonArgument;
		assertEquals(30d, jsonArgument.get("xZero"));
		assertEquals(40d, jsonArgument.get("yZero"));
		assertEquals(5d, jsonArgument.get("scale"));
		assertEquals(6d, jsonArgument.get("yscale"));
	}

	@Test
	public void objectListenerShouldSurviveRedefine() {
		MockScriptManager scriptManager = prepareScriptManager();

		api.evalCommand("C=1");
		api.evalCommand("ans = ?");
		api.evalCommand("input = InputBox(ans)");
		api.evalCommand("correct = ans == 2*C");
		GeoInputBox input = (GeoInputBox) lookup("input");
		scriptManager.registerObjectUpdateListener("correct", "onUpdate");

		input.updateLinkedGeo("2");
		Mockito.verify(scriptManager, times(1))
				.evalJavaScript("onUpdate(\"correct\");");

		input.updateLinkedGeo("2 + C");
		Mockito.verify(scriptManager, times(2))
				.evalJavaScript("onUpdate(\"correct\");");
	}

	private GeoElement lookup(String input) {
		return app.getKernel().lookupLabel(input);
	}

	@Test
	public void globalListenerShouldSurviveAttach() {
		MockScriptManager scriptManager = prepareScriptManager();

		api.evalCommand("A=(0,0)");
		api.evalCommand("C=(1,1)");
		api.evalCommand("Circle(C,1)");
		api.evalCommand("l: x = y");
		scriptManager.registerObjectUpdateListener("A", "onUpdate");
		scriptManager.registerUpdateListener("onUpdateGlobal");

		app.getKernel().getAlgoDispatcher().attach((GeoPointND) lookup("C"),
				(Path) lookup("l"), app.getActiveEuclidianView(), null);
		lookup("A").notifyUpdate();
		Mockito.verify(scriptManager, times(1))
				.evalJavaScript("onUpdate(\"A\");");
		Mockito.verify(scriptManager, times(1))
				.evalJavaScript("onUpdateGlobal(\"A\");");
	}

	private MockScriptManager prepareScriptManager() {
		MockScriptManager scriptManager = Mockito.spy(new MockScriptManager());
		app.getEventDispatcher().addEventListener(scriptManager);
		app.setScriptManager(scriptManager);
		return scriptManager;
	}

	@Test
	public void viewClicked2DTest() {
		app.getKernel().getAlgebraProcessor()
				.processAlgebraCommand("Polygon((0, 0), (20, 0), (0, -20))", false);

		ScriptManager scriptManager = prepareScriptManager();

		EuclidianController ec = app.getActiveEuclidianView().getEuclidianController();
		ec.wrapMousePressed(new TestEvent(300, 400));

		ArgumentCaptor<Event> eventCaptor = ArgumentCaptor.forClass(Event.class);

		Mockito.verify(scriptManager, times(2))
				.sendEvent(eventCaptor.capture());

		List<Event> capturedEvents = eventCaptor.getAllValues();
		assertEquals(2, capturedEvents.size());

		Event event = capturedEvents.get(0);
		assertEquals(EventType.MOUSE_DOWN, event.type);

		Map<String, Object> jsonArgument = event.jsonArgument;
		assertEquals(1.7d, jsonArgument.get("x"));
		assertEquals(-1.7d, jsonArgument.get("y"));

		String[] hits = (String[]) jsonArgument.get("hits");

		assertEquals(1, hits.length);
		assertEquals("t1", hits[0]);
	}

	@Test
	public void dragEnd2DTest() {
		app.getKernel().getAlgebraProcessor()
				.processAlgebraCommand("Polygon((0, 0), (20, 0), (0, -20))", false);

		ScriptManager scriptManager = prepareScriptManager();

		EuclidianController ec = app.getActiveEuclidianView().getEuclidianController();

		ec.setDraggingDelay(0);
		ec.wrapMousePressed(new TestEvent(300, 400));
		ec.wrapMouseDragged(new TestEvent(302, 402), true);
		ec.wrapMouseReleased(new TestEvent(302, 402));

		ArgumentCaptor<Event> eventCaptor = ArgumentCaptor.forClass(Event.class);

		Mockito.verify(scriptManager, Mockito.atLeast(1))
				.sendEvent(eventCaptor.capture());

		List<Event> capturedEvents = eventCaptor.getAllValues();

		int dragEndEvents = 0;
		for (Event event : capturedEvents) {
			if (event.type == EventType.DRAG_END) {
				assertEquals("t1", event.argument);
				dragEndEvents++;
			}
		}

		assertEquals(1, dragEndEvents);
	}

	@Test
	public void testGetValueString() {
		app.getLocalization().currentLocale = Locale.FRANCE;
		api.evalCommand("f(x) = If(x > 3, x, 3)");
		assertThat(api.getValueString("f", true), is("f(x) = Si(x > 3, x, 3)"));
		assertThat(api.getValueString("f", false), is("f(x) = If[x > 3, x, 3]"));
	}

	private class MockScriptManager extends ScriptManagerJre {
		public MockScriptManager() {
			super(GgbApiTest.this.app);
		}

		@Override
		protected void evalJavaScript(String jsFunction) {

		}
	}
}
