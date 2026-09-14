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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.geogebra.common.awt.AwtFactory;
import org.geogebra.common.awt.GArea;
import org.geogebra.common.awt.GGeneralPath;
import org.geogebra.common.awt.GRectangle;
import org.geogebra.common.awt.GRectangle2D;
import org.geogebra.common.euclidian.plot.implicit.classification.ClassifiedRegion;
import org.geogebra.common.euclidian.plot.implicit.classification.GraphBuilder;
import org.geogebra.common.euclidian.plot.implicit.classification.RegionClassifier;
import org.geogebra.common.euclidian.plot.implicit.classification.topology.BoundaryCycle;
import org.geogebra.common.euclidian.plot.implicit.classification.topology.EpsilonPolicy;
import org.geogebra.common.euclidian.plot.implicit.classification.topology.Face;
import org.geogebra.common.euclidian.plot.implicit.classification.topology.HalfEdge;
import org.geogebra.common.euclidian.plot.implicit.classification.topology.PlanarGraph;
import org.geogebra.common.euclidian.plot.implicit.classification.topology.Vertex;
import org.geogebra.common.euclidian.plot.interval.EuclidianViewBounds;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunctionNVar;
import org.geogebra.common.kernel.implicit.EuclidianViewBoundsRWSCMock;
import org.junit.jupiter.api.Test;

@SuppressWarnings("PMD.JUnitJupiterTestShouldBePackagePrivate")
public class RegionClassifierStageATest extends BaseContourTestSetup {

	@Test
	void clippedFragmentsShouldBuildNonEmptyGraphFromRuntimePipeline() {
		ContourInfo info = setupCassiniWithFourIntersectionOnTop();
		PerimeterContourClipper clipper = createClipper(info);
		RegionClassifier classifier = new RegionClassifier(clipper::getClippedFragmentsResult,
				() -> rectangleFrom(info.getBounds()));

		assertTrue(classifier.process());
		assertFalse(classifier.getGraph().isEmpty(),
				"graph should contain viewport and contour edges");
	}

	@Test
	void openViewportFragmentShouldProduceValidLinksAndFaces() {
		GeoElement curve = evaluateGeoElement("x^3 + y^3 = 0");
		ContourInfo info = builder.withImplicitCurve(curve)
				.withBounds(-10, 10, -10, 10, 1237, 1265)
				.build();
		PerimeterContourClipper clipper = createClipper(info);
		RegionClassifier classifier = new RegionClassifier(clipper::getClippedFragmentsResult,
				() -> rectangleFrom(info.getBounds()));

		assertTrue(classifier.process());
		PlanarGraph graph = classifier.getGraph();
		assertTrue(graph.hasValidLinks(),
				"fragment-native graph should produce valid half-edge links");
		assertTrue(graph.hasValidFaces(),
				"fragment-native graph should produce a consistent face structure");
	}

	@Test
	void resetShouldAllowReprocessingWithoutStaleGraphState() {
		ContourInfo info = setupCassiniWithFourIntersectionOnTop();
		PerimeterContourClipper clipper = createClipper(info);
		RegionClassifier classifier = new RegionClassifier(clipper::getClippedFragmentsResult,
				() -> rectangleFrom(info.getBounds()));

		assertTrue(classifier.process());
		int firstFaceCount = classifier.getGraph().getFaces().size();

		classifier.reset();

		assertTrue(classifier.process());
		assertEquals(firstFaceCount, classifier.getGraph().getFaces().size());
	}

