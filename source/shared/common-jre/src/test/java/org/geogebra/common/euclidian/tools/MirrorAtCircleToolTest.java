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

package org.geogebra.common.euclidian.tools;

import static org.geogebra.common.euclidian.EuclidianConstants.MODE_MIRROR_AT_CIRCLE;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;

import org.geogebra.common.kernel.algos.AlgoConicPartCircumcircle;
import org.geogebra.common.kernel.geos.GeoConic;
import org.geogebra.common.kernel.geos.GeoConicPart;
import org.geogebra.common.kernel.geos.GeoCurveCartesian;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.geos.GeoImage;
import org.geogebra.common.kernel.geos.GeoLine;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.geos.GeoPolygon;
import org.geogebra.common.kernel.geos.GeoSegment;
import org.geogebra.common.kernel.kernelND.GeoConicNDConstants;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

/** Tests for circle inversion with the reflection tool. */
class MirrorAtCircleToolTest extends BaseMirrorToolTest {

	@BeforeEach
	void setMode() {
		setMode(MODE_MIRROR_AT_CIRCLE);
	}

	@ParameterizedTest
	@CsvSource({"4, -3, 7, -3", "7, -3, 4, -3", "5, -3, 5, -3", "4, -1, 3.8, -1.4"})
	void pointThenCircleShouldInvertInteriorExteriorAndBoundaryPoints(
			double x, double y, double expectedX, double expectedY) {
		GeoPoint point = add("A = (" + x + "," + y + ")");
		GeoConic mirror = addMirrorCircle();
		Set<String> existingObjects = getObjectNames();

		clickRW(x, y);
		assertEquals(existingObjects, getObjectNames());
		clickRW(3, -5);

		GeoPoint inverted = getOnlyNewObject(existingObjects, GeoPoint.class);
		assertOnlyNewObjects(existingObjects, inverted);
		assertPoint(inverted, expectedX, expectedY);
		assertPoint(point, x, y);
		assertMirrorInputs(inverted, point, mirror);
	}

	@Test
	void invertedPointShouldStayOnSameRayAndHaveReciprocalDistance() {
		GeoPoint point = add("A = (4, -1)");
		GeoConic mirror = addMirrorCircle();
		Set<String> existingObjects = getObjectNames();

		clickRW(4, -1);
		clickRW(3, -5);

		GeoPoint inverted = getOnlyNewObject(existingObjects, GeoPoint.class);
		assertOnlyNewObjects(existingObjects, inverted);
		assertTrue(inverted.isDefined());
		double dx = point.getInhomX() - 3;
		double dy = point.getInhomY() + 3;
		double invertedDx = inverted.getInhomX() - 3;
		double invertedDy = inverted.getInhomY() + 3;
		assertAll(
				() -> assertEquals(0, dx * invertedDy - dy * invertedDx, 1E-8),
				() -> assertTrue(dx * invertedDx + dy * invertedDy > 0),
				() -> assertEquals(4, Math.hypot(dx, dy) * Math.hypot(invertedDx, invertedDy), 1E-8),
				() -> assertPoint(point, 4, -1),
				() -> assertCircle(mirror, 3, -3, 2),
				() -> assertMirrorInputs(inverted, point, mirror));
	}

	@Test
	void pointAtInversionCenterShouldBeUndefinedAndRecoverWhenMoved() {
		GeoPoint point = add("A = (3, -3)");
		GeoConic mirror = addMirrorCircle();
		Set<String> existingObjects = getObjectNames();

		clickRW(3, -3);
		clickRW(3, -5);

		GeoPoint inverted = getOnlyNewObject(existingObjects, GeoPoint.class);
		assertOnlyNewObjects(existingObjects, inverted);
		assertFalse(inverted.isDefined());
		assertMirrorInputs(inverted, point, mirror);
		point.setCoords(7, -3, 1);
		point.updateCascade();
		assertPoint(inverted, 4, -3);
	}

