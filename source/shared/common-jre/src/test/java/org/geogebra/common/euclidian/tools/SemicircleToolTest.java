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

import static org.geogebra.common.euclidian.EuclidianConstants.MODE_SEMICIRCLE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.geogebra.common.kernel.geos.GeoConicPart;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.kernelND.GeoConicNDConstants;
import org.geogebra.editor.share.util.Unicode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SemicircleToolTest extends BaseToolTest {

	@BeforeEach
	void setMode() {
		setMode(MODE_SEMICIRCLE);
	}

	@Test
	void twoClicksCreateSemicircle() {
		clickRW(2, -2);
		clickRW(2, 0);
		assertEquals(
				GeoConicNDConstants.CONIC_PART_ARC, ((GeoConicPart) lookup("c")).getConicPartType());
		checkContent("A = (2, -2)", "B = (2, 0)", "c = " + Unicode.pi);
	}

	@Test
	void draggingCreatesSemicircle() {
		dragRW(2, -2, 2, 0);
		assertEquals(
				GeoConicNDConstants.CONIC_PART_ARC, ((GeoConicPart) lookup("c")).getConicPartType());
		checkContent("A = (2, -2)", "B = (2, 0)", "c = " + Unicode.pi);
	}

	@Test
	void reversingExistingEndpointsChangesHalfPlane() {
		add("A = (1, -2)");
		add("B = (5, -2)");
		clickRW(1, -2);
		clickRW(5, -2);
		GeoConicPart upper = (GeoConicPart) lookup("c");
		GeoPoint above = new GeoPoint(getApp().getKernel().getConstruction(), 3, 0, 1);
		GeoPoint below = new GeoPoint(getApp().getKernel().getConstruction(), 3, -4, 1);
		assertTrue(upper.isOnPath(above, 1e-8));
		assertFalse(upper.isOnPath(below, 1e-8));
		clickRW(5, -2);
		clickRW(1, -2);
		GeoConicPart lower = (GeoConicPart) lookup("d");
		assertTrue(lower.isOnPath(below, 1e-8));
		assertFalse(lower.isOnPath(above, 1e-8));
	}
}
