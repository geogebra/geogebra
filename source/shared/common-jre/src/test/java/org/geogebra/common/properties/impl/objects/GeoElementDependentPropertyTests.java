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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.geogebra.common.SuiteSubApp;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.util.Box;
import org.geogebra.test.BaseAppTestSetup;
import org.junit.jupiter.api.Test;

class GeoElementDependentPropertyTests extends BaseAppTestSetup {

	@Test
	void testElementRedefinition() {
		setupApp(SuiteSubApp.GRAPHING);

		GeoElement penStroke = evaluateGeoElement("PenStroke((1, 2), (4, 3), (5, 6))");
		PenStrokeAbsolutePositionProperty absolutePositionProperty = assertDoesNotThrow(() ->
				new PenStrokeAbsolutePositionProperty(getLocalization(), penStroke));
		Box<GeoElement> redefinedTo = new Box<>(null);
		absolutePositionProperty.addRedefinitionObserver(
				(originalElement, newElement) -> redefinedTo.value = newElement);

		assertEquals(penStroke, absolutePositionProperty.getGeoElement());
		assertFalse(absolutePositionProperty.getValue());
		assertNull(redefinedTo.value);

		absolutePositionProperty.setValue(true);

		assertNotEquals(penStroke, absolutePositionProperty.getGeoElement());
		assertTrue(absolutePositionProperty.getValue());
		assertNotNull(redefinedTo);
	}
}
