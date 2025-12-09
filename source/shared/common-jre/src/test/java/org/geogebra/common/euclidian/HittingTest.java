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

package org.geogebra.common.euclidian;

import static org.junit.Assert.assertEquals;

import org.geogebra.common.euclidian.event.PointerEventType;
import org.junit.Test;

public class HittingTest extends BaseEuclidianControllerTest {

	@Test
	public void polygonTool() {
		add("p1=Polygon((0, 0), (2, 0),(2, -2), (0, -2))");
		add("p2=Polygon((2, 0), (4, 0),(4, -2), (2, -2))");
		for (String segment: getApp().getGgbApi().getAllObjectNames("segment")) {
			getApp().getGgbApi().setFixed(segment, true, false);
		}
		setMode(EuclidianConstants.MODE_MOVE);
		click(96, 50, PointerEventType.TOUCH);
		assertEquals("p1", getSelectedLabel());
		click(104, 50, PointerEventType.TOUCH);
		assertEquals("p2", getSelectedLabel());
	}

	private String getSelectedLabel() {
		return getApp().getSelectionManager().getSelectedGeos().get(0).getLabelSimple();
	}
}