	@Test
	void bottomLeftClippedCassiniShouldSampleBoundedFaceNearContour() {
		EuclidianViewBounds bounds = newBounds(-1.5, 28, -2, 32, 1200, 1600);
		GeoElement cassini = addCassini(2.9, 2.98);
		ContourInfo info = builder.withImplicitCurve(cassini)
				.withBounds(bounds)
				.build();
		PerimeterContourClipper clipper = createClipper(info);
		RegionClassifier classifier = new RegionClassifier(clipper::getClippedFragmentsResult,
				() -> rectangleFrom(info.getBounds()));

		assertTrue(classifier.process());
		PlanarGraph graph = classifier.getGraph();
		Face boundedFace = graph.getFaces().stream()
				.filter(face -> !face.isExterior())
				.findFirst()
				.orElseThrow();
		assertNotNull(boundedFace.getSamplePoint());
		assertTrue(boundedFace.getSamplePoint().x < 5,
				"Clipped bounded Cassini face should sample near the visible contour,"
						+ " not the far viewport");
		assertTrue(boundedFace.getSamplePoint().y < 2,
				"Clipped bounded Cassini face should sample near the visible contour,"
						+ " not the far viewport");
	}

	@Test
	void leftClippedCassiniShouldProduceBoundedFace() {
		EuclidianViewBounds bounds = newBounds(-0.861, 35.645, -26.28, 5.624, 1408, 1254);
		GeoElement cassini = addCassini(2.8, 2.7);
		ContourInfo info = builder.withImplicitCurve(cassini)
				.withBounds(bounds)
				.build();
		PerimeterContourClipper clipper = createClipper(info);
		RegionClassifier classifier = new RegionClassifier(clipper::getClippedFragmentsResult,
				() -> rectangleFrom(info.getBounds()));

		assertTrue(classifier.process());
		PlanarGraph graph = classifier.getGraph();
		long boundedFaceCount = graph.getFaces().stream()
				.filter(face -> !face.isExterior())
				.count();
		assertTrue(boundedFaceCount >= 1,
				"Left-clipped Cassini should still produce a bounded clipped interior face");
	}

	@Test
	void clippedCassiniShouldSampleNearVisibleContourNotFarViewport() {
		EuclidianViewBounds bounds = newBounds(0.772, 37.279, -9.851, 22.663, 1408, 1254);
		GeoElement cassini = addCassini(2.8, 2.7);
		ContourInfo info = builder.withImplicitCurve(cassini)
				.withBounds(bounds)
				.build();
		PerimeterContourClipper clipper = createClipper(info);
		RegionClassifier classifier = new RegionClassifier(clipper::getClippedFragmentsResult,
				() -> rectangleFrom(info.getBounds()));

		assertTrue(classifier.process());
		Face boundedFace = classifier.getGraph().getFaces().stream()
				.filter(face -> !face.isExterior())
				.findFirst()
				.orElseThrow();
		assertNotNull(boundedFace.getSamplePoint());
		assertTrue(boundedFace.getSamplePoint().x < 6,
				"Clipped bounded Cassini face should sample near the visible contour,"
						+ " not the far viewport");
		assertTrue(boundedFace.getSamplePoint().y < 3,
				"Clipped bounded Cassini face should sample near the visible contour,"
						+ " not the far viewport");
	}

	@Test
	void clippedCassiniNearViewportCornerShouldStillProduceBoundedFace() {
		EuclidianViewBounds bounds = newBounds(-34.36, 2.146, -33.108, -0.595, 1408, 1254);
		GeoElement cassini = addCassini(2.8, 2.7);
		ContourInfo info = builder.withImplicitCurve(cassini)
				.withBounds(bounds)
				.build();
		PerimeterContourClipper clipper = createClipper(info);
		RegionClassifier classifier = new RegionClassifier(clipper::getClippedFragmentsResult,
				() -> rectangleFrom(info.getBounds()));

		assertTrue(classifier.process());
		long boundedFaceCount = classifier.getGraph().getFaces().stream()
				.filter(face -> !face.isExterior())
				.count();
		assertTrue(boundedFaceCount >= 1,
				"Clipped Cassini near the viewport corner should still produce"
						+ " a bounded clipped interior face");
	}

