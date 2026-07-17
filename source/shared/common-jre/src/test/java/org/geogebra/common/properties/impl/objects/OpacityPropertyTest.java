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

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.properties.impl.objects.delegate.NotApplicablePropertyException;
import org.junit.jupiter.api.Test;

class OpacityPropertyTest extends BaseUnitTest {

	@Test
	void testConstructorForPolygon() {
		addAvInput("A = (0, 0)");
		addAvInput("B = (1, 1)");
		addAvInput("C = (0, 1)");
		GeoElement polygon = addAvInput("Polygon(A,B,C)");
		try {
			new OpacityProperty(getLocalization(), polygon);
		} catch (NotApplicablePropertyException e) {
			fail(e.getMessage());
		}
	}

	@Test
	void testConstructorForSlider() {
		GeoElement slider = addAvInput("1");
		slider.setEuclidianVisible(true);
		assertThrows(NotApplicablePropertyException.class,
				() -> new OpacityProperty(getLocalization(), slider));
	}

	@Test
	void testConstructorForPoint() {
		GeoElement point = addAvInput("(1,2)");
		assertThrows(NotApplicablePropertyException.class,
				() -> new OpacityProperty(getLocalization(), point));
	}
}
