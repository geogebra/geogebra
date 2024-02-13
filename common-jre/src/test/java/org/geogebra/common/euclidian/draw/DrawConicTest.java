package org.geogebra.common.euclidian.draw;

import static org.geogebra.common.awt.GPathIterator.SEG_CUBICTO;
import static org.geogebra.common.awt.GPathIterator.SEG_LINETO;
import static org.geogebra.common.awt.GPathIterator.SEG_MOVETO;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.awt.GPathIterator;
import org.geogebra.common.kernel.ConstructionDefaults;
import org.geogebra.common.kernel.geos.GeoConic;
import org.junit.Before;
import org.junit.Test;

public class DrawConicTest extends BaseUnitTest {

	@Before
	public void zoomIn() {
		add("ZoomIn(-5,-5,5,5)");
		getConstruction().getConstructionDefaults()
				.getDefaultGeo(ConstructionDefaults.DEFAULT_CONIC).setAlphaValue(1);
	}

	@Test
	public void testBigArcDrawingTriangleFill() {
		GeoConic circle = add("c=Circle((-50,-50),(-3,-3))");
		assertEquals(Arrays.asList(SEG_MOVETO, SEG_LINETO, SEG_CUBICTO), getSegmentTypes(circle));

		circle = add("c=Circle((-50,50),(-3,3))");
		assertEquals(Arrays.asList(SEG_MOVETO, SEG_LINETO, SEG_CUBICTO), getSegmentTypes(circle));

		circle = add("c=Circle((50,50),(3,3))");
		assertEquals(Arrays.asList(SEG_MOVETO, SEG_LINETO, SEG_CUBICTO), getSegmentTypes(circle));

		circle = add("c=Circle((50,-50),(3,-3))");
		assertEquals(Arrays.asList(SEG_MOVETO, SEG_LINETO, SEG_CUBICTO), getSegmentTypes(circle));
	}

	@Test
	public void testBigArcDrawingVerticalRectangleFill() {
		GeoConic circle = add("c=Circle((-50,0),(0,0))");
		assertEquals(Arrays.asList(SEG_MOVETO, SEG_LINETO, SEG_CUBICTO, SEG_LINETO),
				getSegmentTypes(circle));
				circle = add("c=Circle((50,0),(0,0))");
		assertEquals(Arrays.asList(SEG_MOVETO, SEG_LINETO, SEG_CUBICTO, SEG_LINETO),
				getSegmentTypes(circle));
	}

	@Test
	public void testBigArcDrawingHorizontalRectangleFill() {
		GeoConic circle = add("c=Circle((0,50),(0,0))");
		assertEquals(Arrays.asList(SEG_MOVETO, SEG_LINETO, SEG_CUBICTO, SEG_LINETO),
				getSegmentTypes(circle));
		circle = add("c=Circle((0,-50),(0,0))");
		assertEquals(Arrays.asList(SEG_MOVETO, SEG_LINETO, SEG_CUBICTO, SEG_LINETO),
				getSegmentTypes(circle));
	}

	private List<Integer> getSegmentTypes(GeoConic circle) {
		DrawConic d = (DrawConic) getDrawable(circle);
		ArrayList<Integer> types = new ArrayList<>();
		GPathIterator iterator = d.fillShape.getPathIterator(null);
		double[] current = new double[6];
		while (!iterator.isDone()) {
			int type = iterator.currentSegment(current);
			iterator.next();
			types.add(type);
		}
		return types;
	}
}