	@Test
	void clippedCassiniNearViewportCornerShouldSampleBoundedFace() {
		EuclidianViewBounds bounds = newBounds(-34.36, 2.146, -33.108, -0.595, 1408, 1254);
		GeoElement cassini = addCassini(2.8, 2.7);
		ContourInfo info = builder.withImplicitCurve(cassini)
				.withBounds(bounds)
				.build();
		PerimeterContourClipper clipper = createClipper(info);
		RegionClassifier classifier = new RegionClassifier(clipper::getClippedFragmentsResult,
				() -> rectangleFrom(info.getBounds()));

		assertTrue(classifier.process());
		Face boundedFace = classifier.getGraph().getFaces().stream()
				.filter(face -> !face.isExterior())
				.findFirst()
				.orElseThrow();
		assertNotNull(boundedFace.getSamplePoint(),
				"Clipped Cassini near the viewport corner should still find a sample point");
	}

	@Test
	void clippedCassiniNearViewportCornerShouldNotLoseBoundedFaceForAlternateCornerView() {
		EuclidianViewBounds bounds = newBounds(-2.858, 33.908, -30.529, -0.53, 1418, 1157);
		GeoElement cassini = addCassini(2.8, 2.7);
		ContourInfo info = builder.withImplicitCurve(cassini)
				.withBounds(bounds)
				.build();
		PerimeterContourClipper clipper = createClipper(info);
		RegionClassifier classifier = new RegionClassifier(clipper::getClippedFragmentsResult,
				() -> rectangleFrom(info.getBounds()));

		assertTrue(classifier.process());
		long boundedFaceCount = classifier.getGraph().getFaces().stream()
				.filter(face -> !face.isExterior())
				.count();
		assertTrue(boundedFaceCount >= 1,
				"Alternate corner-clipped Cassini view should still produce"
						+ " a bounded clipped interior face");
	}

	@Test
	void clippedCassiniNearViewportCornerShouldNotLoseBoundedFaceForWideCornerView() {
		EuclidianViewBounds bounds = newBounds(-39.696, 0.746, -33.974, -0.976, 1418, 1157);
		GeoElement cassini = addCassini(2.8, 2.7);
		ContourInfo info = builder.withImplicitCurve(cassini)
				.withBounds(bounds)
				.build();
		PerimeterContourClipper clipper = createClipper(info);
		RegionClassifier classifier = new RegionClassifier(clipper::getClippedFragmentsResult,
				() -> rectangleFrom(info.getBounds()));

		assertTrue(classifier.process());
		long boundedFaceCount = classifier.getGraph().getFaces().stream()
				.filter(face -> !face.isExterior())
				.count();
		assertTrue(boundedFaceCount >= 1,
				"Wide corner-clipped Cassini view should still produce"
						+ " a bounded clipped interior face");
	}

	@Test
	void clippedCassiniNearViewportCornerShouldNotLoseBoundedFaceForTopWideCornerView() {
		EuclidianViewBounds bounds = newBounds(-2.077, 38.365, 1.277, 34.276, 1418, 1157);
		GeoElement cassini = addCassini(2.8, 2.7);
		ContourInfo info = builder.withImplicitCurve(cassini)
				.withBounds(bounds)
				.build();
		PerimeterContourClipper clipper = createClipper(info);
		RegionClassifier classifier = new RegionClassifier(clipper::getClippedFragmentsResult,
				() -> rectangleFrom(info.getBounds()));

		assertTrue(classifier.process());
		long boundedFaceCount = classifier.getGraph().getFaces().stream()
				.filter(face -> !face.isExterior())
				.count();
		assertTrue(boundedFaceCount >= 1,
				"Top wide corner-clipped Cassini view should still produce"
						+ " a bounded clipped interior face");
	}

