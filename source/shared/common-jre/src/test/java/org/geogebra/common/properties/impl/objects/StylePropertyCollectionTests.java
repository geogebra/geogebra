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

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.geogebra.common.SuiteSubApp;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.properties.factory.GeoElementPropertiesFactory;
import org.geogebra.common.properties.impl.facade.ColorPropertyListFacade;
import org.geogebra.common.properties.impl.facade.NamedEnumeratedPropertyListFacade;
import org.geogebra.test.BaseAppTestSetup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class StylePropertyCollectionTests extends BaseAppTestSetup {

	private final GeoElementPropertiesFactory propertiesFactory = new GeoElementPropertiesFactory();

	@BeforeEach
	void setup() {
		setupApp(SuiteSubApp.GRAPHING);
	}

	@Test
	void testAngleSliderStyle() {
		GeoNumeric slider = evaluateGeoElement("Slider(0, 360, 1, 1, 100, true)");
		StylePropertyCollection stylePropertyCollection =
				assertDoesNotThrow(() -> new StylePropertyCollection(
						propertiesFactory, getLocalization(), List.of(slider)));
		assertTrue(stylePropertyCollection.isAvailable());
		assertEquals(2, stylePropertyCollection.getProperties().length);
		assertInstanceOf(ObjectColorProperty.class, ((ColorPropertyListFacade<?>)
				stylePropertyCollection.getProperties()[0]).getFirstProperty());
		assertInstanceOf(SliderOrientationProperty.class, ((NamedEnumeratedPropertyListFacade<?, ?>)
				stylePropertyCollection.getProperties()[1]).getFirstProperty());
	}
}
