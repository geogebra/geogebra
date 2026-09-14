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

package org.geogebra.common.euclidian.plot.implicit.classification;

import static org.geogebra.common.euclidian.plot.implicit.RegionClassifierStageATest.rectangleFrom;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.geogebra.common.awt.AwtFactory;
import org.geogebra.common.awt.GArea;
import org.geogebra.common.awt.GGeneralPath;
import org.geogebra.common.awt.GPoint2D;
import org.geogebra.common.awt.GRectangle;
import org.geogebra.common.euclidian.plot.implicit.BaseContourTestSetup;
import org.geogebra.common.euclidian.plot.implicit.BernsteinPlotterSettings;
import org.geogebra.common.euclidian.plot.implicit.ContourInfo;
import org.geogebra.common.euclidian.plot.implicit.PerimeterContourClipper;
import org.geogebra.common.euclidian.plot.implicit.classification.topology.BoundaryCycle;
import org.geogebra.common.euclidian.plot.implicit.classification.topology.Face;
import org.geogebra.common.euclidian.plot.implicit.classification.topology.HalfEdge;
import org.geogebra.common.euclidian.plot.implicit.classification.topology.PlanarGraph;
import org.geogebra.common.euclidian.plot.implicit.classification.topology.Vertex;
import org.geogebra.common.euclidian.plot.interval.EuclidianViewBounds;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunctionNVar;
import org.geogebra.common.util.debug.Log;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

@SuppressWarnings({
		"PMD.UnusedPrivateMethod"
})
class GraphBuilderWithContoursTest extends BaseContourTestSetup {
	@BeforeEach
	void setUp() {
		super.setupGraphingApp();
	}

	@ParameterizedTest
	@CsvSource({
			"(x^2 + y^2 - 1)(x^2 + y^2 - 4) = 0",
			"x^3 + y^3 = 0",
			"(x^2 + y^2 - 1)((x-4)^2 + y^2 - 1) = 0",
			"(x^2 + y^2)^2 - 17.7608 * (x^2 - y^2)- -8.133404159999998 = 0"

	})
	void testRegionClassifier(String def) {
		PlanarGraph graph = buildGraph(def, -10, 10, -10, 10, 1237, 1265);
		assertAll(
				() -> assertFalse(graph.isEmpty(), "The graph is empty"),
				() -> assertTrue(graph.hasValidFaces(), "The graph has invalid face structure"),
				() -> assertTrue(graph.hasValidLinks(), "The graph has broken next/prev links")
		);

	}

	@Test
	void shouldSampleCassiniBoundedFaces() {
		PlanarGraph graph = buildGraph(
				"(x^2 + y^2)^2 - 17.7608 * (x^2 - y^2)- -8.133404159999998 = 0",
				-10, 10, -10, 10, 1237, 1265);

		long boundedFaceCount = graph.getFaces().stream()
				.filter(face -> !face.isExterior())
				.count();
		long sampledBoundedFaceCount = graph.getFaces().stream()
				.filter(face -> !face.isExterior() && face.getSamplePoint() != null)
				.count();
		Face boundedFace = graph.getFaces().stream()
				.filter(face -> !face.isExterior())
				.findFirst()
				.orElse(null);

		assertAll(
				() -> assertTrue(boundedFaceCount >= 1,
						"Cassini contour should produce bounded faces: "
								+ describeGraphState(graph)),
				() -> assertEquals(boundedFaceCount, sampledBoundedFaceCount,
						"Every bounded Cassini face should get a sample point: "
								+ describeGraphState(graph)),
				() -> assertNotNull(boundedFace,
						"Cassini contour should expose a bounded face instance: "
								+ describeGraphState(graph)),
				() -> assertNotNull(boundedFace == null ? null : boundedFace.getSamplePoint(),
						"Cassini bounded face should have a sample point: "
								+ describeGraphState(graph))
		);
	}

	@Test
	void shouldSampleAnnulusBoundedFace() {
		PlanarGraph graph = buildGraph("(x^2 + y^2 - 1)(x^2 + y^2 - 4) = 0",
				-10, 10, -10, 10, 1237, 1265);

		long boundedFaceCount = graph.getFaces().stream()
				.filter(face -> !face.isExterior())
				.count();
		long sampledBoundedFaceCount = graph.getFaces().stream()
				.filter(face -> !face.isExterior() && face.getSamplePoint() != null)
				.count();
		Face boundedFace = graph.getFaces().stream()
				.filter(face -> !face.isExterior())
				.findFirst()
				.orElse(null);

		assertAll(
				() -> assertEquals(2, boundedFaceCount,
						"Annulus contour should produce the band and inner disk faces: "
								+ describeGraphState(graph)),
				() -> assertNotNull(boundedFace,
						"Annulus contour should expose the bounded face instance: "
								+ describeGraphState(graph)),
				() -> assertNotNull(boundedFace == null ? null : boundedFace.getSamplePoint(),
						"Annulus bounded face should have a sample point: "
								+ describeGraphState(graph)),
				() -> assertEquals(boundedFaceCount, sampledBoundedFaceCount,
						"Every bounded annulus face should get a sample point: "
								+ describeGraphState(graph))
		);
	}

	@Test
	void shouldSampleDisconnectedBoundedFaces() {
		PlanarGraph graph = buildGraph("(x^2 + y^2 - 1)((x-4)^2 + y^2 - 1) = 0",
				-10, 10, -10, 10, 1237, 1265);

		long boundedFaceCount = graph.getFaces().stream()
				.filter(face -> !face.isExterior())
				.count();
		long sampledBoundedFaceCount = graph.getFaces().stream()
				.filter(face -> !face.isExterior() && face.getSamplePoint() != null)
				.count();

		assertAll(
				() -> assertTrue(boundedFaceCount >= 1,
						"Disconnected contours should produce bounded faces: "
								+ describeGraphState(graph)),
				() -> assertEquals(boundedFaceCount, sampledBoundedFaceCount,
						"Every disconnected bounded face should get a sample point: "
								+ describeGraphState(graph))
		);
	}

	@Test
	void shouldClassifyFilledAnnulusBand() {
		List<ClassifiedRegion> results = classifyRegions(
				"(x^2 + y^2 - 1)(x^2 + y^2 - 4) <= 0",
				-10, 10, -10, 10, 1237, 1265);

		long filledCount = results.stream().filter(ClassifiedRegion::isFilled).count();
		ClassifiedRegion filledRegion = getFilledRegion(results);

		assertAll(
				() -> assertEquals(1, filledCount,
						"Annulus inequality should produce exactly one filled region: "
								+ describeRegions(results)),
				() -> assertNotNull(filledRegion,
						"Annulus inequality should expose the filled region instance: "
								+ describeRegions(results)),
				() -> assertEquals(1, filledRegion == null ? -1 : filledRegion.getHoles().size(),
						"Filled annulus region should preserve the inner hole: "
								+ describeRegions(results)),
				() -> assertNotNull(filledRegion == null ? null : filledRegion.getSamplePoint(),
						"Filled annulus region should carry a sample point: "
								+ describeRegions(results))
		);
	}

	@Test
	void clippedAnnulusShouldKeepAtLeastOneFilledBandRegion() {
		ClassificationResult result = classifyRegionWithGraph(
				"(x^2 + y^2 - 1)(x^2 + y^2 - 4) <= 0",
				1.1498150961004139, 17.380526306616982,
				-0.7013900120773064, 8.460723084197832,
				2039, 1151);
		PlanarGraph graph = result.graph();
		List<ClassifiedRegion> results = result.regions();
		long filledCount = results.stream().filter(ClassifiedRegion::isFilled).count();
		long filledWithSampleCount = results.stream()
				.filter(region -> region.isFilled() && region.getSamplePoint() != null)
				.count();

		assertAll(
				() -> assertMultiFragmentTopologyWasBuilt(graph),
				() -> assertTrue(filledCount >= 1,
						"Clipped annulus should keep at least one filled band region: "
								+ describeRegions(results) + " " + describeGraphState(graph)),
				() -> assertTrue(filledWithSampleCount >= 1,
						"Clipped annulus filled region should carry a usable sample point: "
								+ describeRegions(results) + " " + describeGraphState(graph))
		);
	}