	@Test
	void freshLoadCassiniViewportShouldKeepVisibleBoundedRegionFilled() {
		EuclidianViewBounds bounds = newAppLikeBounds(-22.890, 1.490, -22.790, 0.130,
				1219, 1146);
		GeoFunctionNVar cassini = (GeoFunctionNVar) evaluate(
				"(x^(2)+y^(2))^(2)-2*2.7^(2)*(x^(2)-y^(2))-(2.8^(4)-2.7^(4))<=0")[0];
		GeoElement border = cassini.getIneqs().getIneq().getImplicitCurveBorder();
		ContourInfo info = builder.withImplicitCurve(border)
				.withBounds(bounds)
				.build();
		PerimeterContourClipper clipper = createClipper(info);
		RegionClassifier classifier = new RegionClassifier(clipper::getClippedFragmentsResult,
				() -> rectangleFrom(info.getBounds()));

		assertTrue(classifier.process(),
				"Fresh-load Cassini viewport should classify successfully");
		PlanarGraph graph = classifier.getGraph();
		long boundedFaceCount = graph.getFaces().stream()
				.filter(face -> !face.isExterior())
				.count();
		assertTrue(boundedFaceCount >= 1,
				"Fresh-load Cassini viewport should still produce a bounded clipped face");
		Face boundedFace = graph.getFaces().stream()
				.filter(face -> !face.isExterior())
				.findFirst()
				.orElseThrow();
		assertNotNull(boundedFace.getSamplePoint(),
				"Fresh-load Cassini viewport should still find a bounded face sample point");

		List<ClassifiedRegion> results = classifier.getResults(info.getBounds());
		for (ClassifiedRegion region : results) {
			region.setFilled(
					cassini.isInRegion(region.getSamplePoint().x, region.getSamplePoint().y));
		}
		long filledBounded = results.stream()
				.filter(region -> region.isFilled() && region.getHoles().isEmpty())
				.count();
		assertTrue(filledBounded >= 1,
				"Fresh-load Cassini viewport should keep the visible bounded"
						+ " Cassini region filled: "
						+ describeRegions(results)
						+ " extracted="
						+ describeCycles(graph, graph.getLastExtractedBoundaryCycles())
						+ " canonical="
						+ describeCycles(graph, graph.getLastCanonicalBoundaryCycles()));
	}

	@Test
	void freshLoadCassiniViewportShouldNotDropAllCanonicalCycles() {
		EuclidianViewBounds bounds = newAppLikeBounds(-25.944, -0.011, -19.868, 0.625,
				1690, 1254);
		GeoFunctionNVar cassini = (GeoFunctionNVar) evaluate(
				"(x^(2)+y^(2))^(2)-2*2.7^(2)*(x^(2)-y^(2))-(2.8^(4)-2.7^(4))<=0")[0];
		GeoElement border = cassini.getIneqs().getIneq().getImplicitCurveBorder();
		ContourInfo info = builder.withImplicitCurve(border)
				.withBounds(bounds)
				.build();
		PerimeterContourClipper clipper = createClipper(info);
		RegionClassifier classifier = new RegionClassifier(clipper::getClippedFragmentsResult,
				() -> rectangleFrom(info.getBounds()));

		assertTrue(classifier.process(),
				"Fresh-load Cassini viewport should classify successfully");
		PlanarGraph graph = classifier.getGraph();
		assertFalse(graph.getLastCanonicalBoundaryCycles().isEmpty(),
				"Fresh-load Cassini viewport should not drop all canonical cycles: extracted="
						+ describeCycles(graph, graph.getLastExtractedBoundaryCycles())
						+ " canonical="
						+ describeCycles(graph, graph.getLastCanonicalBoundaryCycles()));
	}

