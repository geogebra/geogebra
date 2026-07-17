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

package org.geogebra.common.properties.impl.graphics;

import static org.geogebra.common.plugin.EuclidianStyleConstants.LINE_TYPE_DASHED_SHORT;
import static org.geogebra.common.plugin.EuclidianStyleConstants.LINE_TYPE_DOTTED;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.geogebra.common.SuiteSubApp;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.test.BaseAppTestSetup;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class GridLineStylePropertyTests extends BaseAppTestSetup {
	@Test
	void testSettingValue() {
		setupApp(SuiteSubApp.GRAPHING);
		GridLineStyleProperty gridLineStyleProperty = new GridLineStyleProperty(getLocalization(),
				getEuclidianSettings());

		gridLineStyleProperty.setValue(LINE_TYPE_DASHED_SHORT);
		assertEquals(LINE_TYPE_DASHED_SHORT, gridLineStyleProperty.getValue());
		assertEquals(LINE_TYPE_DASHED_SHORT, getEuclidianSettings().getGridLineStyle());

		gridLineStyleProperty.setValue(LINE_TYPE_DOTTED);
		assertEquals(LINE_TYPE_DOTTED, gridLineStyleProperty.getValue());
		assertEquals(LINE_TYPE_DOTTED, getEuclidianSettings().getGridLineStyle());
	}

	@ParameterizedTest
	@ValueSource(ints = {
			EuclidianView.GRID_CARTESIAN,
			EuclidianView.GRID_CARTESIAN_WITH_SUBGRID,
			EuclidianView.GRID_ISOMETRIC,
			EuclidianView.GRID_POLAR,
	})
	void testGridTypesWithAvailableLineStyleProperty(int gridType) {
		setupApp(SuiteSubApp.GRAPHING);
		GridLineStyleProperty gridLineStyleProperty = new GridLineStyleProperty(
				getLocalization(), getEuclidianSettings());
		getEuclidianSettings().setGridType(gridType);

		assertTrue(gridLineStyleProperty.isAvailable());
	}

	@Test
	void testUnavailableLineStylePropertyForDotGridType() {
		setupApp(SuiteSubApp.GRAPHING);
		GridLineStyleProperty gridLineStyleProperty = new GridLineStyleProperty(
				getLocalization(), getEuclidianSettings());
		getEuclidianSettings().setGridType(EuclidianView.GRID_DOTS);

		assertFalse(gridLineStyleProperty.isAvailable());
	}
}
