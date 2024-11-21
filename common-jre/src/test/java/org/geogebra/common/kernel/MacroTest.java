package org.geogebra.common.kernel;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;

import java.util.Arrays;

import org.geogebra.common.AppCommonFactory;
import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.gui.dialog.ToolCreationDialogModel;
import org.geogebra.common.jre.headless.AppCommon;
import org.geogebra.common.kernel.geos.GeoCurveCartesian;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.main.AppCommon3D;
import org.geogebra.test.annotation.Issue;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class MacroTest extends BaseUnitTest {
	private AppCommon macroApp;
	private Kernel macroKernel;

	@Before
	public void macroSetup() {
		macroApp = createAppCommon();
		macroKernel = macroApp.getKernel();
	}

	@Override
	public AppCommon3D createAppCommon() {
		return AppCommonFactory.create3D();
	}

	@Test
	public void lineMacro() {
		GeoElement a = add("A=(1,1)");
		GeoElement b = add("B=(2,2)");
		GeoElement f = add("f=Line(A,B)");
		createMacro(getApp(), "TestLine", f, a, b);
		GeoElement g = add("g=TestLine((1,3),(2,3))");
		assertThat(g, hasValue("y = 3"));
	}

	@Test
	@Issue("APPS-1496")
	public void surfacesShouldWorkInMacros() {
		GeoElement a = add("A=(0,1,0)");
		add("f(u,v)=x(A)*u+y(A)*v");
		GeoElement s = add("s=Surface(2*f(u,v),3*f(u,v),4*f(u,v),u,0,1,v,0,1)");
		createMacro(getApp(), "TestSurface", s, a);
		add("B=(2,3,4)");
		add("C=(5,6,7)");
		GeoElement sb = add("TestSurface(B)");
		GeoElement sc = add("TestSurface(C)");
		assertThat(sb, hasValue("(2 (2 u + 3 v), 3 (2 u + 3 v), 4 (2 u + 3 v))"));
		assertThat(sc, hasValue("(2 (5 u + 6 v), 3 (5 u + 6 v), 4 (5 u + 6 v))"));
		getKernel().updateConstruction();
		assertThat(sb, hasValue("(2 (2 u + 3 v), 3 (2 u + 3 v), 4 (2 u + 3 v))"));
	}

	@Test
	@Issue("APPS-1496")
	public void curvesShouldWorkInMacros() {
		GeoElement a = add("A=(0,1,0)");
		add("f(u)=x(A)*u+y(A)");
		GeoElement s = add("s=Curve(2*f(u),3*f(u),4*f(u),u,0,1)");
		createMacro(getApp(), "TestCurve", s, a);
		add("B=(2,3,4)");
		add("C=(5,6,7)");
		GeoElement sb = add("TestCurve(B)");
		GeoElement sc = add("TestCurve(C)");
		assertThat(sb, hasValue("(2 (2 u + 3), 3 (2 u + 3), 4 (2 u + 3))"));
		assertThat(sc, hasValue("(2 (5 u + 6), 3 (5 u + 6), 4 (5 u + 6))"));
		getKernel().updateConstruction();
		assertThat(sb, hasValue("(2 (2 u + 3), 3 (2 u + 3), 4 (2 u + 3))"));
	}

	private AppCommon getMacroApp() {
		return macroApp;
	}

	private Kernel getMacroKernel() {
		return macroKernel;
	}

	protected <T extends GeoElementND> T addMacroCommand(String command) {
		GeoElementND[] geoElements = getMacroKernel().getAlgebraProcessor()
				.processAlgebraCommand(command, false);
		return geoElements.length > 0 ? (T) geoElements[0] : null;
	}

	@Test
	public void testMacroEditing() {
		// test openEditMacro
		GeoElement a = addMacroCommand("A=(1,2)");
		GeoElement b = addMacroCommand("B=(3,4)");
		GeoElement f = addMacroCommand("f=Line(A,B)");
		String macroName1 = "LineMacro1";
		createMacro(getMacroApp(), macroName1, f, a, b);
		getMacroApp().openEditMacro(getMacroKernel().getMacro(macroName1));

		Macro editMacro = getMacroApp().getEditMacro();
		GeoElement[] input = editMacro.getMacroInput();
		GeoElement[] output = editMacro.getMacroOutput();
		Assert.assertEquals(2, input.length);
		Assert.assertEquals(1, output.length);
		Assert.assertEquals("A", input[0].getLabel(StringTemplate.defaultTemplate));
		Assert.assertEquals(1, input[0].getLabelPosition().getX(), .001);
		Assert.assertEquals(2, input[0].getLabelPosition().getY(), .001);
		Assert.assertEquals("B", input[1].getLabel(StringTemplate.defaultTemplate));
		Assert.assertEquals(3, input[1].getLabelPosition().getX(), .001);
		Assert.assertEquals(4, input[1].getLabelPosition().getY(), .001);
		Assert.assertEquals("f", output[0].getLabel(StringTemplate.defaultTemplate));
		StringBuilder sb = new StringBuilder();
		editMacro.getXML(sb);
		String xml1 = sb.toString();
		assertThat(xml1, containsString("label=\"A\""));
		assertThat(xml1, containsString("label=\"B\""));
		assertThat(xml1, containsString("label=\"f\""));

		// test addMacroXML
		GeoElement c = addMacroCommand("C=(5,6)");
		GeoElement d = addMacroCommand("D=(7,8)");
		GeoElement g = addMacroCommand("g=Line(C,D)");
		String macroName2 = "LineMacro2";
		createMacro(getMacroApp(), macroName2, g, c, d);
		getMacroKernel().getMacro(macroName2).getXML(sb);
		String xml2 = sb.toString();
		getMacroApp().removeAllMacros();
		getMacroApp().addMacroXML(xml1);
		getMacroApp().addMacroXML(xml2);
		String allXML = getMacroApp().getAllMacrosXML();
		assertThat(allXML, containsString("label=\"A\""));
		assertThat(allXML, containsString("label=\"B\""));
		assertThat(allXML, containsString("label=\"f\""));
		assertThat(allXML, containsString("label=\"C\""));
		assertThat(allXML, containsString("label=\"D\""));
		assertThat(allXML, containsString("label=\"g\""));
	}

	@Test
	@Issue("APPS-5988")
	public void macroWithParametricCurves() {
		GeoElement angle = add("angle=0");
		GeoElement width = add("width=10");
		GeoElement length = add("length=30");
		GeoElement pt = add("Pt=(0,-11)");
		add("edge1=Curve(Pt + 0.5 * (width;angle+90deg) + (t;angle),t,-length / 2, length/2)");
		add("edge2=Curve(Pt + 0.5 * (width;angle-90deg) + (t;angle),t,-length / 2, length/2)");
		GeoElement out = add("Curve(If(0 ≤ t ≤ length, edge1(t - 0.5length), length ≤ t ≤ 2length,"
				+ " edge2(length * 1.5 - t)), t, 0, length * 2)");
		createMacro(getApp(), "rect", out, pt, angle, width, length);
		getKernel().clearConstruction(true);
		GeoCurveCartesian gc1 = add("rect((1,1),2,3,4)");
		GeoCurveCartesian gc2 = add("rect((1,1),2,5,6)");
		assertThat(gc1, hasValue("(If(0 ≤ t ≤ 4, 1 + 0.5cos(2 + 90°) * 3 + cos(2) (t - 0.5 * 4),"
				+ " 4 ≤ t ≤ 2 * 4, 1 + 0.5cos(2 - 90°) * 3 + cos(2) (4 * 1.5 - t)), "
				+ "If(0 ≤ t ≤ 4, 1 + 0.5sin(2 + 90°) * 3 + sin(2) (t - 0.5 * 4), 4 ≤ t ≤ 2 * 4,"
				+ " 1 + 0.5sin(2 - 90°) * 3 + sin(2) (4 * 1.5 - t)))"));
		assertThat(gc2, hasValue("(If(0 ≤ t ≤ 6, 1 + 0.5cos(2 + 90°) * 5 + cos(2) (t - 0.5 * 6), "
				+ "6 ≤ t ≤ 2 * 6, 1 + 0.5cos(2 - 90°) * 5 + cos(2) (6 * 1.5 - t)),"
				+ " If(0 ≤ t ≤ 6, 1 + 0.5sin(2 + 90°) * 5 + sin(2) (t - 0.5 * 6), 6 ≤ t ≤ 2 * 6, "
				+ "1 + 0.5sin(2 - 90°) * 5 + sin(2) (6 * 1.5 - t)))"));
		gc1.update();
		// verify that all used variables are in main construction rather than macro construction
		gc1.getFun(0).getFunctionExpression().inspect(val -> {
			if (val instanceof GeoElement) {
				assertThat(((GeoElement) val).getConstruction(), equalTo(getConstruction()));
			}
			return false;
		});
	}

	private void createMacro(AppCommon app, String name, GeoElement output, GeoElement... input) {
		ToolCreationDialogModel macroBuilder = new ToolCreationDialogModel(app,
				() -> {/* no UI to update */});
		Arrays.stream(input).forEach(macroBuilder::addToInput);
		macroBuilder.addToOutput(output);
		macroBuilder.createTool();
		macroBuilder.finish(app, name, name, input.length + " inputs expected",
				false, null);
	}
}