	@Test
	void inversionShouldBeAnInvolution() {
		GeoPoint point = add("A = (7, -3)");
		GeoConic mirror = addMirrorCircle();
		Set<String> beforeFirstInversion = getObjectNames();
		clickRW(7, -3);
		clickRW(3, -5);
		GeoPoint inverted = getOnlyNewObject(beforeFirstInversion, GeoPoint.class);
		assertOnlyNewObjects(beforeFirstInversion, inverted);
		assertMirrorInputs(inverted, point, mirror);
		assertPoint(inverted, 4, -3);
		Set<String> existingObjects = getObjectNames();

		clickRW(4, -3);
		clickRW(3, -5);

		GeoPoint restored = getOnlyNewObject(existingObjects, GeoPoint.class);
		assertOnlyNewObjects(existingObjects, restored);
		assertPoint(restored, 7, -3);
		assertMirrorInputs(restored, inverted, mirror);
		assertPoint(point, 7, -3);
		assertPoint(inverted, 4, -3);
		assertCircle(mirror, 3, -3, 2);
	}

	@Test
	void invertedPointShouldFollowSourceCenterRadiusAndUndefinedInputs() {
		GeoPoint point = add("A = (7, -3)");
		GeoPoint center = add("O = (3, -3)");
		GeoNumeric radius = add("r = 2");
		GeoConic mirror = add("c = Circle(O, r)");
		Set<String> existingObjects = getObjectNames();
		clickRW(7, -3);
		clickRW(3, -5);
		GeoPoint inverted = getOnlyNewObject(existingObjects, GeoPoint.class);
		assertOnlyNewObjects(existingObjects, inverted);
		assertMirrorInputs(inverted, point, mirror);
		assertPoint(inverted, 4, -3);

		point.setCoords(5, -3, 1);
		point.updateCascade();
		assertPoint(inverted, 5, -3);
		radius.setValue(4);
		radius.updateCascade();
		assertPoint(inverted, 11, -3);
		center.setCoords(4, -3, 1);
		center.updateCascade();
		assertPoint(inverted, 20, -3);

		center.setUndefined();
		center.updateCascade();
		assertFalse(inverted.isDefined());
		center.setCoords(3, -3, 1);
		center.updateCascade();
		assertPoint(inverted, 11, -3);
		point.setUndefined();
		point.updateCascade();
		assertFalse(inverted.isDefined());
		point.setCoords(7, -3, 1);
		point.updateCascade();
		assertPoint(inverted, 7, -3);
	}

	@Test
	void emptyClickShouldCreateSourcePoint() {
		GeoConic mirror = addMirrorCircle();
		Set<String> existingObjects = getObjectNames();

		clickRW(7, -3);
		clickRW(3, -5);

		GeoPoint point = assertInstanceOf(GeoPoint.class, lookup("A"));
		GeoPoint inverted = assertInstanceOf(GeoPoint.class, lookup("A'"));
		assertOnlyNewObjects(existingObjects, point, inverted);
		assertPoint(point, 7, -3);
		assertPoint(inverted, 4, -3);
		assertTrue(point.isIndependent());
		assertMirrorInputs(inverted, point, mirror);
	}

	@ParameterizedTest
	@CsvSource({"x = 5, 5, -1, 4, -3", "y = -1, 7, -1, 3, -2"})
	void lineNotThroughCenterShouldBecomeCircle(
			@NonNull String equation, double clickX, double clickY, double centerX, double centerY) {
		GeoLine line = add("g: " + equation);
		GeoConic mirror = addMirrorCircle();
		Set<String> existingObjects = getObjectNames();

		clickRW(clickX, clickY);
		clickRW(3, -5);

		GeoConic inverted = getOnlyNewObject(existingObjects, GeoConic.class);
		assertOnlyNewObjects(existingObjects, inverted);
		assertCircle(inverted, centerX, centerY, 1);
		assertMirrorInputs(inverted, line, mirror);
	}

