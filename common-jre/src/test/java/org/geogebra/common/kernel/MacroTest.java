package org.geogebra.common.kernel;

import static org.hamcrest.MatcherAssert.assertThat;

import java.util.Arrays;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.factories.AwtFactoryCommon;
import org.geogebra.common.gui.dialog.ToolCreationDialogModel;
import org.geogebra.common.jre.headless.LocalizationCommon;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.AppCommon3D;
import org.junit.Test;

public class MacroTest extends BaseUnitTest {

	@Override
	public AppCommon3D createAppCommon() {
		return new AppCommon3D(new LocalizationCommon(3),
				new AwtFactoryCommon());
	}

	@Test
	public void lineMacro() {
		GeoElement a = add("A=(1,1)");
		GeoElement b = add("B=(2,2)");
		GeoElement f = add("f=Line(A,B)");
		createMacro("TestLine", f, a, b);
		GeoElement g = add("g=TestLine((1,3),(2,3))");
		assertThat(g, hasValue("y = 3"));
	}

	@Test
	public void surfacesShouldWorkInMacros() {
		GeoElement a = add("A=(0,1,0)");
		add("f(u,v)=x(A)*u+y(A)*v");
		GeoElement s = add("s=Surface(2*f(u,v),3*f(u,v),4*f(u,v),u,0,1,v,0,1)");
		createMacro("TestSurface", s, a);
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
	public void curvesShouldWorkInMacros() {
		GeoElement a = add("A=(0,1,0)");
		add("f(u)=x(A)*u+y(A)");
		GeoElement s = add("s=Curve(2*f(u),3*f(u),4*f(u),u,0,1)");
		createMacro("TestCurve", s, a);
		add("B=(2,3,4)");
		add("C=(5,6,7)");
		GeoElement sb = add("TestCurve(B)");
		GeoElement sc = add("TestCurve(C)");
		assertThat(sb, hasValue("(2 (2 u + 3), 3 (2 u + 3), 4 (2 u + 3))"));
		assertThat(sc, hasValue("(2 (5 u + 6), 3 (5 u + 6), 4 (5 u + 6))"));
		getKernel().updateConstruction();
		assertThat(sb, hasValue("(2 (2 u + 3), 3 (2 u + 3), 4 (2 u + 3))"));
	}

	private void createMacro(String name, GeoElement output, GeoElement... input) {
		ToolCreationDialogModel macroBuilder = new ToolCreationDialogModel(getApp(),
				() -> {/* no UI to update */});
		Arrays.stream(input).forEach(macroBuilder::addToInput);
		macroBuilder.addToOutput(output);
		macroBuilder.createTool();
		macroBuilder.finish(getApp(), name, name, input.length + " inputs expected",
				false, null);
	}
}
