package org.geogebra.common.euclidian.draw;

import static org.geogebra.common.awt.GPathIterator.SEG_CLOSE;
import static org.geogebra.common.awt.GPathIterator.SEG_CUBICTO;
import static org.geogebra.common.awt.GPathIterator.SEG_LINETO;
import static org.geogebra.common.awt.GPathIterator.SEG_MOVETO;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.awt.GGraphicsCommon;
import org.geogebra.common.awt.GPathIterator;
import org.geogebra.common.awt.GShape;
import org.geogebra.common.kernel.ConstructionDefaults;
import org.geogebra.common.kernel.geos.GeoConic;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class DrawConicTest extends BaseUnitTest {

	private GShape lastShape;

	@Before
	public void zoomIn() {
		add("ZoomIn(-5,-5,5,5)");
		getConstruction().getConstructionDefaults()
				.getDefaultGeo(ConstructionDefaults.DEFAULT_CONIC).setAlphaValue(1);
	}

	@Test
	public void testBigArcDrawingTriangleFill() {
		GeoConic circle = add("c=Circle((-50,-50),(-3,-3))");
		assertEquals(Arrays.asList(SEG_MOVETO, SEG_CUBICTO, SEG_LINETO, SEG_CLOSE),
				getSegmentTypes(circle));

		circle = add("c=Circle((-50,50),(-3,3))");
		assertEquals(Arrays.asList(SEG_MOVETO, SEG_CUBICTO, SEG_LINETO, SEG_CLOSE),
				getSegmentTypes(circle));

		circle = add("c=Circle((50,50),(3,3))");
		assertEquals(Arrays.asList(SEG_MOVETO, SEG_CUBICTO, SEG_LINETO, SEG_CLOSE),
				getSegmentTypes(circle));

		circle = add("c=Circle((50,-50),(3,-3))");
		assertEquals(Arrays.asList(SEG_MOVETO, SEG_CUBICTO, SEG_LINETO, SEG_CLOSE),
				getSegmentTypes(circle));
	}

	@Test
	public void testBigArcDrawingVerticalRectangleFill() {
		GeoConic circle = add("c=Circle((-50,0),(0,0))");
		assertEquals(Arrays.asList(SEG_MOVETO, SEG_CUBICTO, SEG_LINETO, SEG_LINETO, SEG_CLOSE),
				getSegmentTypes(circle));
				circle = add("c=Circle((50,0),(0,0))");
		assertEquals(Arrays.asList(SEG_MOVETO, SEG_CUBICTO, SEG_LINETO, SEG_LINETO, SEG_CLOSE),
				getSegmentTypes(circle));
	}

	@Test
	public void testBigArcDrawingHorizontalRectangleFill() {
		GeoConic circle = add("c=Circle((0,50),(0,0))");
		assertEquals(Arrays.asList(SEG_MOVETO, SEG_CUBICTO, SEG_LINETO, SEG_LINETO, SEG_CLOSE),
				getSegmentTypes(circle));
		circle = add("c=Circle((0,-50),(0,0))");
		assertEquals(Arrays.asList(SEG_MOVETO, SEG_CUBICTO, SEG_LINETO,  SEG_LINETO, SEG_CLOSE),
				getSegmentTypes(circle));
	}

	@Test
	public void testBigArcDrawingDiagonally() {
		GeoConic ellipse = add("c=Ellipse((-50,-50),(50,50),(50.1,50.1))");
		assertEquals(Arrays.asList(SEG_MOVETO, SEG_CUBICTO, SEG_LINETO, SEG_LINETO,
						SEG_CUBICTO, SEG_LINETO, SEG_CLOSE),
				getSegmentTypes(ellipse));
	}

	@Test

	public void testHugeHyperbola() {
		// almost parabolic, but focus far offscreen
		GeoConic hyperbola = add("Conic((-4.45,0),(4.45,0),"
				+ "(-0.536383065322,2.1925887111664),(2.5665707545687,1.4848398178468),"
				+ "(-1.168876600841,2.0713936021869))");

		assertEquals(List.of(
				600, 572, 545, 520, 495, 471, 448, 426, 405, 384, 365, 346, 329, 312,
				296, 281, 268, 254, 242, 231, 221, 211, 203, 195, 188, 182, 177, 173, 170, 168,
				166, 166, 166, 168, 170, 173, 177, 182, 188, 195, 203, 211, 221, 231, 242, 254,
				267, 281, 296, 312, 329, 346, 365, 384, 405, 426, 448, 471, 495, 520, 545, 572,
				599, 1081, 1081
		), getYCoordinates(hyperbola));
	}

	@Test
	public void hugeZoomTest() {
		double eps = 0.5e-8;
		double xm = 1.9827119206636958;
		double ym = 4.003436509728207;
		getApp().getActiveEuclidianView().setRealWorldCoordSystem(
				xm - eps, xm + eps, ym - eps, ym + eps);
		GeoConic ellipse = add("c:2 x^(2)+3 y^(2)-2 x+6 y=76");
		assertEquals(Arrays.asList(SEG_MOVETO, SEG_CUBICTO, SEG_LINETO, SEG_LINETO, SEG_CLOSE),
				getSegmentTypes(ellipse));
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

	private List<Integer> getYCoordinates(GeoConic circle) {
		DrawConic d = (DrawConic) getDrawable(circle);
		GGraphics2D g2 = Mockito.spy(new GGraphicsCommon());
		Mockito.doAnswer(invocation -> {
			lastShape = invocation.getArgument(0);
			return null;
		}).when(g2).draw(any());
		ArrayList<Integer> types = new ArrayList<>();
		d.draw(g2);
		GPathIterator iterator = lastShape.getPathIterator(null);
		double[] current = new double[6];
		while (!iterator.isDone()) {
			iterator.currentSegment(current);
			iterator.next();
			types.add((int) current[1]);
		}
		return types;
	}
}