	@Test
	void clippedAnnulusShouldNotBreakHalfEdgeLinkageNearViewportCorner() {
		double xmin = -2.9530864197530824;
		double xmax = 0.46024691358024633;
		double ymin = -1.513086419753087;
		double ymax = 3.0340740740740673;
		int width = 440;
		int height = 586;
		String def = "(x^2 + y^2 - 1)(x^2 + y^2 - 4) <= 0";
		GeoFunctionNVar function = (GeoFunctionNVar) evaluate(def)[0];
		GeoElement border = function.getIneqs().getIneq().getImplicitCurveBorder();
		ContourInfo info = builder.withImplicitCurve(border)
				.withBounds(xmin, xmax, ymin, ymax, width, height)
				.build();
		PerimeterContourClipper clipper = new PerimeterContourClipper(info.getAssembler());
		clipper.setPolynomial(info.getPolynomial());
		clipper.clip(info.getBounds());
		RegionClassifier regionClassifier =
				new RegionClassifier(clipper::getClippedFragmentsResult,
						() -> rectangleFrom(info.getBounds()));
		boolean processed = regionClassifier.process();
		PlanarGraph graph = regionClassifier.getGraph();
		String graphState = graph == null ? "graph=null" : describeGraphState(graph);

		assertTrue(processed, "Clipped annulus topology should not break half-edge linkage: "
							+ graphState);
		List<ClassifiedRegion> results = regionClassifier.getResults(info.getBounds());
		evaluateFilled(function, results);
		long filledCount = results.stream().filter(ClassifiedRegion::isFilled).count();

		assertTrue(filledCount >= 1,
				"Clipped annulus should keep at least one filled band region: "
						+ describeRegions(results) + " " + graphState);
	}

	@Test
	void multiFragmentCorridorShouldKeepAtLeastOneFilledRegion() {
		ClassificationResult result = classifyRegionWithGraph(
				"(x - x^3)(y - y^3) < 0.01",
				-16.59, 16.59, -11.51, 11.51, 1659, 1151);
		PlanarGraph graph = result.graph();
		List<ClassifiedRegion> results = result.regions();
		long filledCount = results.stream().filter(ClassifiedRegion::isFilled).count();
		long filledWithSampleCount = results.stream()
				.filter(region -> region.isFilled() && region.getSamplePoint() != null)
				.count();

		assertAll(
				() -> assertMultiFragmentTopologyWasBuilt(graph),
				() -> assertTrue(filledCount >= 1,
						"Multi-fragment corridor should keep at least one filled region: "
								+ describeRegions(results) + " " + describeGraphState(graph)),
				() -> assertTrue(filledWithSampleCount >= 1,
						"Multi-fragment corridor should expose a filled usable sample: "
								+ describeRegions(results) + " " + describeGraphState(graph))
		);
	}

	@Disabled("For separate ticket")
	@Test
	void mixedOpenAndClosedLoopShouldKeepTopRegionAndInnerLoopClassification() {
		double clipXmin = -2.2696296296296308;
		double clipXmax = 2.26962962962963;
		double clipYmin = -2.893827160493824;
		double clipYmax = 2.893827160493824;
		int width = 941;
		int height = 1148;
		double invXscale = (clipXmax - clipXmin)
				/ (width + 2.0 * BernsteinPlotterSettings.MARGIN_IN_PX);
		double invYscale = (clipYmax - clipYmin)
				/ (height + 2.0 * BernsteinPlotterSettings.MARGIN_IN_PX);
		double xmin = clipXmin + BernsteinPlotterSettings.MARGIN_IN_PX * invXscale;
		double xmax = clipXmax - BernsteinPlotterSettings.MARGIN_IN_PX * invXscale;
		double ymin = clipYmin + BernsteinPlotterSettings.MARGIN_IN_PX * invYscale;
		double ymax = clipYmax - BernsteinPlotterSettings.MARGIN_IN_PX * invYscale;
		String def = "x^2 + y^2 * (1 - y)^3 < 0.03";
		GeoFunctionNVar function = (GeoFunctionNVar) evaluate(def)[0];
		GeoElement border = function.getIneqs().getIneq().getImplicitCurveBorder();
		ContourInfo info = builder.withImplicitCurve(border)
				.withBounds(xmin, xmax, ymin, ymax, width, height)
				.build();
		PerimeterContourClipper clipper = new PerimeterContourClipper(info.getAssembler());
		clipper.setPolynomial(info.getPolynomial());
		clipper.clip(info.getBounds());
		RegionClassifier regionClassifier =
				new RegionClassifier(clipper::getClippedFragmentsResult,
						() -> rectangleFrom(info.getBounds()));
		boolean processed = regionClassifier.process();
		PlanarGraph graph = regionClassifier.getGraph();
		String graphState = graph == null ? "graph=null" : describeGraphState(graph);

		assertTrue(processed,
				"Mixed open/closed-loop topology should process successfully: " + graphState);

		List<ClassifiedRegion> results = regionClassifier.getResults(info.getBounds());
		evaluateFilled(function, results);
		EuclidianViewBounds bounds = newBounds(xmin, xmax, ymin, ymax, width, height);
		GArea filledArea = buildFilledArea(results, bounds);
		long filledCount = results.stream().filter(ClassifiedRegion::isFilled).count();

		assertAll(
				() -> assertEquals(2, clipper.getFragments().size(),
						"Expected one open clipped contour and one closed loop"),
				() -> assertTrue(graph.hasValidLinks(),
						"Mixed open/closed-loop graph should keep valid links: " + graphState),
				() -> assertTrue(graph.hasValidFaces(),
						"Mixed open/closed-loop graph should keep valid faces: " + graphState),
				() -> assertEquals(3, graph.getFaces().size(),
						"Mixed open/closed-loop case should keep the logged face count: "
								+ graphState),
				() -> assertFalse(results.isEmpty(),
						"Mixed open/closed-loop classification should produce regions: "
								+ describeRegions(results) + " " + graphState),
				() -> assertTrue(filledCount >= 1,
						"Mixed open/closed-loop case should keep a filled region: "
								+ describeRegions(results) + " " + graphState),
				() -> assertTrue(filledArea.contains(bounds.toScreenCoordXd(0),
								bounds.toScreenCoordYd(2)),
						"Top region should be filled: "
								+ describeRegions(results) + " " + graphState),
				() -> assertTrue(filledArea.contains(bounds.toScreenCoordXd(0),
								bounds.toScreenCoordYd(1)),
						"Neck region should be filled: "
								+ describeRegions(results) + " " + graphState),
				() -> assertTrue(filledArea.contains(bounds.toScreenCoordXd(0),
								bounds.toScreenCoordYd(0)),
						"Inner closed loop should be filled: "
								+ describeRegions(results) + " " + graphState),
				() -> assertFalse(filledArea.contains(bounds.toScreenCoordXd(0.5),
								bounds.toScreenCoordYd(1)),
						"Side false region should not be filled: "
								+ describeRegions(results) + " " + graphState),
				() -> assertFalse(filledArea.contains(bounds.toScreenCoordXd(1),
								bounds.toScreenCoordYd(1)),
						"Outer false region should not be filled: "
								+ describeRegions(results) + " " + graphState)
		);
	}

	@Test
	void multiFragmentCorridorShouldNotFillBottomLeftFalseRegion() {
		double xmin = -8.750;
		double xmax = 18.110;
		double ymin = -11.970;
		double ymax = 11.050;
		int width = 1343;
		int height = 1151;
		ClassificationResult result = classifyRegionWithGraph(
				"(x - x^3)(y - y^3) < 0.01",
				xmin, xmax, ymin, ymax, width, height);
		PlanarGraph graph = result.graph();
		List<ClassifiedRegion> results = result.regions();
		EuclidianViewBounds bounds = newBounds(xmin, xmax, ymin, ymax, width, height);
		GArea filledArea = buildFilledArea(results, bounds);

		assertAll(
				() -> assertMultiFragmentTopologyWasBuilt(graph),
				() -> assertFalse(corridorPredicate(-5, -5),
						"Probe should be mathematically outside the inequality"),
				() -> assertFalse(filledArea.contains(bounds.toScreenCoordXd(-5),
								bounds.toScreenCoordYd(-5)),
						"Bottom-left false region must not be included in the filled area: "
								+ describeRegions(results) + " " + describeGraphState(graph))
		);
	}

	@ParameterizedTest
	@CsvSource ({
			"-23.63, 3.23, -13.05, 9.97, 1343, 1151, -10.2, -0.5",
			"-5.540, 21.060, -1.38, 21.5, 1331, 1148, -2, -0.5"
	})
	void multiFragmentCorridorShouldFillVisibleLeftCorridor(double xmin, double xmax,
			double ymin, double ymax, int width, int height, double x, double y) {
		ClassificationResult result = classifyRegionWithGraph(
				"(x - x^3)(y - y^3) < 0.01",
				xmin, xmax, ymin, ymax, width, height);
		PlanarGraph graph = result.graph();
		List<ClassifiedRegion> results = result.regions();
		EuclidianViewBounds bounds = newBounds(xmin, xmax, ymin, ymax, width, height);
		GArea filledArea = buildFilledArea(results, bounds);

		assertAll(
				() -> assertMultiFragmentTopologyWasBuilt(graph),
				() -> assertTrue(corridorPredicate(x, y),
						"Probe should be mathematically inside the inequality"),
				() -> assertTrue(filledArea.contains(bounds.toScreenCoordXd(x),
								bounds.toScreenCoordYd(y)),
						"Visible left corridor should be included in the filled area: "
								+ describeRegions(results) + " " + describeGraphState(graph))
			);
	}

