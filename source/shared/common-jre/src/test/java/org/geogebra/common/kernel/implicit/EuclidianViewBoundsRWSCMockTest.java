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

package org.geogebra.common.kernel.implicit;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class EuclidianViewBoundsRWSCMockTest {
	private final EuclidianViewBoundsRWSCMock bounds = new EuclidianViewBoundsRWSCMock(-5, 5,
			-5, 5, 800, 600);

	@ParameterizedTest
	@CsvSource({
			"-5, 0",
			"-2.5, 200",
			"0, 400",
			"2.5, 600",
			"5, 800"
	})
	void testToScreenCoordsX(double xRW, double x) {
		assertEquals(x, bounds.toScreenCoordXd(xRW));
	}

	@ParameterizedTest
	@CsvSource({
			"0, -5",
			"200, -2.5",
			"400, 0",
			"600, 2.5",
			"800, 5"
	})
	void testRealWorldCoordsX(double x, double xRW) {
		assertEquals(xRW , bounds.toRealWorldCoordX(x));
	}

	@ParameterizedTest
	@CsvSource({
			"-5, 0",
			"-2.5, 150",
			"0, 300",
			"2.5, 450",
			"5, 600"
	})
	void testToScreenCoordsY(double yRW, double y) {
		assertEquals(y, bounds.toScreenCoordYd(yRW));
	}

	@ParameterizedTest
	@CsvSource({
			"0, -5",
			"150, -2.5",
			"300, 0",
			"450, 2.5",
			"600, 5"
	})
	void testRealWorldCoordsY(double y, double yRW) {
		assertEquals(yRW , bounds.toRealWorldCoordY(y));
	}

}
