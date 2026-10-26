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

package org.geogebra.common.euclidian.tools;

import static org.geogebra.common.euclidian.EuclidianConstants.MODE_CIRCLE_ARC_THREE_POINTS;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.geogebra.common.kernel.geos.GeoConicPart;
import org.geogebra.common.kernel.kernelND.GeoConicNDConstants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CircularArcToolTest extends BaseToolTest {

	@BeforeEach
	void setMode() {
		setMode(MODE_CIRCLE_ARC_THREE_POINTS);
	}

	@Test
	void threeClicksCreateArc() {
		clickRW(2, -2);
		clickRW(2, 0);
		clickRW(0, 0);
		assertEquals(
				GeoConicNDConstants.CONIC_PART_ARC, ((GeoConicPart) lookup("c")).getConicPartType());
		checkContent("A = (2, -2)", "B = (2, 0)", "C = (0, 0)", "c = 1.5708");
	}
}