	@Test
	void multiFragmentCorridorShouldKeepRightPannedFillParity() {
		double xmin = -7.57;
		double xmax = 19.01;
		double ymin = -17.27999999999999;
		double ymax = 5.679999999999997;
		int width = 1329;
		int height = 1148;
		ClassificationResult result = classifyRegionWithGraph(
				"(x - x^3)(y - y^3) < 0.01",
				xmin, xmax, ymin, ymax, width, height);
		PlanarGraph graph = result.graph();
		List<ClassifiedRegion> results = result.regions();
		EuclidianViewBounds bounds = newBounds(xmin, xmax, ymin, ymax, width, height);
		GArea filledArea = buildFilledArea(results, bounds);

		assertAll(
				() -> assertMultiFragmentTopologyWasBuilt(graph),
				() -> assertTrue(corridorPredicate(10, 0.5),
						"Probe should be mathematically inside the right horizontal corridor"),
				() -> assertTrue(filledArea.contains(bounds.toScreenCoordXd(10),
								bounds.toScreenCoordYd(0.5)),
						"Right horizontal corridor should be included in the filled area: "
								+ describeRegions(results) + " " + describeGraphState(graph)),
				() -> assertFalse(corridorPredicate(10, 4),
						"Probe should be mathematically outside the inequality"),
				() -> assertFalse(filledArea.contains(bounds.toScreenCoordXd(10),
								bounds.toScreenCoordYd(4)),
						"Top-right false region must not be included in the filled area: "
								+ describeRegions(results) + " " + describeGraphState(graph)),
				() -> assertFalse(corridorPredicate(-5, -5),
						"Probe should be mathematically outside the inequality"),
				() -> assertFalse(filledArea.contains(bounds.toScreenCoordXd(-5),
								bounds.toScreenCoordYd(-5)),
						"Bottom-left false region must not be included in the filled area: "
								+ describeRegions(results) + " " + describeGraphState(graph))
		);
	}

	@Test
	void multiFragmentCorridorShouldNotFillFalseRegionsInWideTopRightView() {
		double xmin = -0.99;
		double xmax = 25.55;
		double ymin = -2.54;
		double ymax = 20.42;
		int width = 1327;
		int height = 1148;
		String def = "(x - x^3)(y - y^3) < 0.01";
		GeoFunctionNVar function = (GeoFunctionNVar) evaluate(def)[0];
		ClassificationResult result = classifyRegionWithGraph(
				def, xmin, xmax, ymin, ymax, width, height);
		PlanarGraph graph = result.graph();
		List<ClassifiedRegion> results = result.regions();
		EuclidianViewBounds bounds = newBounds(xmin, xmax, ymin, ymax, width, height);
		GArea filledArea = buildFilledArea(results, bounds);

		assertAll(
				() -> assertMultiFragmentTopologyWasBuilt(graph),
				() -> assertTrue(nonViewportCanonicalCycleCount(graph) > 1,
						() -> "Expected multiple non-viewport canonical interior cycles: "
								+ describeWrongFill(function, results, graph, bounds)),
				() -> assertEquals(0, sourceFaceId(graph, 0),
						() -> "Region 0 should come from the exterior face: "
								+ describeWrongFill(function, results, graph, bounds)),
				() -> assertFalse(isMixedRegion(function, results.get(0), bounds),
						() -> "Exterior region should not include predicate-false holes: "
								+ describeWrongFill(function, results, graph, bounds)),
				() -> assertFalse(corridorPredicate(-0.5, 10),
						"Probe should be mathematically outside the left false region"),
				() -> assertTrue(containingCanonicalCycleIds(graph, -0.5, 10).contains(2),
						() -> "Left false probe should be enclosed by the left vertical "
								+ "canonical cycle: "
								+ describeWrongFill(function, results, graph, bounds)),
				() -> assertFalse(exteriorFaceContainsByHoleModel(graph, -0.5, 10),
						() -> "Left false probe should be excluded from exterior "
								+ "face by its hole model: "
								+ describeWrongFill(function, results, graph, bounds)),
				() -> assertTrue(isExteriorHoleCycle(graph, 2),
						() -> "Cycle 2 should be an exterior hole so the left false "
								+ "probe is excluded from the exterior face: "
								+ describeWrongFill(function, results, graph, bounds)),
				() -> assertTrue(isNonExteriorHoleCycle(graph, 2),
						() -> "Cycle 2 is only used as a hole of the dominant interior face: "
								+ describeWrongFill(function, results, graph, bounds)),
				() -> assertFalse(filledArea.contains(bounds.toScreenCoordXd(-0.5),
								bounds.toScreenCoordYd(10)),
						() -> "Left false region must not be included in the filled area: "
								+ describeWrongFill(function, results, graph, bounds)),
				() -> assertFalse(filledRegionContaining(results, bounds, -0.5, 10) >= 0,
						() -> "Left false region is inside a filled classified region: "
								+ describeWrongFill(function, results, graph, bounds)),
				() -> assertFalse(corridorPredicate(10, 10),
						"Probe should be mathematically outside the top-right false region"),
				() -> assertTrue(containingCanonicalCycleIds(graph, 10, 10).contains(0),
						() -> "Top-right false probe should be enclosed by the large "
								+ "top-right canonical cycle: "
								+ describeWrongFill(function, results, graph, bounds)),
				() -> assertFalse(exteriorFaceContainsByHoleModel(graph, 10, 10),
						() -> "Top-right false probe should be excluded from exterior "
								+ "face by its current hole model: "
								+ describeWrongFill(function, results, graph, bounds)),
				() -> assertFalse(filledArea.contains(bounds.toScreenCoordXd(10),
								bounds.toScreenCoordYd(10)),
						() -> "Top-right false region must not be included in the filled area: "
								+ describeWrongFill(function, results, graph, bounds)),
				() -> assertFalse(filledRegionContaining(results, bounds, 10, 10) >= 0,
						() -> "Top-right false region is inside a filled classified region: "
								+ describeWrongFill(function, results, graph, bounds)),
				() -> assertTrue(corridorPredicate(0.5, 10),
						"Probe should be mathematically inside the visible vertical corridor"),
				() -> assertTrue(filledArea.contains(bounds.toScreenCoordXd(0.5),
								bounds.toScreenCoordYd(10)),
						() -> "Visible vertical corridor should remain filled: "
								+ describeWrongFill(function, results, graph, bounds)),
				() -> assertTrue(corridorPredicate(10, 0.5),
						"Probe should be mathematically inside the visible horizontal corridor"),
				() -> assertTrue(filledArea.contains(bounds.toScreenCoordXd(10),
								bounds.toScreenCoordYd(0.5)),
						() -> "Visible horizontal corridor should remain filled: "
								+ describeWrongFill(function, results, graph, bounds))
		);
	}

	@Test
	void multiFragmentCorridorShouldSubtractContainedFalseRootWithBadProbe() {
		double xmin = -1.8598053281406146;
		double xmax = 0.14723125667571754;
		double ymin = -0.8926690902466957;
		double ymax = 0.7757436536846216;
		int width = 1588;
		int height = 1148;
		String def = "(x - x^3)(y - y^3) < 0.01";
		GeoFunctionNVar function = (GeoFunctionNVar) evaluate(def)[0];
		ClassificationResult result = classifyRegionWithGraph(
				def, xmin, xmax, ymin, ymax, width, height);
		PlanarGraph graph = result.graph();
		List<ClassifiedRegion> results = result.regions();
		EuclidianViewBounds bounds = newBounds(xmin, xmax, ymin, ymax, width, height);
		GArea filledArea = buildFilledArea(results, bounds);
		double falseX = -1.5089398315739102;
		double falseY = 0.4613634304848018;
		double trueX = -1.0456147899368262;
		double trueY = 0.01954187642268022;

		assertAll(
				() -> assertMultiFragmentTopologyWasBuilt(graph),
				() -> assertFalse(corridorPredicate(falseX, falseY),
						"Probe should be mathematically outside the inequality"),
				() -> assertTrue(containedByNestedCanonicalCycle(graph, falseX, falseY),
						() -> "False probe should be enclosed by a nested canonical cycle: "
								+ describeWrongFill(function, results, graph, bounds)),
				() -> assertFalse(filledArea.contains(bounds.toScreenCoordXd(falseX),
								bounds.toScreenCoordYd(falseY)),
						() -> "Contained false root must be subtracted from the filled area: "
								+ describeWrongFill(function, results, graph, bounds)),
				() -> assertFalse(filledRegionContaining(results, bounds, falseX, falseY) >= 0,
						() -> "Contained false root is inside a filled classified region: "
								+ describeWrongFill(function, results, graph, bounds)),
				() -> assertTrue(corridorPredicate(trueX, trueY),
						"Probe should be mathematically inside the inequality"),
				() -> assertTrue(filledArea.contains(bounds.toScreenCoordXd(trueX),
								bounds.toScreenCoordYd(trueY)),
						() -> "Adjacent true corridor should remain filled: "
								+ describeWrongFill(function, results, graph, bounds))
		);
	}

