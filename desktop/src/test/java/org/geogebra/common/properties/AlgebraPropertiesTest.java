package org.geogebra.common.properties;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.commands.AlgebraTest;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.properties.impl.general.AngleUnitProperty;
import org.geogebra.common.properties.impl.general.RoundingProperty;
import org.geogebra.desktop.headless.AppDNoGui;
import org.geogebra.test.TestStringUtil;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class AlgebraPropertiesTest {

	private static AppDNoGui app;

	@BeforeClass
	public static void setup() {
		app = AlgebraTest.createApp();
	}

	private static void t(String s) {
		app.getKernel().getAlgebraProcessor().processAlgebraCommand(s, true);
	}

	@Before
	public void clean() {
		app.getKernel().clearConstruction(true);
		app.setRounding("2");
		app.getKernel().setAngleUnit(Kernel.ANGLE_DEGREE);
	}

	@Test
	public void roundingSHouldUpdateAV() {
		RoundingProperty rp = new RoundingProperty(app, app.getLocalization());
		t("a=1/3");
		t("b=4*a");
		valueTextShouldBe("a", "a = 0.33");
		valueTextShouldBe("b", "b = 1.33");
		rp.setIndex(5);
		valueTextShouldBe("a", "a = 0.33333");
		valueTextShouldBe("b", "b = 1.33333");
	}

	@Test
	public void angleUnitShouldUpdateAV() {
		AngleUnitProperty rp = new AngleUnitProperty(app.getKernel(),
				app.getLocalization());
		t("a=90deg");
		t("b=Angle(xAxis,yAxis)");
		valueTextShouldBe("a", "a = 90deg");
		valueTextShouldBe("b", "b = 90deg");
		rp.setIndex(1);
		valueTextShouldBe("a", "a = 1.57 rad");
		valueTextShouldBe("b", "b = 1.57 rad");
	}

	private static void valueTextShouldBe(String label, String expectedValue) {
		Assert.assertEquals(TestStringUtil.unicode(expectedValue),
				get(label).getAlgebraDescriptionDefault());
	}

	private static GeoElement get(String string) {
		return app.getKernel().lookupLabel(string);

	}
}
