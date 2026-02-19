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

package org.geogebra.common.properties.impl.objects;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.geogebra.common.SuiteSubApp;
import org.geogebra.common.kernel.geos.GeoButton;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.properties.Property;
import org.geogebra.common.properties.factory.GeoElementPropertiesFactory;
import org.geogebra.common.properties.factory.PropertiesArray;
import org.geogebra.test.BaseAppTestSetup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class StylePropertiesTests extends BaseAppTestSetup {
	private final GeoElementPropertiesFactory propertiesFactory = new GeoElementPropertiesFactory();

	@BeforeEach
	public void setupApp() {
		setupApp(SuiteSubApp.GRAPHING);
	}

	@Test
	public void testPointStyleProperties() {
		GeoPoint zeroPoint = evaluateGeoElement("(0,0)");
		PropertiesArray propertiesArray = propertiesFactory.createStyleProperties(
				getAlgebraProcessor(), getApp().getImageManager(), getLocalization(),
				List.of(zeroPoint));
		List<String> styleProperties = Arrays.stream(propertiesArray.getProperties())
				.map(Property::getName).collect(Collectors.toList());
		assertEquals(List.of("Style"), styleProperties);
	}

	@Test
	public void testButtonStyleProperties() {
		GeoButton button = evaluateGeoElement("Button[]");
		PropertiesArray propertiesArray = propertiesFactory.createStyleProperties(
				getAlgebraProcessor(), getApp().getImageManager(), getLocalization(),
				List.of(button));
		List<String> styleProperties = Arrays.stream(propertiesArray.getProperties())
				.map(Property::getName).collect(Collectors.toList());
		assertEquals(List.of("Text", "Icon", "Background", "Size", "Filling"),
				styleProperties);
	}
}