	@Test
	void crossingCassiniShouldFillHorizontalLobes() {
		double xmin = -4.1822222222222;
		double xmax = 4.182222222222244;
		double ymin = -5.102222222222156;
		double ymax = 5.102222222222109;
		int width = 941;
		int height = 1148;
		ClassificationResult result = classifyRegionWithGraph(
				"(x^2 + y^2)^2 - 2 * 5^2 * (x^2 - y^2) - (5^4 - 5^4) < 0",
				xmin, xmax, ymin, ymax, width, height);
		PlanarGraph graph = result.graph();
		List<ClassifiedRegion> results = result.regions();
		EuclidianViewBounds bounds = newBounds(xmin, xmax, ymin, ymax, width, height);
		GArea filledArea = buildFilledArea(results, bounds);

		assertAll(
				() -> assertMultiFragmentTopologyWasBuilt(graph),
				() -> assertTrue(filledArea.contains(bounds.toScreenCoordXd(2),
								bounds.toScreenCoordYd(0)),
						"Right Cassini lobe should be filled: "
								+ describeRegions(results) + " " + describeGraphState(graph)),
				() -> assertTrue(filledArea.contains(bounds.toScreenCoordXd(-2),
								bounds.toScreenCoordYd(0)),
						"Left Cassini lobe should be filled: "
								+ describeRegions(results) + " " + describeGraphState(graph)),
				() -> assertFalse(filledArea.contains(bounds.toScreenCoordXd(0),
								bounds.toScreenCoordYd(2)),
						"Top false region must not be filled: "
								+ describeRegions(results) + " " + describeGraphState(graph))
		);
	}

	@Test
	void crossingCassiniWideViewShouldSampleLobesAwayFromNeck() {
		double xmin = -7.915;
		double xmax = 7.915;
		double ymin = -5.74;
		double ymax = 5.74;
		int width = 1189;
		int height = 862;
		ClassificationResult result = classifyRegionWithGraph(
				"(x^2 + y^2)^2 - 2 * 5^2 * (x^2 - y^2) - (5^4 - 5^4) < 0",
				xmin, xmax, ymin, ymax, width, height);
		PlanarGraph graph = result.graph();
		List<ClassifiedRegion> results = result.regions();
		EuclidianViewBounds bounds = newBounds(xmin, xmax, ymin, ymax, width, height);
		GArea filledArea = buildFilledArea(results, bounds);

		assertAll(
				() -> assertMultiFragmentTopologyWasBuilt(graph),
				() -> assertTrue(filledArea.contains(bounds.toScreenCoordXd(4),
								bounds.toScreenCoordYd(0)),
						"Right Cassini lobe should be filled in wide view: "
								+ describeRegions(results) + " " + describeGraphState(graph)),
				() -> assertTrue(filledArea.contains(bounds.toScreenCoordXd(-4),
								bounds.toScreenCoordYd(0)),
						"Left Cassini lobe should be filled in wide view: "
								+ describeRegions(results) + " " + describeGraphState(graph)),
				() -> assertFalse(filledArea.contains(bounds.toScreenCoordXd(0),
								bounds.toScreenCoordYd(1)),
						"Vertical false region must not be filled in wide view: "
								+ describeRegions(results) + " " + describeGraphState(graph))
		);
	}

	@Test
	void zoomedCrossingCassiniShouldNotBreakHalfEdgeLinkage() {
		double xmin = -0.153;
		double xmax = 0.17315605355330052;
		double ymin = -0.276;
		double ymax = 0.1214810504025361;
		int width = 942;
		int height = 1148;
		String def = "(x^2 + y^2)^2 - 2 * 5^2 * (x^2 - y^2) - (5^4 - 5^4) < 0";
		GeoFunctionNVar function = (GeoFunctionNVar) evaluate(def)[0];
		GeoElement border = function.getIneqs().getIneq().getImplicitCurveBorder();
		ContourInfo info = builder.withImplicitCurve(border)
				.withBounds(xmin, xmax, ymin, ymax, width, height)
				.build();
		PerimeterContourClipper clipper = new PerimeterContourClipper(info.getAssembler());
		clipper.setPolynomial(info.getPolynomial());
		clipper.clip(info.getBounds());
		RegionClassifier regionClassifier =
				new RegionClassifier(clipper::getClippedFragmentsResult,
						() -> rectangleFrom(info.getBounds()));

		boolean processed = regionClassifier.process();
		PlanarGraph graph = regionClassifier.getGraph();
		String graphState = graph == null ? "graph=null" : describeGraphState(graph);

		assertTrue(processed,
				"Zoomed crossing Cassini should not break half-edge linkage: " + graphState);

		List<ClassifiedRegion> results = regionClassifier.getResults(info.getBounds());
		evaluateFilled(function, results);
		EuclidianViewBounds bounds = newBounds(xmin, xmax, ymin, ymax, width, height);
		GArea filledArea = buildFilledArea(results, bounds);

		assertAll(
				() -> assertTrue(filledArea.contains(bounds.toScreenCoordXd(0.1),
								bounds.toScreenCoordYd(0)),
						"Right zoomed lobe should be filled: "
								+ describeRegions(results) + " " + graphState),
				() -> assertFalse(filledArea.contains(bounds.toScreenCoordXd(0),
								bounds.toScreenCoordYd(0.1)),
						"Vertical false region must not be filled: "
								+ describeRegions(results) + " " + graphState)
		);
	}

	@Test
	void zoomedCrossingCassiniShouldBeFilled() {
		double xmin = -1.007;
		double xmax = 1.007;
		double ymin = -1.512;
		double ymax = 1.512;
		int width = 765;
		int height = 1148;
		String def = "(x^2 + y^2)^2 - 2 * (-4.5)^2 * (x^2 - y^2) - ((4.5)^2 - (-4.5)^2) < 0";
		GeoFunctionNVar function = (GeoFunctionNVar) evaluate(def)[0];
		GeoElement border = function.getIneqs().getIneq().getImplicitCurveBorder();
		ContourInfo info = builder.withImplicitCurve(border)
				.withBounds(xmin, xmax, ymin, ymax, width, height)
				.build();
		PerimeterContourClipper clipper = new PerimeterContourClipper(info.getAssembler());
		clipper.setPolynomial(info.getPolynomial());
		clipper.clip(info.getBounds());
		RegionClassifier regionClassifier =
				new RegionClassifier(clipper::getClippedFragmentsResult,
						() -> rectangleFrom(info.getBounds()));

		boolean processed = regionClassifier.process();
		PlanarGraph graph = regionClassifier.getGraph();
		String graphState = graph == null ? "graph=null" : describeGraphState(graph);

		assertTrue(processed,
				"Zoomed crossing Cassini should not break half-edge linkage: " + graphState);

		List<ClassifiedRegion> results = regionClassifier.getResults(info.getBounds());
		evaluateFilled(function, results);
		EuclidianViewBounds bounds = newBounds(xmin, xmax, ymin, ymax, width, height);
		GArea filledArea = buildFilledArea(results, bounds);

		assertAll(
				() -> assertTrue(filledArea.contains(bounds.toScreenCoordXd(0.1),
								bounds.toScreenCoordYd(0)),
						"Right zoomed lobe should be filled: "
								+ describeRegions(results) + " " + graphState),
				() -> assertFalse(filledArea.contains(bounds.toScreenCoordXd(0),
								bounds.toScreenCoordYd(0.1)),
						"Vertical false region must not be filled: "
								+ describeRegions(results) + " " + graphState)
		);
		Log.debug("GRAPH STATE: " + graphState);
	}

	private GArea buildFilledArea(List<ClassifiedRegion> regions, EuclidianViewBounds bounds) {
		GArea filled = AwtFactory.getPrototype().newArea();
		for (ClassifiedRegion region : regions) {
			if (!region.isFilled()) {
				continue;
			}
			GArea regionArea = AwtFactory.getPrototype().newArea(region.getOuterBoundary());
			for (GGeneralPath hole : region.getHoles()) {
				regionArea.subtract(AwtFactory.getPrototype().newArea(hole));
			}
			subtractExplicitFalseRegions(regionArea, regions, region, bounds);
			filled.add(regionArea);
		}
		return filled;
	}

	private void subtractExplicitFalseRegions(GArea filledRegionArea,
			List<ClassifiedRegion> regions, ClassifiedRegion filledRegion,
			EuclidianViewBounds bounds) {
		for (ClassifiedRegion region : regions) {
			if (region == filledRegion || region.isFilled()
					|| region.getOuterBoundary() == null) {
				continue;
			}
			GPoint2D samplePoint = region.getSamplePoint();
			if (samplePoint == null || !filledRegionArea.contains(
					bounds.toScreenCoordXd(samplePoint.x),
					bounds.toScreenCoordYd(samplePoint.y))) {
				continue;
			}
			filledRegionArea.subtract(areaOf(region));
		}
	}

	private boolean corridorPredicate(double x, double y) {
		return (x - x * x * x) * (y - y * y * y) < 0.01;
	}

