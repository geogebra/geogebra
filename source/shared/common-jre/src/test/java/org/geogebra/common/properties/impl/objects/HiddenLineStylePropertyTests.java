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
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.plugin.EuclidianStyleConstants;
import org.geogebra.common.properties.impl.objects.HiddenLineStyleProperty.HiddenLineStyle;
import org.geogebra.common.properties.impl.objects.delegate.NotApplicablePropertyException;
import org.geogebra.test.BaseAppTestSetup;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

public class HiddenLineStylePropertyTests extends BaseAppTestSetup {
	@Test
	public void testAvailableIn3D() {
		setupApp(SuiteSubApp.G3D);
		assertDoesNotThrow(() -> new HiddenLineStyleProperty(
				getLocalization(), evaluateGeoElement("Line((0, 0), (1, 1))")));
	}

	@ParameterizedTest
	@EnumSource(value = SuiteSubApp.class, names = {"GRAPHING", "GEOMETRY", "CAS"})
	public void testUnavailableInOtherApps(SuiteSubApp suiteSubApp) {
		setupApp(suiteSubApp);
		mockedCasGiac.memorize("Line((0, 0), (1, 1))", "y=x");
		assertThrows(NotApplicablePropertyException.class, () -> new HiddenLineStyleProperty(
				getLocalization(), evaluateGeoElement("Line((0, 0), (1, 1))")));
	}

	@Test
	public void testSettingHiddenLineStyle() {
		setupApp(SuiteSubApp.G3D);
		GeoElement geoElement = evaluateGeoElement("Line((0, 0), (1, 1))");
		HiddenLineStyleProperty hiddenLineStyleProperty = assertDoesNotThrow(() ->
				new HiddenLineStyleProperty(getLocalization(), geoElement));

		hiddenLineStyleProperty.setValue(HiddenLineStyle.DASHED);
		assertEquals(HiddenLineStyle.DASHED, hiddenLineStyleProperty.getValue());
		assertEquals(EuclidianStyleConstants.LINE_TYPE_HIDDEN_DASHED,
				geoElement.getLineTypeHidden());

		hiddenLineStyleProperty.setValue(HiddenLineStyle.UNCHANGED);
		assertEquals(HiddenLineStyle.UNCHANGED, hiddenLineStyleProperty.getValue());
		assertEquals(EuclidianStyleConstants.LINE_TYPE_HIDDEN_AS_NOT_HIDDEN,
				geoElement.getLineTypeHidden());

		hiddenLineStyleProperty.setValue(HiddenLineStyle.INVISIBLE);
		assertEquals(HiddenLineStyle.INVISIBLE, hiddenLineStyleProperty.getValue());
		assertEquals(EuclidianStyleConstants.LINE_TYPE_HIDDEN_NONE,
				geoElement.getLineTypeHidden());
	}
}