	@Test
	void freshLoadCassiniViewportShouldNotCollapseWhenLobeSitsOnScreenEdge() {
		EuclidianViewBounds bounds = newAppLikeBounds(-23.898, -0.237, -30.515, -0.685,
				909, 1146);
		GeoFunctionNVar cassini = (GeoFunctionNVar) evaluate(
				"(x^(2)+y^(2))^(2)-2*2.7^(2)*(x^(2)-y^(2))-(2.8^(4)-2.7^(4))<=0")[0];
		GeoElement border = cassini.getIneqs().getIneq().getImplicitCurveBorder();
		ContourInfo info = builder.withImplicitCurve(border)
				.withBounds(bounds)
				.build();
		PerimeterContourClipper clipper = createClipper(info);
		RegionClassifier classifier = new RegionClassifier(clipper::getClippedFragmentsResult,
				() -> rectangleFrom(info.getBounds()));

		assertTrue(classifier.process(),
				"Edge-threshold Cassini viewport should classify successfully");
		PlanarGraph graph = classifier.getGraph();
		long boundedFaceCount = graph.getFaces().stream()
				.filter(face -> !face.isExterior())
				.count();
		assertTrue(boundedFaceCount >= 1,
				"Fresh-load Cassini edge-threshold viewport should not collapse to exterior only: "
						+ "extracted="
						+ describeCycles(graph, graph.getLastExtractedBoundaryCycles())
						+ " canonical="
						+ describeCycles(graph, graph.getLastCanonicalBoundaryCycles()));
	}

	@Test
	void freshLoadCassiniViewportShouldNotCollapseForStableTopRightBand() {
		EuclidianViewBounds bounds = newAppLikeBounds(-11.447, -0.944, -14.334, -1.092,
				909, 1146);
		GeoFunctionNVar cassini = (GeoFunctionNVar) evaluate(
				"(x^(2)+y^(2))^(2)-2*2.7^(2)*(x^(2)-y^(2))-(2.8^(4)-2.7^(4))<=0")[0];
		GeoElement border = cassini.getIneqs().getIneq().getImplicitCurveBorder();
		ContourInfo info = builder.withImplicitCurve(border)
				.withBounds(bounds)
				.build();
		PerimeterContourClipper clipper = createClipper(info);
		RegionClassifier classifier = new RegionClassifier(clipper::getClippedFragmentsResult,
				() -> rectangleFrom(info.getBounds()));

		assertTrue(classifier.process(),
				"Stable top-right Cassini band viewport should classify successfully");
		PlanarGraph graph = classifier.getGraph();
		long boundedFaceCount = graph.getFaces().stream()
				.filter(face -> !face.isExterior())
				.count();
		assertTrue(boundedFaceCount >= 1,
				"Stable top-right Cassini band should not collapse to exterior only: extracted="
						+ describeCycles(graph, graph.getLastExtractedBoundaryCycles())
						+ " canonical="
						+ describeCycles(graph, graph.getLastCanonicalBoundaryCycles()));
	}

	@Test
	void freshLoadCassiniViewportShouldNotCollapseForShiftedTopRightBand() {
		EuclidianViewBounds bounds = newAppLikeBounds(-12.383, -0.793, -15.744, -0.976,
				1003, 1278);
		GeoFunctionNVar cassini = (GeoFunctionNVar) evaluate(
				"(x^(2)+y^(2))^(2)-2*2.7^(2)*(x^(2)-y^(2))-(2.8^(4)-2.7^(4))<=0")[0];
		GeoElement border = cassini.getIneqs().getIneq().getImplicitCurveBorder();
		ContourInfo info = builder.withImplicitCurve(border)
				.withBounds(bounds)
				.build();
		PerimeterContourClipper clipper = createClipper(info);
		RegionClassifier classifier = new RegionClassifier(clipper::getClippedFragmentsResult,
				() -> rectangleFrom(info.getBounds()));

		assertTrue(classifier.process(),
				"Shifted top-right Cassini band viewport should classify successfully");
		PlanarGraph graph = classifier.getGraph();
		long boundedFaceCount = graph.getFaces().stream()
				.filter(face -> !face.isExterior())
				.count();
		assertTrue(boundedFaceCount >= 1,
				"Shifted top-right Cassini band should not collapse to exterior only: extracted="
						+ describeCycles(graph, graph.getLastExtractedBoundaryCycles())
						+ " canonical="
						+ describeCycles(graph, graph.getLastCanonicalBoundaryCycles()));
	}