	private void assertMultiFragmentTopologyWasBuilt(PlanarGraph graph) {
		long boundedFaceCount = graph.getFaces().stream()
				.filter(face -> !face.isExterior())
				.count();
		int extractedCount = graph.getLastExtractedBoundaryCycles().size();
		int canonicalCount = graph.getLastCanonicalBoundaryCycles().size();

		assertAll(
				() -> assertTrue(graph.hasValidLinks(),
						"Graph links should stay valid while reproducing the fill bug: "
								+ describeGraphState(graph)),
				() -> assertTrue(boundedFaceCount > 0,
						"Reproducer should build bounded faces before fill evaluation: "
								+ describeGraphState(graph)),
				() -> assertTrue(extractedCount > 1 && extractedCount >= canonicalCount,
						"Reproducer should expose multi-cycle topology selection state: "
								+ describeGraphState(graph))
		);
	}

	private static ClassifiedRegion getFilledRegion(List<ClassifiedRegion> results) {
		return results.stream()
				.filter(ClassifiedRegion::isFilled)
				.findFirst()
				.orElse(null);
	}

	@Test
	void shouldClassifyDisconnectedFilledComponents() {
		List<ClassifiedRegion> results = classifyRegions(
				"(x^2 + y^2 - 1)(((x-4)^2 + y^2) - 1) <= 0",
				-10, 10, -10, 10, 1237, 1265);

		long filledCount = results.stream().filter(ClassifiedRegion::isFilled).count();
		long holeCount = results.stream()
				.filter(ClassifiedRegion::isFilled)
				.mapToLong(region -> region.getHoles().size())
				.sum();

		assertAll(
				() -> assertEquals(2, filledCount,
						"Separated implicit components should stay as two filled regions: "
								+ describeRegions(results)),
				() -> assertEquals(0, holeCount,
						"Separated filled components should not invent holes: "
								+ describeRegions(results))
		);
	}

	@Test
	void debugCassiniFreshLoadViewport() {
		String def = "(x^2 + y^2)^2 - 17.7608 * (x^2 - y^2)- -8.133404159999998 = 0";
		GeoElement geo = (GeoElement) evaluate(def)[0];
		ContourInfo info = builder.withImplicitCurve(geo)
				.withBounds(-33.70160061026482, -0.23288260946263112,
						-35.792345390507414, -0.5532418027609858, 1191, 1254)
				.build();
		PerimeterContourClipper clipper = new PerimeterContourClipper(info.getAssembler());
		clipper.setPolynomial(info.getPolynomial());
		clipper.clip(info.getBounds());

		RegionClassifier regionClassifier =
				new RegionClassifier(clipper::getClippedFragmentsResult,
						() -> rectangleFrom(info.getBounds()));

		assertTrue(regionClassifier.process(),
				"Debug scenario should complete topology processing");

		PlanarGraph graph = regionClassifier.getGraph();
		List<ClassifiedRegion> results = regionClassifier.getResults(info.getBounds());

		assertAll(
				() -> assertNotNull(graph, "Debug graph should be available"),
				() -> assertNotNull(results, "Debug classification results should be available"),
				() -> assertFalse(results.isEmpty(),
						"Debug scenario should produce at least the viewport region")
		);
	}

	@ParameterizedTest
	@CsvSource({
			"-16.12, 42.39, -13.43, 29.13",
			"-28.82163760797805, 29.70083729770597, -17.81372538329036, 24.75424650286259",
			"-26.6221990257481, 6.502199025748115, -16.595017064846424, 8.51501706484644"
	})
	void shouldBoundedCyclePresent(double xmin, double xmax, double ymin, double ymax) {
		ClassificationResult result = classifyRegionWithGraph(
				"x^3 <= y^3", xmin, xmax, ymin, ymax, 1237, 1265);
		assertEquals(2, result.regions.size(), describeGraphState(result.graph));

	}

	private PlanarGraph buildGraph(String def, double xmin, double xmax,
			double ymin, double ymax, int width, int height) {
		GeoElement geo = (GeoElement) evaluate(def)[0];
		ContourInfo info = builder.withImplicitCurve(geo)
				.withBounds(xmin, xmax, ymin, ymax, width, height)
				.build();
		PerimeterContourClipper clipper = new PerimeterContourClipper(info.getAssembler());
		clipper.setPolynomial(info.getPolynomial());
		clipper.clip(info.getBounds());
		RegionClassifier regionClassifier =
				new RegionClassifier(clipper::getClippedFragmentsResult,
						() -> rectangleFrom(info.getBounds()));
		regionClassifier.process();
		return regionClassifier.getGraph();
	}

	private List<ClassifiedRegion> classifyRegions(String def, double xmin, double xmax,
			double ymin, double ymax, int width, int height) {
		return classifyRegionWithGraph(def, xmin, xmax, ymin, ymax, width, height).regions;
	}

	private ClassificationResult classifyRegionWithGraph(String def, double xmin, double xmax,
			double ymin, double ymax, int width, int height) {
		GeoFunctionNVar function = (GeoFunctionNVar) evaluate(def)[0];
		GeoElement border = function.getIneqs().getIneq().getImplicitCurveBorder();
		ContourInfo info = builder.withImplicitCurve(border)
				.withBounds(xmin, xmax, ymin, ymax, width, height)
				.build();
		PerimeterContourClipper clipper = new PerimeterContourClipper(info.getAssembler());
		clipper.setPolynomial(info.getPolynomial());
		clipper.clip(info.getBounds());
		RegionClassifier regionClassifier =
				new RegionClassifier(clipper::getClippedFragmentsResult,
						() -> rectangleFrom(info.getBounds()));
		assertTrue(regionClassifier.process(), "Classification should succeed for " + def);
		List<ClassifiedRegion> results = regionClassifier.getResults(info.getBounds());
		evaluateFilled(function, results);
		return new ClassificationResult(regionClassifier.getGraph(), results);
	}

	private record ClassificationResult(PlanarGraph graph, List<ClassifiedRegion> regions) {
	}

	private void evaluateFilled(GeoFunctionNVar function, List<ClassifiedRegion> regions) {
		for (ClassifiedRegion region : regions) {
			GPoint2D p = region.getSamplePoint();
			region.setFilled(function.isInRegion(p.x, p.y));
		}
	}

	private String describeGraphState(PlanarGraph graph) {
		return "faces=" + graph.getFaces().size()
				+ " contourDupDir=" + countDuplicateDirectedContourEdges(graph)
				+ " contourDupUndir=" + countDuplicateUndirectedContourEdges(graph)
				+ " extracted=" + describeCycles(graph,
						graph.getLastExtractedBoundaryCycles())
				+ " canonical=" + describeCycles(graph,
						graph.getLastCanonicalBoundaryCycles());
	}

	private WrongFillDiagnostics describeWrongFill(GeoFunctionNVar function,
			List<ClassifiedRegion> regions, PlanarGraph graph, EuclidianViewBounds bounds) {
		return new WrongFillDiagnostics(
				probeDiagnostics(function, regions, bounds),
				probeTopologyDiagnostics(graph),
				regionDiagnostics(function, regions, graph, bounds),
				faceDiagnostics(graph),
				cycleRoleDiagnostics(graph),
				cycleDiagnostics(graph, graph.getLastExtractedBoundaryCycles()),
				cycleDiagnostics(graph, graph.getLastCanonicalBoundaryCycles()),
				cycleGroupDiagnostics(graph),
				hierarchySignal(graph));
	}

	private long countDuplicateDirectedContourEdges(PlanarGraph graph) {
		return graph.getHalfEdges().stream()
				.filter(halfEdge -> halfEdge.isActive() && halfEdge.isContourEdge())
				.collect(Collectors.groupingBy(halfEdge -> halfEdge.getSourceContourId() + ":"
						+ halfEdge.getOriginVertexId() + "->" + halfEdge.getTargetVertexId(),
						Collectors.counting()))
				.values().stream()
				.filter(count -> count > 1)
				.count();
	}

	private long countDuplicateUndirectedContourEdges(PlanarGraph graph) {
		return graph.getHalfEdges().stream()
				.filter(halfEdge -> halfEdge.isActive() && halfEdge.isContourEdge())
				.collect(Collectors.groupingBy(halfEdge -> {
					int origin = halfEdge.getOriginVertexId();
					int target = halfEdge.getTargetVertexId();
					int a = Math.min(origin, target);
					int b = Math.max(origin, target);
					return halfEdge.getSourceContourId() + ":" + a + "-" + b;
				}, Collectors.counting()))
				.values().stream()
				.filter(count -> count > 2)
				.count();
	}

	private String describeCycles(PlanarGraph graph, List<BoundaryCycle> cycles) {
		StringBuilder sb = new StringBuilder("[");
		for (int i = 0; i < cycles.size(); i++) {
			BoundaryCycle cycle = cycles.get(i);
			if (i > 0) {
				sb.append(", ");
			}
			sb.append("{id=").append(cycle.getId())
					.append(", signedArea=").append(cycle.getSignedArea())
					.append(", absArea=").append(cycle.getAbsArea())
					.append(", edges=").append(cycle.getHalfEdgeIds().size())
					.append(", parent=").append(cycle.getParentId())
					.append(", depth=").append(cycle.getDepth())
					.append(", children=").append(cycle.getChildIds())
					.append(", bbox=").append(boundaryBounds(graph, cycle.getHalfEdgeIds()))
					.append(", corners=").append(viewportCornerCount(graph, cycle))
					.append(", viewport=").append(isViewportLikeCycle(graph, cycle))
					.append(", probe=").append(cycle.getContainmentProbePoint())
					.append('}');
		}
		sb.append(']');
		return sb.toString();
	}