	@Test
	void lineThroughCenterShouldRemainLineConic() {
		GeoLine line = add("g: x = 3");
		GeoConic mirror = addMirrorCircle();
		Set<String> existingObjects = getObjectNames();

		clickRW(3, -0.5);
		clickRW(3, -5);

		GeoConic inverted = getOnlyNewObject(existingObjects, GeoConic.class);
		assertOnlyNewObjects(existingObjects, inverted);
		assertEquals(GeoConicNDConstants.CONIC_LINE, inverted.getType());
		assertLineThrough(inverted.getLines()[0], 3, -1);
		assertLineThrough(inverted.getLines()[0], 3, -4);
		assertMirrorInputs(inverted, line, mirror);

		line.setCoords(1, 0, -5);
		line.updateCascade();
		assertCircle(inverted, 4, -3, 1);
		line.setCoords(1, 0, -3);
		line.updateCascade();
		assertEquals(GeoConicNDConstants.CONIC_LINE, inverted.getType());
		assertLineThrough(inverted.getLines()[0], 3, -1);
		assertLineThrough(inverted.getLines()[0], 3, -4);
		assertOnlyNewObjects(existingObjects, inverted);
	}

	@ParameterizedTest
	@ValueSource(booleans = {true, false})
	void secondSelectedCircleShouldBeMirror(boolean smallCircleFirst) {
		GeoConic smallCircle = add("s: (x - 6)^2 + (y + 3)^2 = 1");
		GeoConic largeCircle = addMirrorCircle();
		Set<String> existingObjects = getObjectNames();

		if (smallCircleFirst) {
			clickRW(7, -3);
			clickRW(3, -5);
		} else {
			clickRW(3, -5);
			clickRW(7, -3);
		}

		GeoConic inverted = getOnlyNewObject(existingObjects, GeoConic.class);
		assertOnlyNewObjects(existingObjects, inverted);
		assertCircle(inverted, smallCircleFirst ? 4.5 : 5.4, -3, smallCircleFirst ? 0.5 : 0.4);
		assertMirrorInputs(
				inverted,
				smallCircleFirst ? smallCircle : largeCircle,
				smallCircleFirst ? largeCircle : smallCircle);
	}

	@Test
	void concentricCircleShouldHaveReciprocalRadius() {
		GeoConic source = add("s: (x - 3)^2 + (y + 3)^2 = 1");
		GeoConic mirror = addMirrorCircle();
		Set<String> existingObjects = getObjectNames();

		clickRW(3, -2);
		clickRW(3, -5);

		GeoConic inverted = getOnlyNewObject(existingObjects, GeoConic.class);
		assertOnlyNewObjects(existingObjects, inverted);
		assertCircle(inverted, 3, -3, 4);
		assertMirrorInputs(inverted, source, mirror);
	}

	@Test
	void circleThroughCenterShouldBecomeLineConic() {
		GeoPoint sourceCenter = add("S = (4, -3)");
		GeoConic source = add("s = Circle(S, 1)");
		GeoConic mirror = addMirrorCircle();
		Set<String> existingObjects = getObjectNames();

		clickRW(4, -2);
		clickRW(3, -5);

		GeoConic inverted = getOnlyNewObject(existingObjects, GeoConic.class);
		assertOnlyNewObjects(existingObjects, inverted);
		assertEquals(GeoConicNDConstants.CONIC_LINE, inverted.getType());
		assertLineThrough(inverted.getLines()[0], 5, -1);
		assertLineThrough(inverted.getLines()[0], 5, -4);
		assertMirrorInputs(inverted, source, mirror);

		sourceCenter.setCoords(5, -3, 1);
		sourceCenter.updateCascade();
		assertCircle(inverted, 17.0 / 3, -3, 4.0 / 3);
		sourceCenter.setCoords(4, -3, 1);
		sourceCenter.updateCascade();
		assertEquals(GeoConicNDConstants.CONIC_LINE, inverted.getType());
		assertLineThrough(inverted.getLines()[0], 5, -1);
		assertLineThrough(inverted.getLines()[0], 5, -4);
		assertOnlyNewObjects(existingObjects, inverted);
	}