	private String describeRegions(List<ClassifiedRegion> regions) {
		StringBuilder builder = new StringBuilder();
		builder.append('[');
		for (int i = 0; i < regions.size(); i++) {
			ClassifiedRegion region = regions.get(i);
			if (i > 0) {
				builder.append(", ");
			}
			GRectangle bounds = region.getOuterBoundary().getBounds();
			builder.append("{filled=").append(region.isFilled())
					.append(", holes=").append(region.getHoles().size())
					.append(", sample=").append(region.getSamplePoint())
					.append(", bounds=").append(bounds)
					.append('}');
		}
		builder.append(']');
		return builder.toString();
	}

	private String describeCycles(PlanarGraph graph, List<BoundaryCycle> cycles) {
		StringBuilder builder = new StringBuilder();
		builder.append('[');
		for (int i = 0; i < cycles.size(); i++) {
			BoundaryCycle cycle = cycles.get(i);
			if (i > 0) {
				builder.append(", ");
			}
			GRectangle bounds = cycleBounds(graph, cycle);
			builder.append("{id=").append(cycle.getId())
					.append(", area=").append(cycle.getSignedArea())
					.append(", abs=").append(cycle.getAbsArea())
					.append(", edges=").append(cycle.getHalfEdgeIds().size())
					.append(", bounds=").append(bounds)
					.append('}');
		}
		builder.append(']');
		return builder.toString();
	}

	private String describeFaces(PlanarGraph graph) {
		StringBuilder builder = new StringBuilder();
		builder.append('[');
		for (int i = 0; i < graph.getFaces().size(); i++) {
			Face face = graph.getFaces().get(i);
			if (i > 0) {
				builder.append(", ");
			}
			builder.append("{id=").append(face.getId())
					.append(", exterior=").append(face.isExterior())
					.append(", outer=").append(face.getOuterHalfEdgeId())
					.append(", holes=").append(face.getHoleHalfEdgeIds())
					.append(", sample=").append(face.getSamplePoint())
					.append('}');
		}
		builder.append(']');
		return builder.toString();
	}

	private GRectangle cycleBounds(PlanarGraph graph, BoundaryCycle cycle) {
		double minX = Double.POSITIVE_INFINITY;
		double maxX = Double.NEGATIVE_INFINITY;
		double minY = Double.POSITIVE_INFINITY;
		double maxY = Double.NEGATIVE_INFINITY;
		for (int halfEdgeId : cycle.getHalfEdgeIds()) {
			HalfEdge edge = graph.halfEdge(halfEdgeId);
			Vertex origin = graph.vertex(edge.getOriginVertexId());
			minX = Math.min(minX, origin.getX());
			maxX = Math.max(maxX, origin.getX());
			minY = Math.min(minY, origin.getY());
			maxY = Math.max(maxY, origin.getY());
		}
		GRectangle rect = AwtFactory.getPrototype().newRectangle();
		rect.setRect((int) Math.floor(minX), (int) Math.floor(minY),
				(int) Math.ceil(maxX - minX), (int) Math.ceil(maxY - minY));
		return rect;
	}

	private static EuclidianViewBounds newAppLikeBounds(double xmin, double xmax, double ymin,
			double ymax, int width, int height) {
		return new EuclidianViewBoundsRWSCMock(xmin, xmax, ymin, ymax, width, height) {
			@Override
			public double toScreenCoordYd(double yRW) {
				double yScale = 1.0 / getInvYscale();
				return getYZero() - (yRW * yScale);
			}

			@Override
			public double toRealWorldCoordY(double y) {
				return (getYZero() - y) * getInvYscale();
			}
		};
	}