	private String describeRegions(List<ClassifiedRegion> regions) {
		StringBuilder sb = new StringBuilder("[");
		for (int i = 0; i < regions.size(); i++) {
			ClassifiedRegion region = regions.get(i);
			if (i > 0) {
				sb.append(", ");
			}
			sb.append("{filled=").append(region.isFilled())
					.append(", holes=").append(region.getHoles().size())
					.append(", sample=").append(region.getSamplePoint())
					.append('}');
		}
		sb.append(']');
		return sb.toString();
	}

	private String describeRegions(GeoFunctionNVar function, List<ClassifiedRegion> regions,
			EuclidianViewBounds bounds) {
		StringBuilder sb = new StringBuilder("[");
		for (int i = 0; i < regions.size(); i++) {
			ClassifiedRegion region = regions.get(i);
			if (i > 0) {
				sb.append(", ");
			}
			GPoint2D sample = region.getSamplePoint();
			Boolean predicate = sample == null ? null : function.isInRegion(sample.x, sample.y);
			sb.append("{index=").append(i)
					.append(", filled=").append(region.isFilled())
					.append(", predicate=").append(predicate)
					.append(", holes=").append(region.getHoles().size())
					.append(", viewport=").append(isViewportRegion(region, bounds))
					.append(", screenBbox=").append(formatScreenBounds(region))
					.append(", worldBbox=").append(formatWorldBounds(region, bounds))
					.append(", sample=").append(sample)
					.append('}');
		}
		sb.append(']');
		return sb.toString();
	}

	private List<CycleDiagnostic> cycleDiagnostics(PlanarGraph graph,
			List<BoundaryCycle> cycles) {
		return cycles.stream()
				.map(cycle -> new CycleDiagnostic(cycle.getId(), cycle.getSignedArea(),
						cycle.getAbsArea(), cycle.getHalfEdgeIds().size(),
						cycle.getParentId(), cycle.getDepth(), cycle.getChildIds(),
						boundaryBounds(graph, cycle.getHalfEdgeIds()),
						viewportCornerCount(graph, cycle), isViewportLikeCycle(graph, cycle),
						cycle.getContainmentProbePoint()))
				.collect(Collectors.toList());
	}

	private List<CycleGroupDiagnostic> cycleGroupDiagnostics(PlanarGraph graph) {
		Map<String, List<BoundaryCycle>> byBounds = new LinkedHashMap<>();
		for (BoundaryCycle cycle : graph.getLastExtractedBoundaryCycles()) {
			byBounds.computeIfAbsent(cycleBoundsKey(graph, cycle),
					key -> new ArrayList<>()).add(cycle);
		}
		return byBounds.entrySet().stream()
				.map(entry -> cycleGroupDiagnostic(graph, entry.getKey(), entry.getValue()))
				.collect(Collectors.toList());
	}

	private CycleGroupDiagnostic cycleGroupDiagnostic(PlanarGraph graph, String boundsKey,
			List<BoundaryCycle> extractedCycles) {
		List<Integer> extractedIds = new ArrayList<>(extractedCycles.size());
		List<Double> extractedSigns = new ArrayList<>(extractedCycles.size());
		List<Integer> canonicalIds = new ArrayList<>();
		List<Double> canonicalSigns = new ArrayList<>();
		Set<Integer> extractedIdSet = extractedCycles.stream()
				.map(BoundaryCycle::getId)
				.collect(Collectors.toSet());
		for (BoundaryCycle cycle : extractedCycles) {
			extractedIds.add(cycle.getId());
			extractedSigns.add(Math.signum(cycle.getSignedArea()));
		}
		for (BoundaryCycle cycle : graph.getLastCanonicalBoundaryCycles()) {
			if (extractedIdSet.contains(cycle.getId())) {
				canonicalIds.add(cycle.getId());
				canonicalSigns.add(Math.signum(cycle.getSignedArea()));
			}
		}
		return new CycleGroupDiagnostic(boundsKey, extractedIds, extractedSigns,
				canonicalIds, canonicalSigns);
	}

	private List<FaceDiagnostic> faceDiagnostics(PlanarGraph graph) {
		return graph.getFaces().stream()
				.map(face -> {
					List<Integer> outerBoundary = face.getOuterHalfEdgeId() < 0
							? List.of() : graph.outerBoundaryOf(face);
					String bbox = face.getOuterHalfEdgeId() < 0
							? "[]" : boundaryBounds(graph, outerBoundary);
					return new FaceDiagnostic(face.getId(), face.isExterior(),
							face.getOuterHalfEdgeId(),
							cycleIdOfBoundary(graph, outerBoundary),
							face.getHoleHalfEdgeIds(), holeCycleIds(graph, face),
							face.getSamplePoint(), bbox);
				})
				.collect(Collectors.toList());
	}

	private List<CycleRoleDiagnostic> cycleRoleDiagnostics(PlanarGraph graph) {
		Set<Integer> exteriorHoleCycleIds = new HashSet<>();
		Set<Integer> nonExteriorOuterCycleIds = new HashSet<>();
		Set<Integer> nonExteriorHoleCycleIds = new HashSet<>();
		for (Face face : graph.getFaces()) {
			if (face.isExterior()) {
				exteriorHoleCycleIds.addAll(holeCycleIds(graph, face));
				continue;
			}
			nonExteriorOuterCycleIds.add(cycleIdOfBoundary(graph, graph.outerBoundaryOf(face)));
			nonExteriorHoleCycleIds.addAll(holeCycleIds(graph, face));
		}
		return graph.getLastCanonicalBoundaryCycles().stream()
				.map(cycle -> new CycleRoleDiagnostic(cycle.getId(), cycle.getParentId(),
						cycle.getDepth(), cycle.getParentId() == -1,
						exteriorHoleCycleIds.contains(cycle.getId()),
						nonExteriorOuterCycleIds.contains(cycle.getId()),
						nonExteriorHoleCycleIds.contains(cycle.getId()),
						cycle.getStartHalfEdgeId(),
						boundaryBounds(graph, cycle.getHalfEdgeIds())))
				.collect(Collectors.toList());
	}

	private boolean isExteriorHoleCycle(PlanarGraph graph, int cycleId) {
		return cycleRoleDiagnostics(graph).stream()
				.filter(role -> role.id() == cycleId)
				.anyMatch(CycleRoleDiagnostic::exteriorHole);
	}

	private boolean isNonExteriorHoleCycle(PlanarGraph graph, int cycleId) {
		return cycleRoleDiagnostics(graph).stream()
				.filter(role -> role.id() == cycleId)
				.anyMatch(CycleRoleDiagnostic::nonExteriorHole);
	}

	private List<RegionDiagnostic> regionDiagnostics(GeoFunctionNVar function,
			List<ClassifiedRegion> regions, PlanarGraph graph, EuclidianViewBounds bounds) {
		List<RegionDiagnostic> diagnostics = new ArrayList<>(regions.size());
		for (int i = 0; i < regions.size(); i++) {
			diagnostics.add(regionDiagnostic(function, i, sourceFaceId(graph, i),
					regions.get(i), bounds));
		}
		return diagnostics;
	}

	private RegionDiagnostic regionDiagnostic(GeoFunctionNVar function, int index,
			int sourceFaceId,
			ClassifiedRegion region, EuclidianViewBounds bounds) {
		GPoint2D sample = region.getSamplePoint();
		Boolean predicate = sample == null ? null : function.isInRegion(sample.x, sample.y);
		MixedProbeStats mixedProbeStats = mixedProbeStats(function, region, bounds);
		return new RegionDiagnostic(index, sourceFaceId, region.isFilled(), sample, predicate,
				region.getHoles().size(), formatScreenBounds(region),
				formatWorldBounds(region, bounds), isViewportRegion(region, bounds),
				mixedProbeStats.trueProbeCount(), mixedProbeStats.falseProbeCount(),
				mixedProbeStats.mixedPredicate(), mixedProbeStats.firstTrueProbe(),
				mixedProbeStats.firstFalseProbe());
	}

	private int sourceFaceId(PlanarGraph graph, int regionIndex) {
		return graph.getFaces().size() <= regionIndex ? -1
				: graph.getFaces().get(regionIndex).getId();
	}

	private boolean isMixedRegion(GeoFunctionNVar function, ClassifiedRegion region,
			EuclidianViewBounds bounds) {
		return mixedProbeStats(function, region, bounds).mixedPredicate();
	}

