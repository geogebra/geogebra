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

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.kernel.geos.GeoElement;
import org.junit.jupiter.api.Test;

class ShowInAVPropertyTest extends BaseUnitTest {

	@Test
	void testIsEnabledWhenHiddenInGraphics() {
		GeoElement element = getElementFactory().createGeoLine();
		element.setEuclidianVisible(false);

		ShowInAVProperty property = new ShowInAVProperty(getLocalization(), element);
		assertTrue(property.isEnabled(), "property should be available when hidden in graphics");
	}

	@Test
	void testListRoundTrip() {
		GeoElement list = addAvInput("{(1,2),(3,4)}");
		ShowInAVProperty property = new ShowInAVProperty(getLocalization(), list);
		property.setValue(false);
		assertFalse(property.getValue(), "object should not be selectable");
		property.setValue(true);
		assertTrue(property.getValue(), "object should be selectable");
	}
}