	@Test
	void clippedCassiniNearViewportCornerShouldClassifyUsingVisibleSamplePoints() {
		EuclidianViewBounds bounds = newBounds(-2.077, 38.365, 1.277, 34.276, 1418, 1157);
		GeoFunctionNVar cassini = (GeoFunctionNVar) evaluate(
				"(x^(2)+y^(2))^(2)-2*2.7^(2)*(x^(2)-y^(2))-(2.8^(4)-2.7^(4))<=0")[0];
		GeoElement border = cassini.getIneqs().getIneq().getImplicitCurveBorder();
		ContourInfo info = builder.withImplicitCurve(border)
				.withBounds(bounds)
				.build();
		PerimeterContourClipper clipper = createClipper(info);
		RegionClassifier classifier = new RegionClassifier(clipper::getClippedFragmentsResult,
				() -> rectangleFrom(info.getBounds()));

		assertTrue(classifier.process());
		List<ClassifiedRegion> results = classifier.getResults(info.getBounds());
		for (ClassifiedRegion region : results) {
			region.setFilled(
					cassini.isInRegion(region.getSamplePoint().x, region.getSamplePoint().y));
		}
		long filledBounded = results.stream()
				.filter(region -> region.isFilled() && region.getHoles().isEmpty())
				.count();
		assertTrue(filledBounded >= 1,
				"Top wide corner-clipped Cassini should still classify"
						+ " a visible bounded region as filled");
	}

	@Test
	void halfPlaneViewportShouldKeepBothSidesOfDiagonalAtLargePannedView() {
		EuclidianViewBounds bounds = newAppLikeBounds(-28.82163760797805, 29.70083729770597,
				-17.81372538329036, 24.75424650286259, 1377, 1002);
		GeoFunctionNVar halfPlane = (GeoFunctionNVar) evaluate("x^3 <= y^3")[0];
		GeoElement border = halfPlane.getIneqs().getIneq().getImplicitCurveBorder();
		ContourInfo info = builder.withImplicitCurve(border)
				.withBounds(bounds)
				.build();
		PerimeterContourClipper clipper = createClipper(info);
		RegionClassifier classifier = new RegionClassifier(clipper::getClippedFragmentsResult,
				() -> rectangleFrom(info.getBounds()));

		boolean processed = classifier.process();
		String failureDetail = "";
		if (!processed) {
			try {
				GraphBuilder builder = new GraphBuilder(EpsilonPolicy.defaults());
				builder.build(rectangleFrom(info.getBounds()), clipper.getClippedFragmentsResult());
			} catch (IllegalStateException e) {
				failureDetail = " topologyError=" + e.getMessage();
			}
		}
		assertTrue(processed,
				"Half-plane viewport should classify successfully." + failureDetail);
		PlanarGraph graph = classifier.getGraph();
		List<ClassifiedRegion> results = classifier.getResults(info.getBounds());
		for (ClassifiedRegion region : results) {
			region.setFilled(halfPlane.isInRegion(region.getSamplePoint().x,
					region.getSamplePoint().y));
		}

		long filledCount = results.stream().filter(ClassifiedRegion::isFilled).count();
		long unfilledCount = results.size() - filledCount;
		assertTrue(filledCount >= 1 && unfilledCount >= 1,
				"Clipped half-plane should keep both sides of the diagonal after panning left: "
						+ describeRegions(results)
						+ " extracted="
						+ describeCycles(graph, graph.getLastExtractedBoundaryCycles())
						+ " canonical="
						+ describeCycles(graph, graph.getLastCanonicalBoundaryCycles()));
	}

