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

package org.geogebra.common.euclidian.modes;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.geogebra.common.euclidian.BaseEuclidianControllerTest;
import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.geos.GeoElement;
import org.junit.Test;

public class ModeShapeTest extends BaseEuclidianControllerTest {

	@Test
	public void shapeMaskTool() {
		setMode(EuclidianConstants.MODE_MASK);
		dragStart(50, 50);
		dragEnd(200, 150);
		checkContent("q1 = 6");
		GeoElement mask = getApp().getKernel().lookupLabel("q1");
		assertEquals(1, mask.getAlphaValue(), Kernel.MIN_PRECISION);
	}

	@Test
	public void maskShouldBeInFrontOfObjects() {
		setMode(EuclidianConstants.MODE_MASK);
		dragStart(50, 50);
		dragEnd(200, 150);
		setMode(EuclidianConstants.MODE_SHAPE_RECTANGLE);
		dragStart(50, 50);
		dragEnd(300, 150);
		// fill the shape rectangle
		add("SetFilling(q2, 100%)");
		click(100, 75);
		assertSelected(
				"Clicking intersection of object and mask should select mask",
				"q1");
		click(250, 75);
		assertSelected("Clicking outside mask should select object", "q2");
	}

	private void assertSelected(String message, String string) {
		List<GeoElement> selection = getApp().getSelectionManager()
				.getSelectedGeos();
		assertEquals(message, string, selection.get(0).getLabelSimple());
	}

}
