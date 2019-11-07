package org.geogebra.common.euclidian;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.factories.AwtFactoryCommon;
import org.geogebra.common.jre.headless.LocalizationCommon;
import org.geogebra.common.kernel.ConstructionDefaults;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.main.AppCommon3D;
import org.geogebra.common.plugin.GeoClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class EuclidianStyleTest {
	private AppCommon3D app;
	private ConstructionDefaults cd;

	@Before
	public void setupApp() {
		app = new AppCommon3D(new LocalizationCommon(3),
				new AwtFactoryCommon());
		cd = app.getKernel().getConstruction().getConstructionDefaults();
	}

	@Test
	public void textShouldBeTransparentOnReload() {
		GeoElementND transparentText = t("trans=\"aaa\"");
		Assert.assertNull(transparentText.getBackgroundColor());
		GeoElement defaultText = cd
				.getDefaultGeo(cd.getDefaultType(null, GeoClass.TEXT));
		defaultText.setBackgroundColor(GColor.WHITE);
		GeoElementND whiteText = t("\"aaa\"");
		Assert.assertEquals(whiteText.getBackgroundColor(), GColor.WHITE);
		app.setXML(app.getXML(), true);
		GeoElement transparentText2 = app.getKernel().lookupLabel("trans");
		Assert.assertNull(transparentText2.getBackgroundColor());
	}

	private GeoElementND t(String string) {
		return app.getKernel().getAlgebraProcessor()
				.processAlgebraCommand(string, false)[0];
	}
}
