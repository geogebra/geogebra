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

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.geogebra.common.SuiteSubApp;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.properties.aliases.StringProperty;
import org.geogebra.test.BaseAppTestSetup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class DimensionRatioPropertyTest extends BaseAppTestSetup {

	@BeforeEach
	public void setup() {
		setupApp(SuiteSubApp.GRAPHING);
	}

	@Test
	public void testGetRatio() {
		EuclidianView view = getApp().getEuclidianView1();
		DimensionRatioProperty property = new DimensionRatioProperty(getLocalization(), view);
		StringProperty xRatio = (StringProperty) property.getProperties()[0];
		StringProperty yRatio = (StringProperty) property.getProperties()[1];

		// Default: equal scales → both show "1"
		assertEquals("1", xRatio.getValue());
		assertEquals("1", yRatio.getValue());

		// xscale > yscale: x is the larger scale, shows "1"; y shows the ratio
		view.setCoordSystem(view.getXZero(), view.getYZero(), 200, 100);
		assertEquals("1", xRatio.getValue());
		assertEquals("2", yRatio.getValue());

		// xscale < yscale: y is the larger scale, shows "1"; x shows the ratio
		view.setCoordSystem(view.getXZero(), view.getYZero(), 100, 200);
		assertEquals("2", xRatio.getValue());
		assertEquals("1", yRatio.getValue());
	}

	@Test
	public void testSetRatio() {
		EuclidianView view = getApp().getEuclidianView1();
		DimensionRatioProperty property = new DimensionRatioProperty(getLocalization(), view);
		StringProperty yRatio = (StringProperty) property.getProperties()[1];

		yRatio.setValue("4");
		assertEquals("4", yRatio.getValue());
	}
}
