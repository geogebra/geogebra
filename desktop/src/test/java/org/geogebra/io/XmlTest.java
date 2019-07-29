package org.geogebra.io;

import java.util.Locale;

import org.geogebra.common.io.XmlTestUtil;
import org.geogebra.common.kernel.commands.AlgebraProcessor;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.desktop.headless.AppDNoGui;
import org.geogebra.desktop.main.LocalizationD;
import org.geogebra.desktop.util.UtilD;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class XmlTest {

	static AppDNoGui app;
	private static AlgebraProcessor ap;

	@BeforeClass
	public static void setup() {
		app = new AppDNoGui(new LocalizationD(3), false);
		ap = app.getKernel().getAlgebraProcessor();
		app.setLanguage(Locale.US);
	}

	@Test
	public void emptyAppTest() {
		XmlTestUtil.testCurrentXML(app);
	}

	@Test
	public void pointReloadTest() {
		GeoElementND p = ap.processAlgebraCommand("P=(1,1)", true)[0];
		((GeoPoint) p).setAnimationStep(0.01);
		app.setXML(app.getXML(), true);
		Assert.assertEquals(0.01,
				app.getKernel().lookupLabel("P").getAnimationStep(), 1E-8);
	}

	@Test
	public void specialPointsLoadTest() {
		app.setXML(UtilD.loadFileIntoString(
				"src/test/resources/specialpoints.xml"), true);
		Assert.assertEquals(app.getGgbApi().getAllObjectNames().length, 20);
	}

}