	@Test
	void cubicProductInequalityShouldKeepBothFilledQuadrantsWhenZoomedOut() {
		EuclidianViewBounds bounds = newBounds(-86, 116, -90, 56, 1647, 1161);
		GeoFunctionNVar inequality = (GeoFunctionNVar) evaluate(
				"(x - x^3) * (y - y^3) < 0.01")[0];
		GeoElement border = inequality.getIneqs().getIneq().getImplicitCurveBorder();
		ContourInfo info = builder.withImplicitCurve(border)
				.withBounds(bounds)
				.build();
		PerimeterContourClipper clipper = createClipper(info);
		RegionClassifier classifier = new RegionClassifier(clipper::getClippedFragmentsResult,
				() -> rectangleFrom(info.getBounds()));

		assertTrue(classifier.process());
		List<ClassifiedRegion> results = classifier.getResults(info.getBounds());
		for (ClassifiedRegion region : results) {
			region.setFilled(inequality.isInRegion(region.getSamplePoint().x,
					region.getSamplePoint().y));
		}
		GArea filledArea = assembleFilledArea(results);
		PlanarGraph graph = classifier.getGraph();
		String diagnostic = describeRegions(results)
				+ " faces=" + describeFaces(graph)
				+ " extracted=" + describeCycles(graph, graph.getLastExtractedBoundaryCycles())
				+ " canonical=" + describeCycles(graph, graph.getLastCanonicalBoundaryCycles());

		assertTrue(containsWorldPoint(filledArea, bounds, -50, 50),
				"Zoomed-out cubic product inequality should keep the top-left filled quadrant: "
						+ diagnostic);
		assertTrue(containsWorldPoint(filledArea, bounds, 50, -50),
				"Zoomed-out cubic product inequality should keep the bottom-right filled quadrant: "
						+ diagnostic);
		assertFalse(containsWorldPoint(filledArea, bounds, 50, 50),
				"Zoomed-out cubic product inequality should not fill the top-right quadrant: "
						+ diagnostic);
		assertFalse(containsWorldPoint(filledArea, bounds, -50, -50),
				"Zoomed-out cubic product inequality should not fill the bottom-left quadrant: "
						+ diagnostic);
	}

	private GArea assembleFilledArea(List<ClassifiedRegion> regions) {
		GArea filledArea = AwtFactory.getPrototype().newArea();
		for (ClassifiedRegion region : regions) {
			if (!region.isFilled()) {
				continue;
			}
			GArea regionArea = AwtFactory.getPrototype().newArea(region.getOuterBoundary());
			for (GGeneralPath hole : region.getHoles()) {
				regionArea.subtract(AwtFactory.getPrototype().newArea(hole));
			}
			filledArea.add(regionArea);
		}
		return filledArea;
	}

	private boolean containsWorldPoint(GArea area, EuclidianViewBounds bounds, double x,
			double y) {
		return area.contains(bounds.toScreenCoordXd(x), bounds.toScreenCoordYd(y));
	}

	private PerimeterContourClipper createClipper(ContourInfo info) {
		PerimeterContourClipper clipper = new PerimeterContourClipper(info.getAssembler());
		clipper.setPolynomial(info.getPolynomial());
		clipper.clip(info.getBounds());
		return clipper;
	}

	private ContourInfo setupCassiniWithFourIntersectionOnTop() {
		EuclidianViewBounds bounds = newBounds(-6.28311, 4.56737, -26.65782,
				0.41718, 1237, 1265);
		GeoElement cassini = addCassini(2.9, 2.98);
		return builder.withImplicitCurve(cassini)
				.withBounds(bounds)
				.build();
	}

	/**
	 * Builds a rectangle proxy matching the supplied view bounds.
	 *
	 * @param bounds view bounds to copy
	 * @return rectangle with the same extent as the bounds
	 */
	public static GRectangle2D rectangleFrom(EuclidianViewBounds bounds) {
		GRectangle2D rect = AwtFactory.getPrototype().newRectangle2D();
		rect.setRect(bounds.getXmin(), bounds.getYmin(),
				bounds.getXmax() - bounds.getXmin(), bounds.getYmax() - bounds.getYmin());
		return rect;
	}
}
