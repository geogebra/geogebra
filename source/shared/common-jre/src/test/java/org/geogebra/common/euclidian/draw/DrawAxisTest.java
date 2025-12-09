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

package org.geogebra.common.euclidian.draw;

import static org.junit.Assert.assertEquals;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.euclidian.DrawAxis;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.editor.share.util.Unicode;
import org.junit.Test;

public class DrawAxisTest extends BaseUnitTest {
	@Test
	public void testDegreeLabelsWithPi() {
		EuclidianView view = getApp().getActiveEuclidianView();
		GeoNumberValue distance = add(Unicode.PI_STRING);
		view.getSettings().setAxisNumberingDistance(0, distance);
		assertEquals("3" + Unicode.PI_STRING,
				DrawAxis.tickDescription(view, 3, 0));
	}

	@Test
	public void testDegreeLabelsContainNoPi() {
		EuclidianView view = getApp().getActiveEuclidianView();
		GeoNumberValue distance = add("60deg");
		view.getSettings().setAxisNumberingDistance(0, distance);
		assertEquals("180" + Unicode.DEGREE_STRING,
				DrawAxis.tickDescription(view, 3, 0));
	}

	@Test
	public void testDecimalDescription() {
		EuclidianView view = getApp().getActiveEuclidianView();
		GeoNumberValue distance = add("0.3");
		view.getSettings().setAxisNumberingDistance(0, distance);
		assertEquals("0.9",
				DrawAxis.tickDescription(view, 3, 0));
	}

	@Test
	public void testFractionDescription() {
		EuclidianView view = getApp().getActiveEuclidianView();
		GeoNumberValue distance = add("3/10");
		view.getSettings().setAxisNumberingDistance(0, distance);
		assertEquals("9 / 10",
				DrawAxis.tickDescription(view, 3, 0));
	}
}
