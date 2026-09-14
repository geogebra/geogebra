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

package org.geogebra.common.euclidian.plot.implicit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.geogebra.common.awt.GPoint2D;
import org.geogebra.common.awt.GRectangle2D;
import org.geogebra.common.euclidian.plot.implicit.classification.GraphBuilder;
import org.geogebra.common.euclidian.plot.implicit.classification.RegionClassifier;
import org.geogebra.common.euclidian.plot.implicit.classification.topology.HalfEdge;
import org.geogebra.common.euclidian.plot.implicit.classification.topology.PlanarGraph;
import org.geogebra.common.kernel.MyPoint;
import org.geogebra.common.kernel.implicit.PointList;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

@Disabled
@SuppressWarnings({"PMD.AvoidAccessibilityAlteration", "PMD.UseProperClassLoader"})
class RegionClassifierContourContourIntegrationTest {

	@Test
	void processShouldSplitCrossingContoursAndReuseSharedIntersectionVertex() throws Exception {
		PointList contour1 = pointList(new MyPoint(-1, -1), new MyPoint(1, 1),
				new MyPoint(2, 1));
		PointList contour2 = pointList(new MyPoint(-1, 1), new MyPoint(1, -1),
				new MyPoint(2, -1));

		RegionClassifier classifier = new RegionClassifier(List.of(contour1, contour2),
				RegionClassifierContourContourIntegrationTest::bounds,
				Collections::emptyList);

		assertTrue(classifier.process());
		PlanarGraph graph = extractGraph(classifier);

		int intersectionId = graph.findVertex(new GPoint2D(0, 0), 1e-12);
		assertTrue(intersectionId >= 0,
				"intersection vertex should exist at the shared split point");

		long outgoingContourEdges = graph.vertex(intersectionId).getOutgoingHalfEdges().stream()
				.map(graph::halfEdge)
				.filter(HalfEdge::isContourEdge)
				.count();
		assertEquals(4, outgoingContourEdges,
				"shared contour-contour intersection should reuse one vertex "
						+ "with four contour half-edges");
	}

	private static PlanarGraph extractGraph(RegionClassifier classifier) throws Exception {
		Field field = RegionClassifier.class.getDeclaredField("graphBuilder");
		field.setAccessible(true);
		GraphBuilder builder = (GraphBuilder) field.get(classifier);
		return builder.getGraph();
	}

	private static GRectangle2D bounds() {
		return (GRectangle2D) Proxy.newProxyInstance(
				GRectangle2D.class.getClassLoader(),
				new Class<?>[] {GRectangle2D.class},
				(proxy, method, args) -> switch (method.getName()) {
					case "getMinX", "getX" -> -2.0;
					case "getMinY", "getY" -> -2.0;
					case "getMaxX" -> 2.0;
					case "getMaxY" -> 2.0;
					case "getWidth" -> 4.0;
					case "getHeight" -> 4.0;
					case "intersects" -> false;
					default -> throw new UnsupportedOperationException(method.getName());
				});
	}

	private static PointList pointList(MyPoint start, MyPoint middle, MyPoint end)
			throws Exception {
		Constructor<PointList> constructor = PointList.class
				.getDeclaredConstructor(MyPoint.class, MyPoint.class);
		constructor.setAccessible(true);
		PointList pointList = constructor.newInstance(start, end);
		Field ptsField = PointList.class.getDeclaredField("pts");
		ptsField.setAccessible(true);
		LinkedList<MyPoint> pts = new LinkedList<>();
		pts.add(middle);
		pts.add(new MyPoint(middle.x, middle.y));
		ptsField.set(pointList, pts);
		return pointList;
	}
}
