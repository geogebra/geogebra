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
		Drawable drawable = (Drawable) getApp().getActiveEuclidianView().getDrawableFor(seg);
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
