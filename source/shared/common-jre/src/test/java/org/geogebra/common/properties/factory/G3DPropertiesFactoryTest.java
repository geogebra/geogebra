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
 
package org.geogebra.common.properties.factory;

import static org.geogebra.common.properties.factory.DefaultPropertiesFactoryTest.getNames;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.geogebra.common.AppCommonFactory;
import org.geogebra.common.jre.headless.AppCommon;
import org.geogebra.common.main.PreviewFeature;
import org.geogebra.common.main.settings.config.AppConfigGraphing3D;
import org.geogebra.test.BaseAppTestSetup;
import org.junit.jupiter.api.Test;

public class G3DPropertiesFactoryTest extends BaseAppTestSetup {

	@Test
	public void testPropertiesSuite3D() {
		PreviewFeature.enablePreviewFeatures = true;
		AppCommon app = AppCommonFactory.create3D(new AppConfigGraphing3D());
		suiteScope.registerApp(app);

		app.setActiveView(AppCommon.VIEW_EUCLIDIAN);
		List<PropertiesArray> props = new G3DPropertiesFactory().createProperties(
				app, app.getLocalization(), null);
		assertEquals(3, props.size());
		assertEquals(List.of("Grid", "Axes", "xAxis", "yAxis", "zAxis", "Projection",
						"Advanced"),
				getNames(props.get(2)));
		PreviewFeature.enablePreviewFeatures = false;
	}
}
