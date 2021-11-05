package org.geogebra.common.jre.main;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import org.geogebra.common.AppCommonFactory;
import org.geogebra.common.awt.GColor;
import org.geogebra.common.jre.headless.AppCommon;
import org.geogebra.common.kernel.ConstructionDefaults;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.junit.Test;

public class TemplateHelperTest {

	@Test
	public void applyTemplateShouldChangeStyle() {
		AppCommon templateApp = AppCommonFactory.create3D();
		AppCommon contentApp = AppCommonFactory.create3D();
		Kernel templateKernel = templateApp.getKernel();
		GeoElement pointTemplate = templateKernel.getConstruction()
				.getConstructionDefaults().getDefaultGeo(ConstructionDefaults.DEFAULT_POINT_FREE);
		pointTemplate.setObjColor(GColor.PURPLE);
		GeoElement pointContent = new GeoPoint(contentApp.getKernel().getConstruction());
		pointContent.setLabel(null);
		assertNotEquals(GColor.PURPLE, pointContent.getObjectColor());
		new TemplateHelper(contentApp).applyTemplate(templateApp);
		assertEquals(GColor.PURPLE, pointContent.getObjectColor());
	}
}
