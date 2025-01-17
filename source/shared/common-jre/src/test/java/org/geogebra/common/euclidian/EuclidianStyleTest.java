package org.geogebra.common.euclidian;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;

import org.geogebra.common.AppCommonFactory;
import org.geogebra.common.awt.GColor;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.ConstructionDefaults;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoLine;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.main.AppCommon3D;
import org.geogebra.common.plugin.GeoClass;
import org.junit.Before;
import org.junit.Test;

public class EuclidianStyleTest {
	private AppCommon3D app;
	private ConstructionDefaults cd;
	private Construction construction;

	@Before
	public void setupApp() {
		app = AppCommonFactory.create3D();
		construction = app.getKernel().getConstruction();
		cd = construction.getConstructionDefaults();
	}

	@Test
	public void textShouldBeTransparentOnReload() {
		GeoElementND transparentText = t("trans=\"aaa\"");
		assertNull(transparentText.getBackgroundColor());
		GeoElement defaultText = cd
				.getDefaultGeo(cd.getDefaultType(null, GeoClass.TEXT));
		defaultText.setBackgroundColor(GColor.WHITE);
		GeoElementND whiteText = t("\"aaa\"");
		assertEquals(GColor.WHITE, whiteText.getBackgroundColor());
		app.setXML(app.getXML(), true);
		GeoElement transparentText2 = app.getKernel().lookupLabel("trans");
		assertNull(transparentText2.getBackgroundColor());
	}

	@Test
	public void linePropertiesShouldApplyToNewGeo() {
		EuclidianController ec = app.getActiveEuclidianView().getEuclidianController();
		EuclidianStyleBarSelection selection = new EuclidianStyleBarSelection(app, ec);
		app.setMode(EuclidianConstants.MODE_JOIN);
		selection.updateDefaultsForMode(EuclidianConstants.MODE_JOIN);
		ArrayList<GeoElement> geos = selection.getGeos();
		EuclidianStyleBarStatic.applyColor(GColor.GREEN, 1, app, geos);
		EuclidianStyleBarStatic.applyLineStyle(1, 5, app, geos);
		assertEquals(GColor.GREEN, new GeoLine(construction).getObjectColor());
		assertEquals(5, new GeoLine(construction).getLineThickness());
	}

	private GeoElementND t(String string) {
		return app.getKernel().getAlgebraProcessor()
				.processAlgebraCommand(string, false)[0];
	}
}