	private MixedProbeStats mixedProbeStats(GeoFunctionNVar function, ClassifiedRegion region,
			EuclidianViewBounds bounds) {
		GRectangle screenBounds = region.getOuterBoundary().getBounds();
		if (screenBounds == null) {
			return new MixedProbeStats(0, 0, null, null);
		}
		GArea regionArea = areaOf(region);
		int trueProbeCount = 0;
		int falseProbeCount = 0;
		GPoint2D firstTrueProbe = null;
		GPoint2D firstFalseProbe = null;
		for (int xIndex = 1; xIndex <= 25; xIndex++) {
			double screenX = screenBounds.getX()
					+ screenBounds.getWidth() * xIndex / 26.0;
			for (int yIndex = 1; yIndex <= 25; yIndex++) {
				double screenY = screenBounds.getY()
						+ screenBounds.getHeight() * yIndex / 26.0;
				if (!regionArea.contains(screenX, screenY)) {
					continue;
				}
				double worldX = bounds.toRealWorldCoordX(screenX);
				double worldY = bounds.toRealWorldCoordY(screenY);
				GPoint2D probe = new GPoint2D(worldX, worldY);
				if (function.isInRegion(worldX, worldY)) {
					trueProbeCount++;
					if (firstTrueProbe == null) {
						firstTrueProbe = probe;
					}
				} else {
					falseProbeCount++;
					if (firstFalseProbe == null) {
						firstFalseProbe = probe;
					}
				}
			}
		}
		return new MixedProbeStats(trueProbeCount, falseProbeCount,
				firstTrueProbe, firstFalseProbe);
	}

	private List<ProbeDiagnostic> probeDiagnostics(GeoFunctionNVar function,
			List<ClassifiedRegion> regions, EuclidianViewBounds bounds) {
		GArea filledArea = buildFilledArea(regions, bounds);
		return List.of(
				probeDiagnostic(function, regions, bounds, filledArea, -0.5, 10),
				probeDiagnostic(function, regions, bounds, filledArea, 10, 10),
				probeDiagnostic(function, regions, bounds, filledArea, 0.5, 10),
				probeDiagnostic(function, regions, bounds, filledArea, 10, 0.5));
	}

	private ProbeDiagnostic probeDiagnostic(GeoFunctionNVar function,
			List<ClassifiedRegion> regions, EuclidianViewBounds bounds, GArea filledArea,
			double x, double y) {
		double screenX = bounds.toScreenCoordXd(x);
		double screenY = bounds.toScreenCoordYd(y);
		return new ProbeDiagnostic(x, y, screenX, screenY, function.isInRegion(x, y),
				filledArea.contains(screenX, screenY),
				filledRegionContaining(regions, bounds, x, y));
	}

	private List<ProbeTopologyDiagnostic> probeTopologyDiagnostics(PlanarGraph graph) {
		return List.of(
				probeTopologyDiagnostic(graph, -0.5, 10),
				probeTopologyDiagnostic(graph, 10, 10),
				probeTopologyDiagnostic(graph, 0.5, 10),
				probeTopologyDiagnostic(graph, 10, 0.5));
	}

	private ProbeTopologyDiagnostic probeTopologyDiagnostic(PlanarGraph graph, double x, double y) {
		return new ProbeTopologyDiagnostic(x, y,
				containingCanonicalCycleIds(graph, x, y),
				containingNonExteriorFaceIds(graph, x, y),
				exteriorFaceContainsByHoleModel(graph, x, y));
	}

	private List<Integer> containingCanonicalCycleIds(PlanarGraph graph, double x, double y) {
		GPoint2D point = new GPoint2D(x, y);
		List<Integer> cycleIds = new ArrayList<>();
		for (BoundaryCycle cycle : graph.getLastCanonicalBoundaryCycles()) {
			if (containsPoint(graph, cycle.getHalfEdgeIds(), point)) {
				cycleIds.add(cycle.getId());
			}
		}
		return cycleIds;
	}

	private boolean containedByNestedCanonicalCycle(PlanarGraph graph, double x, double y) {
		GPoint2D point = new GPoint2D(x, y);
		for (BoundaryCycle cycle : graph.getLastCanonicalBoundaryCycles()) {
			if (cycle.getParentId() != -1 && containsPoint(graph, cycle.getHalfEdgeIds(), point)) {
				return true;
			}
		}
		return false;
	}

	private List<Integer> containingNonExteriorFaceIds(PlanarGraph graph, double x, double y) {
		GPoint2D point = new GPoint2D(x, y);
		List<Integer> faceIds = new ArrayList<>();
		for (Face face : graph.getFaces()) {
			if (face.isExterior()) {
				continue;
			}
			if (graph.isPointInsideFace(point, graph.outerBoundaryOf(face),
					graph.holeBoundariesOf(face))) {
				faceIds.add(face.getId());
			}
		}
		return faceIds;
	}

	private boolean exteriorFaceContainsByHoleModel(PlanarGraph graph, double x, double y) {
		Face exteriorFace = graph.getFaces().stream()
				.filter(Face::isExterior)
				.findFirst()
				.orElse(null);
		if (exteriorFace == null) {
			return false;
		}
		GPoint2D point = new GPoint2D(x, y);
		for (List<Integer> holeBoundary : graph.holeBoundariesOf(exteriorFace)) {
			if (containsPoint(graph, holeBoundary, point)) {
				return false;
			}
		}
		return true;
	}

	private boolean containsPoint(PlanarGraph graph, List<Integer> boundary, GPoint2D point) {
		if (boundary.size() < 3) {
			return false;
		}
		boolean inside = false;
		int size = boundary.size();
		for (int i = 0, j = size - 1; i < size; j = i++) {
			Vertex vi = graph.vertex(graph.halfEdge(boundary.get(i)).getOriginVertexId());
			Vertex vj = graph.vertex(graph.halfEdge(boundary.get(j)).getOriginVertexId());
			if (isPointOnSegment(point, vi, vj)) {
				return true;
			}
			boolean intersects = ((vi.getY() > point.y) != (vj.getY() > point.y))
					&& (point.x < (vj.getX() - vi.getX()) * (point.y - vi.getY())
					/ (vj.getY() - vi.getY()) + vi.getX());
			if (intersects) {
				inside = !inside;
			}
		}
		return inside;
	}

	private boolean isPointOnSegment(GPoint2D point, Vertex first, Vertex second) {
		double cross = (point.y - first.getY()) * (second.getX() - first.getX())
				- (point.x - first.getX()) * (second.getY() - first.getY());
		if (Math.abs(cross) > 1e-9) {
			return false;
		}
		double dot = (point.x - first.getX()) * (point.x - second.getX())
				+ (point.y - first.getY()) * (point.y - second.getY());
		return dot <= 0;
	}

	private String hierarchySignal(PlanarGraph graph) {
		List<BoundaryCycle> canonical = graph.getLastCanonicalBoundaryCycles();
		BoundaryCycle dominantRoot = null;
		for (BoundaryCycle cycle : canonical) {
			if (cycle.getParentId() != -1 || isViewportLikeCycle(graph, cycle)) {
				continue;
			}
			if (dominantRoot == null || cycle.getChildIds().size()
					> dominantRoot.getChildIds().size()) {
				dominantRoot = cycle;
			}
		}
		if (dominantRoot == null) {
			return "no non-viewport canonical root";
		}
		return "dominantRoot={id=" + dominantRoot.getId()
				+ ", children=" + dominantRoot.getChildIds().size()
				+ ", bbox=" + boundaryBounds(graph, dominantRoot.getHalfEdgeIds())
				+ ", absArea=" + dominantRoot.getAbsArea()
				+ "} nonViewportCanonical=" + nonViewportCanonicalCycleCount(graph)
				+ " faces=" + graph.getFaces().size();
	}

	private boolean isViewportRegion(ClassifiedRegion region, EuclidianViewBounds bounds) {
		if (region.getOuterBoundary() == null || region.getOuterBoundary().getBounds() == null) {
			return false;
		}
		GRectangle regionBounds = region.getOuterBoundary().getBounds();
		return Math.abs(regionBounds.getWidth() - bounds.getWidth()) <= 1
				&& Math.abs(regionBounds.getHeight() - bounds.getHeight()) <= 1
				&& Math.abs(regionBounds.getX()) <= 1
				&& Math.abs(regionBounds.getY()) <= 1;
	}

	private String describeFaces(PlanarGraph graph) {
		StringBuilder sb = new StringBuilder("[");
		for (int i = 0; i < graph.getFaces().size(); i++) {
			Face face = graph.getFaces().get(i);
			if (i > 0) {
				sb.append(", ");
			}
			List<Integer> outerBoundary = face.getOuterHalfEdgeId() < 0
					? List.of() : graph.outerBoundaryOf(face);
			sb.append("{id=").append(face.getId())
					.append(", exterior=").append(face.isExterior())
					.append(", outer=").append(face.getOuterHalfEdgeId())
					.append(", outerCycle=").append(cycleIdOfBoundary(graph, outerBoundary))
					.append(", holes=").append(face.getHoleHalfEdgeIds().size())
					.append(", holeCycles=").append(holeCycleIds(graph, face))
					.append(", sample=").append(face.getSamplePoint());
			if (face.getOuterHalfEdgeId() >= 0) {
				sb.append(", bbox=").append(boundaryBounds(graph, outerBoundary));
			}
			sb.append('}');
		}
		sb.append(']');
		return sb.toString();
	}

