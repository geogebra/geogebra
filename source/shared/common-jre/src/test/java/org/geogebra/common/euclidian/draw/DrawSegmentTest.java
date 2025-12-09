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

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.awt.GGraphicsCommon;
import org.geogebra.common.awt.GShape;
import org.geogebra.common.euclidian.Drawable;
import org.geogebra.common.kernel.geos.GProperty;
import org.geogebra.common.kernel.geos.GeoSegment;
import org.geogebra.common.kernel.geos.SegmentStyle;
import org.geogebra.test.OrderingComparison;
import org.junit.Test;

public class DrawSegmentTest extends BaseUnitTest {
	private int counter;

	@Test
	public void allStylesShouldBePaintedToGraphics() {
		GeoSegment seg = add("Segment((0,0),(1,1))");
		Drawable drawable = getDrawable(seg);
		GGraphicsCommon g2d = new GGraphicsCommon() {
			@Override
			public void fill(GShape sh) {
				super.fill(sh);
				counter++;
			}
		};
		for (SegmentStyle start: SegmentStyle.values()) {
			for (SegmentStyle end: SegmentStyle.values()) {
				seg.setStartStyle(start);
				seg.setEndStyle(end);
				seg.updateVisualStyle(GProperty.COMBINED);
				counter = 0;
				drawable.draw(g2d);
				if (start.ordinal() + end.ordinal() > 0) {
					assertThat(counter, OrderingComparison.greaterThan(0));
				} else {
					assertThat(counter, equalTo(0));
				}
			}
		}
	}
}