	@Test
	void segmentNotThroughCenterShouldBecomeLimitedArc() {
		add("A = (5, -3)");
		add("B = (5, -1)");
		GeoSegment source = add("s = Segment(A, B)");
		GeoConic mirror = addMirrorCircle();
		Set<String> existingObjects = getObjectNames();

		clickRW(5, -2);
		clickRW(3, -5);

		GeoConicPart inverted = getOnlyNewObject(existingObjects, GeoConicPart.class);
		assertOnlyNewObjects(existingObjects, inverted);
		assertMirrorInputs(inverted, source, mirror);
		assertCircle(inverted, 4, -3, 1);
		assertOnArc(inverted, 5, -3, true);
		assertOnArc(inverted, 4, -2, true);
		assertOnArc(inverted, 3, -3, false);
	}

	@Test
	void rayShouldBecomeArcWithInvertedDefiningPoints() {
		add("A = (5, -3)");
		add("B = (5, -1)");
		add("ray = Ray(A, B)");
		GeoConic mirror = addMirrorCircle();
		Set<String> existingObjects = getObjectNames();

		clickRW(5, -2);
		clickRW(3, -5);

		GeoConicPart inverted = getOnlyNewObject(existingObjects, GeoConicPart.class);
		GeoPoint firstInverted = assertInstanceOf(GeoPoint.class, lookup("A'"));
		GeoPoint secondInverted = assertInstanceOf(GeoPoint.class, lookup("B'"));
		assertOnlyNewObjects(existingObjects, inverted, firstInverted, secondInverted);
		assertPoint(firstInverted, 5, -3);
		assertPoint(secondInverted, 4, -2);
		assertMirrorInputs(firstInverted, lookup("A"), mirror);
		assertMirrorInputs(secondInverted, lookup("B"), mirror);
		AlgoConicPartCircumcircle arcAlgorithm =
				assertInstanceOf(AlgoConicPartCircumcircle.class, inverted.getParentAlgorithm());
		assertSame(firstInverted, arcAlgorithm.getInput()[0]);
		assertSame(secondInverted, arcAlgorithm.getInput()[1]);
		assertCircle(inverted, 4, -3, 1);
		assertOnArc(inverted, 5, -3, true);
		assertOnArc(inverted, 4, -2, true);
		// Image of (5, 1), beyond B on the source ray: a segment arc would omit this.
		assertOnArc(inverted, 3.4, -2.2, true);
		assertOnArc(inverted, 4, -4, false);
	}

	@Test
	void imageShouldNotBeSelectedAsInversionSource() {
		GeoImage image = createImage();
		image.setLabel("image");
		image.setAbsoluteScreenLocActive(true);
		image.setAbsoluteScreenLoc(300, 100);
		image.setEuclidianVisible(true);
		image.updateRepaint();
		GeoPoint point = add("A = (7, -3)");
		GeoConic mirror = addMirrorCircle();
		Set<String> existingObjects = getObjectNames();

		moveMouseRW(6.5, -1.5);
		assertTrue(getApp().getActiveEuclidianView().getHits().contains(image));
		assertFalse(ec.getHighlightedgeos().contains(image));
		clickRW(6.5, -1.5);
		assertEquals(existingObjects, getObjectNames());
		clickRW(7, -3);
		clickRW(3, -5);

		GeoPoint inverted = getOnlyNewObject(existingObjects, GeoPoint.class);
		assertOnlyNewObjects(existingObjects, inverted);
		assertPoint(inverted, 4, -3);
		assertMirrorInputs(inverted, point, mirror);
	}

	@Test
	void functionShouldBecomeInvertedCurve() {
		GeoFunction source = add("f(x) = x^2 - 5");
		GeoConic mirror = addMirrorCircle();
		Set<String> existingObjects = getObjectNames();

		clickRW(0.5, -4.75);
		clickRW(3, -5);

		GeoCurveCartesian inverted = getOnlyNewObject(existingObjects, GeoCurveCartesian.class);
		assertOnlyNewObjects(existingObjects, inverted);
		assertMirrorInputs(inverted, source, mirror);
		assertCurvePoint(inverted, 0, 27.0 / 13, -47.0 / 13);
		assertCurvePoint(inverted, 1, 1.4, -3.8);
		assertCurvePoint(inverted, 2, 2.2, -1.4);
		assertEquals(-4, source.value(1), 1E-8);
		assertCircle(mirror, 3, -3, 2);
	}