	private int nonViewportCanonicalCycleCount(PlanarGraph graph) {
		int count = 0;
		for (BoundaryCycle cycle : graph.getLastCanonicalBoundaryCycles()) {
			if (!isViewportLikeCycle(graph, cycle)) {
				count++;
			}
		}
		return count;
	}

	private int filledRegionContaining(List<ClassifiedRegion> regions,
			EuclidianViewBounds bounds, double x, double y) {
		double screenX = bounds.toScreenCoordXd(x);
		double screenY = bounds.toScreenCoordYd(y);
		for (int i = 0; i < regions.size(); i++) {
			ClassifiedRegion region = regions.get(i);
			if (!region.isFilled()) {
				continue;
			}
			GArea area = areaOf(region);
			if (area.contains(screenX, screenY)) {
				return i;
			}
		}
		return -1;
	}

	private GArea areaOf(ClassifiedRegion region) {
		GArea area = AwtFactory.getPrototype().newArea(region.getOuterBoundary());
		for (GGeneralPath hole : region.getHoles()) {
			area.subtract(AwtFactory.getPrototype().newArea(hole));
		}
		return area;
	}

	private List<Integer> holeCycleIds(PlanarGraph graph, Face face) {
		return graph.holeBoundariesOf(face).stream()
				.map(boundary -> cycleIdOfBoundary(graph, boundary))
				.collect(Collectors.toList());
	}

	private int cycleIdOfBoundary(PlanarGraph graph, List<Integer> boundary) {
		if (boundary.isEmpty()) {
			return -1;
		}
		int startHalfEdgeId = boundary.get(0);
		for (BoundaryCycle cycle : graph.getLastCanonicalBoundaryCycles()) {
			if (cycle.getStartHalfEdgeId() == startHalfEdgeId) {
				return cycle.getId();
			}
		}
		Set<Integer> boundaryIds = new HashSet<>(boundary);
		for (BoundaryCycle cycle : graph.getLastCanonicalBoundaryCycles()) {
			if (boundaryIds.equals(new HashSet<>(cycle.getHalfEdgeIds()))) {
				return cycle.getId();
			}
		}
		return -1;
	}

	private int viewportCornerCount(PlanarGraph graph, BoundaryCycle cycle) {
		boolean[] present = new boolean[4];
		for (int halfEdgeId : cycle.getHalfEdgeIds()) {
			int vertexId = graph.halfEdge(halfEdgeId).getOriginVertexId();
			if (vertexId >= 0 && vertexId < present.length) {
				present[vertexId] = true;
			}
		}
		int count = 0;
		for (boolean value : present) {
			if (value) {
				count++;
			}
		}
		return count;
	}

	private boolean isViewportLikeCycle(PlanarGraph graph, BoundaryCycle cycle) {
		return viewportCornerCount(graph, cycle) == 4;
	}

	private String formatScreenBounds(ClassifiedRegion region) {
		return formatScreenBounds(region.getOuterBoundary().getBounds());
	}

	private String formatWorldBounds(ClassifiedRegion region, EuclidianViewBounds bounds) {
		return formatWorldBounds(region.getOuterBoundary().getBounds(), bounds);
	}

	private String formatScreenBounds(GRectangle rectangle) {
		if (rectangle == null) {
			return "null";
		}
		return "[" + rectangle.getX() + "," + (rectangle.getX() + rectangle.getWidth())
				+ "]x[" + rectangle.getY() + ","
				+ (rectangle.getY() + rectangle.getHeight()) + "]";
	}

	private String formatWorldBounds(GRectangle rectangle, EuclidianViewBounds bounds) {
		if (rectangle == null) {
			return "null";
		}
		double x1 = bounds.toRealWorldCoordX(rectangle.getX());
		double x2 = bounds.toRealWorldCoordX(rectangle.getX() + rectangle.getWidth());
		double y1 = bounds.toRealWorldCoordY(rectangle.getY());
		double y2 = bounds.toRealWorldCoordY(rectangle.getY() + rectangle.getHeight());
		return "[" + Math.min(x1, x2) + "," + Math.max(x1, x2)
				+ "]x[" + Math.min(y1, y2) + "," + Math.max(y1, y2) + "]";
	}

	private String cycleBoundsKey(PlanarGraph graph, BoundaryCycle cycle) {
		double minX = Double.POSITIVE_INFINITY;
		double maxX = Double.NEGATIVE_INFINITY;
		double minY = Double.POSITIVE_INFINITY;
		double maxY = Double.NEGATIVE_INFINITY;
		for (int halfEdgeId : cycle.getHalfEdgeIds()) {
			HalfEdge edge = graph.halfEdge(halfEdgeId);
			Vertex origin = graph.vertex(edge.getOriginVertexId());
			Vertex target = graph.vertex(edge.getTargetVertexId());
			minX = Math.min(minX, Math.min(origin.getX(), target.getX()));
			maxX = Math.max(maxX, Math.max(origin.getX(), target.getX()));
			minY = Math.min(minY, Math.min(origin.getY(), target.getY()));
			maxY = Math.max(maxY, Math.max(origin.getY(), target.getY()));
		}
		return rounded(minX) + "," + rounded(maxX)
				+ "x" + rounded(minY) + "," + rounded(maxY);
	}

	private double rounded(double value) {
		return Math.rint(value * 1e9) / 1e9;
	}

	private String boundaryBounds(PlanarGraph graph, List<Integer> boundary) {
		if (boundary.isEmpty()) {
			return "[]";
		}
		double minX = Double.POSITIVE_INFINITY;
		double maxX = Double.NEGATIVE_INFINITY;
		double minY = Double.POSITIVE_INFINITY;
		double maxY = Double.NEGATIVE_INFINITY;
		for (int halfEdgeId : boundary) {
			HalfEdge edge = graph.halfEdge(halfEdgeId);
			Vertex origin = graph.vertex(edge.getOriginVertexId());
			Vertex target = graph.vertex(edge.getTargetVertexId());
			minX = Math.min(minX, Math.min(origin.getX(), target.getX()));
			maxX = Math.max(maxX, Math.max(origin.getX(), target.getX()));
			minY = Math.min(minY, Math.min(origin.getY(), target.getY()));
			maxY = Math.max(maxY, Math.max(origin.getY(), target.getY()));
		}
		return "[" + minX + "," + maxX + "]x[" + minY + "," + maxY + "]";
	}

	private record WrongFillDiagnostics(List<ProbeDiagnostic> probes,
			List<ProbeTopologyDiagnostic> probeTopology,
			List<RegionDiagnostic> regions, List<FaceDiagnostic> faces,
			List<CycleRoleDiagnostic> cycleRoles, List<CycleDiagnostic> extractedCycles,
			List<CycleDiagnostic> canonicalCycles, List<CycleGroupDiagnostic> cycleGroups,
			String hierarchySignal) {
	}

	private record CycleDiagnostic(int id, double signedArea, double absArea,
			int edgeCount, int parentId, int depth, List<Integer> childIds,
			String bbox, int viewportCornerCount, boolean viewportLike,
			GPoint2D containmentProbe) {
	}

	private record CycleGroupDiagnostic(String boundsKey, List<Integer> extractedIds,
			List<Double> extractedSigns, List<Integer> canonicalIds,
			List<Double> canonicalSigns) {
	}

	private record FaceDiagnostic(int id, boolean exterior, int outerEdgeId,
			int outerCycleId, List<Integer> holeEdgeIds, List<Integer> holeCycleIds,
			GPoint2D samplePoint, String bbox) {
	}

	private record CycleRoleDiagnostic(int id, int parentId, int depth, boolean root,
			boolean exteriorHole, boolean nonExteriorOuter, boolean nonExteriorHole,
			int startHalfEdgeId, String bbox) {
	}

	private record RegionDiagnostic(int index, int sourceFaceId, boolean filled,
			GPoint2D samplePoint, Boolean predicateResult, int holeCount,
			String screenBbox, String worldBbox, boolean viewportLike,
			int trueProbeCount, int falseProbeCount, boolean mixedPredicate,
			GPoint2D firstTrueProbe, GPoint2D firstFalseProbe) {
	}

	private record ProbeDiagnostic(double worldX, double worldY, double screenX,
			double screenY, boolean expectedPredicate, boolean containedInFinalFill,
			int owningFilledRegionIndex) {
	}

	private record ProbeTopologyDiagnostic(double worldX, double worldY,
			List<Integer> containingCanonicalCycleIds,
			List<Integer> containingNonExteriorFaceIds,
			boolean exteriorFaceContainsByHoleModel) {
	}

	private record MixedProbeStats(int trueProbeCount, int falseProbeCount,
			GPoint2D firstTrueProbe, GPoint2D firstFalseProbe) {
		boolean mixedPredicate() {
			return trueProbeCount > 0 && falseProbeCount > 0;
		}
	}
}
