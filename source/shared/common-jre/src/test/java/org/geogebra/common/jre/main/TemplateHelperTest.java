/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 * 
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 * 
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

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
