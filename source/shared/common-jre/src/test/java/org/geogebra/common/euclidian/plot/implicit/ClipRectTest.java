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

package org.geogebra.common.euclidian.plot.implicit;

import static org.geogebra.common.euclidian.plot.implicit.BaseContourTestSetup.newBounds;
import static org.geogebra.common.euclidian.plot.implicit.BernsteinPlotterSettings.MARGIN_IN_PX;
import static org.geogebra.common.kernel.Kernel.MAX_DOUBLE_PRECISION;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.geogebra.common.euclidian.plot.interval.EuclidianViewBounds;
import org.geogebra.common.kernel.arithmetic.BoundsRectangle;
import org.junit.jupiter.api.Test;

class ClipRectTest {

	@Test
	void shouldExpandBoundsByPixelMarginScaledToWorldUnits() {
		EuclidianViewBounds bounds = newBounds(
				-14.900000000000029, 9.220000000000018, -24.190000000000026, 1.110000000000001, 1206, 1265);
		ClipRect clipRect = new ClipRect(bounds, MARGIN_IN_PX);
		double mx = bounds.getInvXscale() * MARGIN_IN_PX;
		double my = bounds.getInvYscale() * MARGIN_IN_PX;
		assertAll(
				() -> assertEquals(bounds.getXmin() - mx, clipRect.getXmin(), MAX_DOUBLE_PRECISION),
				() -> assertEquals(bounds.getXmax() + mx, clipRect.getXmax(), MAX_DOUBLE_PRECISION),
				() -> assertEquals(bounds.getYmin() - my, clipRect.getYmin(), MAX_DOUBLE_PRECISION),
				() -> assertEquals(bounds.getYmax() + my, clipRect.getYmax(), MAX_DOUBLE_PRECISION));
	}

	@Test
	void testDependencyOfClipAndBoundsRectangle() {
		EuclidianViewBounds bounds = newBounds(
				-14.900000000000029, 9.220000000000018, -24.190000000000026, 1.110000000000001, 1206, 1265);
		ClipRect clipRect = new ClipRect(bounds, MARGIN_IN_PX);
		double mx = bounds.getInvXscale() * MARGIN_IN_PX;
		double my = bounds.getInvYscale() * MARGIN_IN_PX;
		BoundsRectangle boundsRectangle = new BoundsRectangle(bounds, mx, my);
		assertAll(
				() -> assertEquals(clipRect.getXmin(), boundsRectangle.getXmin()),
				() -> assertEquals(clipRect.getXmax(), boundsRectangle.getXmax()),
				() -> assertEquals(clipRect.getYmin(), boundsRectangle.getYmin()),
				() -> assertEquals(clipRect.getYmax(), boundsRectangle.getYmax()));
	}
}
