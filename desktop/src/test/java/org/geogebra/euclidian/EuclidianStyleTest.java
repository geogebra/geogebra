package org.geogebra.euclidian;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.kernel.ConstructionDefaults;
import org.geogebra.common.kernel.commands.CommandsTest;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.plugin.GeoClass;
import org.geogebra.desktop.headless.AppDNoGui;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class EuclidianStyleTest {
	private static AppDNoGui app;
	private static ConstructionDefaults cd;

	@BeforeClass
	public static void setupApp() {
		app = CommandsTest.createApp();
		cd = app.getKernel().getConstruction().getConstructionDefaults();
	}

	@Test
	public void textShouldBeTransparentOnReload() {
		GeoElement transparentText = t("trans=\"aaa\"");
		Assert.assertNull(transparentText.getBackgroundColor());
		GeoElement defaultText = cd
				.getDefaultGeo(cd.getDefaultType(null, GeoClass.TEXT));
		defaultText.setBackgroundColor(GColor.WHITE);
		GeoElement whiteText = t("\"aaa\"");
		Assert.assertEquals(whiteText.getBackgroundColor(), GColor.WHITE);
		app.setXML(app.getXML(), true);
		GeoElement transparentText2 = app.getKernel().lookupLabel("trans");
		Assert.assertNull(transparentText2.getBackgroundColor());
	}

	private static GeoElement t(String string) {
		// TODO Auto-generated method stub
		return (GeoElement) app.getKernel().getAlgebraProcessor()
				.processAlgebraCommand(string, false)[0];
	}
}