	@ParameterizedTest
	@ValueSource(booleans = {false, true})
	void polygonShouldBecomeCurveWithoutSeparateInvertedVertices(boolean selectPointFirst) {
		GeoPoint extraPoint = add("P = (7, -1)");
		add("A = (1, -1)");
		add("B = (2, -1)");
		add("C = (1, -3)");
		GeoPolygon source = add("p = Polygon(A, B, C)");
		GeoConic mirror = addMirrorCircle();
		Set<String> existingObjects = getObjectNames();

		if (selectPointFirst) {
			clickRW(7, -1);
		}
		clickRW(1.25, -1.5);
		assertTrue(getApp().getSelectionManager().containsSelectedGeo(source));
		clickRW(3, -5);

		GeoCurveCartesian inverted = getOnlyNewObject(existingObjects, GeoCurveCartesian.class);
		assertOnlyNewObjects(existingObjects, inverted);
		assertMirrorInputs(inverted, source, mirror);
		assertCurvePoint(inverted, 0, 2, -2);
		assertCurvePoint(inverted, 1, 2.2, -1.4);
		assertCurvePoint(inverted, 2, 1, -3);
		assertCurvePoint(inverted, 0.5, 2.04, -1.72);
		assertCurvePoint(inverted, 1.5, 15.0 / 13, -23.0 / 13);
		assertCurvePoint(inverted, 2.5, 1.4, -2.2);
		assertCurvePoint(inverted, 3, 2, -2);
		assertPoint(source.getPoint(0), 1, -1);
		assertPoint(source.getPoint(1), 2, -1);
		assertPoint(source.getPoint(2), 1, -3);
		assertPoint(extraPoint, 7, -1);
	}

	@Test
	void rectangleSelectionShouldInvertAllPointsAndExcludeMirrorCircle() {
		GeoConic mirror = addMirrorCircle();
		GeoPoint first = add("A = (6, -2)");
		GeoPoint second = add("B = (7, -4)");
		Set<String> existingObjects = getObjectNames();

		dragRW(0.5, -0.5, 7.5, -5.5);
		clickRW(3, -5);

		GeoPoint firstInverted = assertInstanceOf(GeoPoint.class, lookup("A'"));
		GeoPoint secondInverted = assertInstanceOf(GeoPoint.class, lookup("B'"));
		assertOnlyNewObjects(existingObjects, firstInverted, secondInverted);
		assertPoint(firstInverted, 4.2, -2.6);
		assertPoint(secondInverted, 67.0 / 17, -55.0 / 17);
		assertMirrorInputs(firstInverted, first, mirror);
		assertMirrorInputs(secondInverted, second, mirror);
	}

	@Test
	void listShouldInvertEveryPoint() {
		GeoList source = add("L = {(6, -2), (7, -4)}");
		GeoPoint first = assertInstanceOf(GeoPoint.class, source.get(0));
		GeoPoint second = assertInstanceOf(GeoPoint.class, source.get(1));
		source.setEuclidianVisible(true);
		source.updateRepaint();
		GeoConic mirror = addMirrorCircle();
		Set<String> existingObjects = getObjectNames();

		clickRW(6, -2);
		clickRW(3, -5);

		GeoList inverted = getOnlyNewObject(existingObjects, GeoList.class);
		assertOnlyNewObjects(existingObjects, inverted);
		assertMirrorInputs(inverted, source, mirror);
		assertEquals(2, inverted.size());
		assertPoint(assertInstanceOf(GeoPoint.class, inverted.get(0)), 4.2, -2.6);
		assertPoint(assertInstanceOf(GeoPoint.class, inverted.get(1)), 67.0 / 17, -55.0 / 17);
		assertNotSame(first, inverted.get(0));
		assertNotSame(second, inverted.get(1));
		assertPoint(first, 6, -2);
		assertPoint(second, 7, -4);
		first.setCoords(5, -3, 1);
		source.updateCascade();
		assertPoint(assertInstanceOf(GeoPoint.class, inverted.get(0)), 5, -3);
		assertPoint(assertInstanceOf(GeoPoint.class, inverted.get(1)), 67.0 / 17, -55.0 / 17);
	}

