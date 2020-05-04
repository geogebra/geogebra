package org.geogebra.common.jre.main;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.factories.AwtFactory;
import org.geogebra.common.factories.AwtFactoryCommon;
import org.geogebra.common.jre.headless.AppCommon;
import org.geogebra.common.jre.headless.LocalizationCommon;
import org.geogebra.common.kernel.ConstructionDefaults;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.junit.Test;

public class TemplateHelperTest {

	@Test
	public void applyTemplateShouldChangeStyle() {
		AwtFactory awt = new AwtFactoryCommon();
		AppCommon templateApp = new AppCommon(new LocalizationCommon(3), awt);
		AppCommon contentApp = new AppCommon(new LocalizationCommon(3), awt);
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
