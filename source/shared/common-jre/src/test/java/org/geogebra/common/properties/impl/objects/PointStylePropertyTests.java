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
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.geogebra.common.SuiteSubApp;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.plugin.EuclidianStyleConstants;
import org.geogebra.common.properties.impl.objects.PointStyleProperty.PointStyle;
import org.geogebra.common.properties.impl.objects.delegate.NotApplicablePropertyException;
import org.geogebra.test.BaseAppTestSetup;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class PointStylePropertyTests extends BaseAppTestSetup {
	@Test
	public void testApplicableGeoElement() {
		setupApp(SuiteSubApp.GRAPHING);
		assertDoesNotThrow(() -> new PointStyleProperty(
				getLocalization(), evaluateGeoElement("(1, 2)")));
	}

	@ParameterizedTest
	@ValueSource(strings = {
			"x",
			"x = 0",
			"5",
			"f: x",
	})
	public void testNotApplicableGeoElements(String expression) {
		setupApp(SuiteSubApp.GRAPHING);
		assertThrows(NotApplicablePropertyException.class, () -> new PointStyleProperty(
				getLocalization(), evaluateGeoElement(expression)));
	}

	@Test
	public void testSettingPointStyles() {
		setupApp(SuiteSubApp.GRAPHING);
		GeoPoint geoPoint = evaluateGeoElement("(1, 2)");
		PointStyleProperty pointStyleProperty = assertDoesNotThrow(() ->
				new PointStyleProperty(getLocalization(), geoPoint));

		pointStyleProperty.setValue(PointStyle.DOT);
		assertEquals(PointStyle.DOT, pointStyleProperty.getValue());
		assertEquals(EuclidianStyleConstants.POINT_STYLE_DOT, geoPoint.getPointStyle());

		pointStyleProperty.setValue(PointStyle.NO_OUTLINE);
		assertEquals(PointStyle.NO_OUTLINE, pointStyleProperty.getValue());
		assertEquals(EuclidianStyleConstants.POINT_STYLE_NO_OUTLINE, geoPoint.getPointStyle());

		pointStyleProperty.setValue(PointStyle.WEST_TRIANGLE);
		assertEquals(PointStyle.WEST_TRIANGLE, pointStyleProperty.getValue());
		assertEquals(EuclidianStyleConstants.POINT_STYLE_TRIANGLE_WEST, geoPoint.getPointStyle());
	}
}
