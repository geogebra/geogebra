package org.geogebra.common.euclidian.draw;

import static org.junit.Assert.assertEquals;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.euclidian.DrawAxis;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.junit.Test;

import com.himamis.retex.editor.share.util.Unicode;

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
}