	@Test
	void nonCircularConicShouldNotBeUsedAsMirror() {
		GeoPoint point = add("A = (7, -4)");
		add("e: (x - 3)^2 / 4 + (y + 3)^2 = 1");
		GeoConic mirror = addMirrorCircle();
		Set<String> existingObjects = getObjectNames();

		clickRW(7, -4);
		clickRW(3, -2);
		assertEquals(existingObjects, getObjectNames());
		clickRW(7, -4);
		clickRW(3, -5);

		GeoPoint inverted = getOnlyNewObject(existingObjects, GeoPoint.class);
		assertOnlyNewObjects(existingObjects, inverted);
		assertPoint(inverted, 67.0 / 17, -55.0 / 17);
		assertMirrorInputs(inverted, point, mirror);
	}

	@Test
	void hoveringShouldNotCreateObjectsOrChooseMirror() {
		GeoPoint point = add("A = (7, -3)");
		GeoConic mirror = addMirrorCircle();
		GeoConic otherMirror = add("d: (x - 7)^2 + (y + 5)^2 = 1");
		Set<String> existingObjects = getObjectNames();

		moveMouseRW(7, -3);
		assertTrue(ec.getHighlightedgeos().contains(point));
		moveMouseRW(3, -5);
		assertTrue(ec.getHighlightedgeos().contains(mirror));
		assertEquals(existingObjects, getObjectNames());
		clickRW(7, -3);
		moveMouseRW(7, -4);
		assertTrue(ec.getHighlightedgeos().contains(otherMirror));
		assertTrue(getApp().getSelectionManager().containsSelectedGeo(point));
		assertEquals(existingObjects, getObjectNames());
		clickRW(3, -5);

		GeoPoint inverted = getOnlyNewObject(existingObjects, GeoPoint.class);
		assertOnlyNewObjects(existingObjects, inverted);
		assertPoint(inverted, 4, -3);
		assertMirrorInputs(inverted, point, mirror);
	}

	@Test
	void clickingSelectedCircleAgainShouldSkipSelfInversionAndResetSelection() {
		GeoConic mirror = addMirrorCircle();
		GeoPoint point = add("A = (7, -3)");
		Set<String> existingObjects = getObjectNames();

		clickRW(3, -5);
		resetMouseLocation();
		clickRW(3, -5);
		assertEquals(existingObjects, getObjectNames());
		clickRW(7, -3);
		assertEquals(existingObjects, getObjectNames());
		clickRW(3, -5);

		GeoPoint inverted = getOnlyNewObject(existingObjects, GeoPoint.class);
		assertOnlyNewObjects(existingObjects, inverted);
		assertPoint(inverted, 4, -3);
		assertMirrorInputs(inverted, point, mirror);
	}

	private @NonNull GeoConic addMirrorCircle() {
		return add("mirror: (x - 3)^2 + (y + 3)^2 = 4");
	}

	private void assertOnArc(@NonNull GeoConicPart arc, double x, double y, boolean expected) {
		GeoPoint probe = new GeoPoint(getKernel().getConstruction());
		probe.setCoords(x, y, 1);
		assertTrue(arc.isDefined());
		assertEquals(
				expected, arc.isOnPath(probe, 1E-8), () -> "Arc membership at (" + x + ", " + y + ")");
	}

	private void assertCircle(@NonNull GeoConic circle, double x, double y, double radius) {
		assertAll(
				() -> assertTrue(circle.isDefined()),
				() -> assertTrue(circle.isCircle()),
				() -> assertEquals(x, circle.getMidpoint().getX(), 1E-8),
				() -> assertEquals(y, circle.getMidpoint().getY(), 1E-8),
				() -> assertEquals(radius, circle.getCircleRadius(), 1E-8));
	}

	private void assertCurvePoint(
			@NonNull GeoCurveCartesian curve, double parameter, double x, double y) {
		double[] coordinates = new double[2];
		curve.evaluateCurve(parameter, coordinates);
		assertAll(
				() -> assertTrue(curve.isDefined()),
				() -> assertEquals(x, coordinates[0], 1E-8),
				() -> assertEquals(y, coordinates[1], 1E-8));
	}
}
