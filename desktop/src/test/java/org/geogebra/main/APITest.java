package org.geogebra.main;

import java.util.Locale;

import javax.swing.JFrame;

import org.geogebra.common.main.App;
import org.geogebra.desktop.CommandLineArguments;
import org.geogebra.desktop.geogebra3D.App3D;
import org.geogebra.desktop.plugin.GgbAPID;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class APITest {
	private static App3D app;
	private static GgbAPID api;

	@BeforeClass
	public static void setupApp() {
		app = new App3D(new CommandLineArguments(
				new String[] { "--prerelease" }), new JFrame(), false);
		app.setLanguage(Locale.US);
		api = app.getGgbApi();
		// Setting the general timeout to 11 seconds. Feel free to change this.
		app.getKernel().getApplication().getSettings().getCasSettings()
				.setTimeoutMilliseconds(11000);
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
		Assert.assertEquals(api.getGridVisible(), false);
		Assert.assertEquals(api.getGridVisible(1), false);
		api.setGridVisible(true);
		Assert.assertEquals(api.getGridVisible(), true);
		Assert.assertEquals(api.getGridVisible(1), true);
	}

	@Test
	public void testAxes() {
		api.evalCommand("SetVisibleInView[xAxis,1,true]");
		api.evalCommand("SetVisibleInView[yAxis,1,true]");
		Assert.assertEquals(api.getVisible("xAxis", 1), true);
		Assert.assertEquals(api.getVisible("yAxis", 1), true);


		api.evalCommand("SetVisibleInView[xAxis,1,false]");
		api.evalCommand("SetVisibleInView[yAxis,1,false]");
		Assert.assertEquals(api.getVisible("xAxis", 1), false);
		Assert.assertEquals(api.getVisible("yAxis", 1), false);

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
		Assert.assertEquals(app.showView(App.VIEW_ALGEBRA), false);
		String geometryXML = api.getPerspectiveXML();
		api.setPerspective("AG");
		Assert.assertEquals(app.showView(App.VIEW_ALGEBRA), true);
		api.setPerspective(geometryXML);
		Assert.assertEquals(app.showView(App.VIEW_ALGEBRA), false);
		Assert.assertEquals(app.showView(App.VIEW_EUCLIDIAN), true);
	}
}
