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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.geogebra.common.SuiteSubApp;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.properties.impl.objects.delegate.NotApplicablePropertyException;
import org.geogebra.test.BaseAppTestSetup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class VerticalStepPropertyTest extends BaseAppTestSetup {

	@BeforeEach
	public void setUp() {
		setupApp(SuiteSubApp.GRAPHING);
	}

	@Test
	public void testApplicable() {
		GeoElement point = evaluateGeoElement("(1, 1)");
		assertDoesNotThrow(() ->
				new VerticalStepProperty(getAlgebraProcessor(), getLocalization(), point));
	}

	@Test
	public void testNotApplicable() {
		GeoElement circle = evaluateGeoElement("Circle((0, 0), 1)");
		assertThrows(NotApplicablePropertyException.class, () ->
				new VerticalStepProperty(getAlgebraProcessor(), getLocalization(), circle));
	}

	@Test
	public void testNotApplicableForIntersectionPoint() {
		evaluateGeoElement("f = x - 3");
		GeoElement intersectionPoint = evaluateGeoElement("Intersect(f, (0,-3))");
		assertThrows(NotApplicablePropertyException.class, () ->
				new VerticalStepProperty(getAlgebraProcessor(), getLocalization(),
						intersectionPoint));
	}

	@Test
	public void testDisabledForLockedObject() {
		GeoElement point = evaluateGeoElement("(1, 1)");
		VerticalStepProperty VerticalStepProperty = assertDoesNotThrow(() ->
				new VerticalStepProperty(getAlgebraProcessor(), getLocalization(), point));
		point.setFixed(true);
		assertFalse(VerticalStepProperty.isEnabled());
		point.setFixed(false);
		assertTrue(VerticalStepProperty.isEnabled());
	}
}
