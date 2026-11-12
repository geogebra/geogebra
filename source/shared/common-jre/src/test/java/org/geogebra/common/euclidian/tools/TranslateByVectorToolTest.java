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

import static org.geogebra.common.euclidian.EuclidianConstants.MODE_MOVE;
import static org.geogebra.common.euclidian.EuclidianConstants.MODE_TRANSLATE_BY_VECTOR;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;

import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.kernel.algos.AlgoTranslate;
import org.geogebra.common.kernel.algos.AlgoVector;
import org.geogebra.common.kernel.geos.GeoConic;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.geos.GeoLine;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.geos.GeoPolyLine;
import org.geogebra.common.kernel.geos.GeoPolygon;
import org.geogebra.common.kernel.geos.GeoRay;
import org.geogebra.common.kernel.geos.GeoSegment;
import org.geogebra.common.kernel.geos.GeoVector;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.kernel.matrix.Coords;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

/** Tests for translating objects using an existing vector, two points or a drag. */
class TranslateByVectorToolTest extends BaseTransformationToolTest {

	@BeforeEach
	void setMode() {
		setMode(MODE_TRANSLATE_BY_VECTOR);
		EuclidianView view = getApp().getActiveEuclidianView();
		view.setCoordSystem(250, 250, 50, 50);
	}

	@Test
	void pointThenVectorShouldTranslatePointWithoutMovingSource() {
		GeoPoint point = add("A = (1, -2)");
		GeoVector vector = addVector();
		Set<String> existingObjects = getObjectNames();

		clickRW(1, -2);
		assertEquals(existingObjects, getObjectNames());
		clickRW(5, -1.5);

		GeoPoint translated = getOnlyNewObject(existingObjects, GeoPoint.class);
		assertOnlyNewObjects(existingObjects, translated);
		assertPoint(translated, 3, -3);
		assertPoint(point, 1, -2);
		assertTranslationInputs(translated, point, vector);
	}

	@ParameterizedTest
	@CsvSource({"2, 1, 3, -2", "-2, -1, -1, -4", "0, 2, 1, -1", "-2, 0, -1, -3"})
	void vectorComponentsShouldControlTranslationDirection(
			double dx, double dy, double expectedX, double expectedY) {
		GeoPoint point = add("A = (1, -3)");
		GeoVector vector = add("u = Vector((4, -1), (" + (4 + dx) + "," + (-1 + dy) + "))");
		Set<String> existingObjects = getObjectNames();

		clickRW(1, -3);
		clickRW(4 + dx / 2, -1 + dy / 2);

		GeoPoint translated = getOnlyNewObject(existingObjects, GeoPoint.class);
		assertOnlyNewObjects(existingObjects, translated);
		assertPoint(translated, expectedX, expectedY);
		assertTranslationInputs(translated, point, vector);
	}

	@Test
	void displacementShouldEqualVectorComponents() {
		GeoPoint point = add("A = (1, -2)");
		GeoVector vector = addVector();
		Set<String> existingObjects = getObjectNames();
		clickRW(1, -2);
		clickRW(5, -1.5);

		GeoPoint translated = getOnlyNewObject(existingObjects, GeoPoint.class);
		assertOnlyNewObjects(existingObjects, translated);
		assertTrue(translated.isDefined());
		assertEquals(vector.getX(), translated.getInhomX() - point.getInhomX(), 1E-8);
		assertEquals(vector.getY(), translated.getInhomY() - point.getInhomY(), 1E-8);
		assertTranslationInputs(translated, point, vector);
		assertPoint(point, 1, -2);
		assertEquals(2, vector.getX(), 1E-8);
		assertEquals(-1, vector.getY(), 1E-8);
	}

	@Test
	void translatedPointShouldFollowSourceAndBothVectorEndpoints() {
		GeoPoint point = add("A = (1, -2)");
		GeoPoint tail = add("B = (4, -1)");
		GeoPoint head = add("C = (6, -2)");
		GeoVector vector = add("u = Vector(B, C)");
		Set<String> existingObjects = getObjectNames();
		clickRW(1, -2);
		clickRW(5, -1.5);
		GeoPoint translated = getOnlyNewObject(existingObjects, GeoPoint.class);
		assertOnlyNewObjects(existingObjects, translated);
		assertTranslationInputs(translated, point, vector);

		point.setCoords(2, -3, 1);
		point.updateCascade();
		assertPoint(translated, 4, -4);
		head.setCoords(7, -2, 1);
		head.updateCascade();
		assertPoint(translated, 5, -4);
		tail.setCoords(5, -1, 1);
		tail.updateCascade();
		assertPoint(translated, 4, -4);

		head.setUndefined();
		head.updateCascade();
		assertFalse(translated.isDefined());
		head.setCoords(5, -1, 1);
		head.updateCascade();
		assertPoint(translated, 2, -3);
		point.setUndefined();
		point.updateCascade();
		assertFalse(translated.isDefined());
		point.setCoords(1, -2, 1);
		point.updateCascade();
		assertPoint(translated, 1, -2);
	}

	@ParameterizedTest
	@ValueSource(booleans = {true, false})
	void twoPointsShouldDefineTranslationVector(boolean endpointsExist) {
		GeoPoint point = add("A = (1, -2)");
		if (endpointsExist) {
			add("B = (4, -1)");
			add("C = (6, -2)");
		}
		Set<String> existingObjects = getObjectNames();

		clickRW(1, -2);
		clickRW(4, -1);
		clickRW(6, -2);

		GeoVector vector = getOnlyNewObject(existingObjects, GeoVector.class);
		GeoPoint translated = assertInstanceOf(GeoPoint.class, lookup("A'"));
		GeoPoint tail = assertInstanceOf(GeoPoint.class, lookup("B"));
		GeoPoint head = assertInstanceOf(GeoPoint.class, lookup("C"));
		if (endpointsExist) {
			assertOnlyNewObjects(existingObjects, vector, translated);
		} else {
			assertOnlyNewObjects(existingObjects, vector, translated, tail, head);
		}
		assertVectorFromPoints(vector, tail, head);
		assertPoint(tail, 4, -1);
		assertPoint(head, 6, -2);
		assertTrue(tail.isIndependent());
		assertTrue(head.isIndependent());
		assertPoint(translated, 3, -3);
		assertTranslationInputs(translated, point, vector);
		tail.setCoords(3, -1, 1);
		tail.updateCascade();
		assertPoint(translated, 4, -3);
		head.setCoords(6, -4, 1);
		head.updateCascade();
		assertPoint(translated, 4, -5);
		assertPoint(point, 1, -2);
	}

	@Test
	void secondVectorShouldBeTranslationParameter() {
		GeoVector source = add("w = Vector((1, -2), (2, -3))");
		GeoVector vector = addVector();
		Set<String> existingObjects = getObjectNames();

		clickRW(1.5, -2.5);
		clickRW(5, -1.5);

		GeoVector translated = getOnlyNewObject(existingObjects, GeoVector.class);
		assertOnlyNewObjects(existingObjects, translated);
		assertTrue(translated.isDefined());
		assertEquals(1, translated.getX(), 1E-8);
		assertEquals(-1, translated.getY(), 1E-8);
		assertTranslationInputs(translated, source, vector);
	}

	@ParameterizedTest
	@CsvSource({"y = -3, 1, -3, 0, -4, 2, -4", "x + y = -1, 2, -3, 0, 0, 2, -2"})
	void lineShouldTranslateToParallelLine(
			@NonNull String equation,
			double clickX,
			double clickY,
			double x1,
			double y1,
			double x2,
			double y2) {
		GeoLine source = add("g: " + equation);
		GeoVector vector = addVector();
		Set<String> existingObjects = getObjectNames();
		clickRW(clickX, clickY);
		clickRW(5, -1.5);

		GeoLine translated = getOnlyNewObject(existingObjects, GeoLine.class);
		assertOnlyNewObjects(existingObjects, translated);
		assertLineThrough(translated, x1, y1);
		assertLineThrough(translated, x2, y2);
		assertTranslationInputs(translated, source, vector);
	}

	@Test
	void circleShouldTranslateCenterAndPreserveRadius() {
		GeoConic source = add("c: (x - 1)^2 + (y + 2)^2 = 1");
		GeoVector vector = addVector();
		Set<String> existingObjects = getObjectNames();
		clickRW(1, -1);
		clickRW(5, -1.5);

		GeoConic translated = getOnlyNewObject(existingObjects, GeoConic.class);
		assertOnlyNewObjects(existingObjects, translated);
		assertTrue(translated.isDefined());
		assertTrue(translated.isCircle());
		assertEquals(3, translated.getMidpoint().getX(), 1E-8);
		assertEquals(-3, translated.getMidpoint().getY(), 1E-8);
		assertEquals(1, translated.getCircleRadius(), 1E-8);
		assertTranslationInputs(translated, source, vector);
	}

	@Test
	void segmentShouldTranslateBothEndpointsAndPreserveLength() {
		add("A = (1, -1)");
		add("B = (2, -3)");
		GeoSegment source = add("s = Segment(A, B)");
		GeoVector vector = addVector();
		Set<String> existingObjects = getObjectNames();
		clickRW(1.5, -2);
		clickRW(5, -1.5);

		GeoSegment translated = getOnlyNewObject(existingObjects, GeoSegment.class);
		assertOnlyNewObjects(
				existingObjects, translated, translated.getStartPoint(), translated.getEndPoint());
		assertPoint(translated.getStartPoint(), 3, -2);
		assertPoint(translated.getEndPoint(), 4, -4);
		assertEquals(Math.sqrt(5), translated.getLength(), 1E-8);
		assertPoint(source.getStartPoint(), 1, -1);
		assertPoint(source.getEndPoint(), 2, -3);
		assertTranslationInputs(translated.getStartPoint(), lookup("A"), vector);
		assertTranslationInputs(translated.getEndPoint(), lookup("B"), vector);
	}

	@Test
	void rayShouldTranslateDefiningPointsAndPreserveDirection() {
		add("A = (1, -1)");
		add("B = (2, -2)");
		add("ray = Ray(A, B)");
		GeoVector vector = addVector();
		Set<String> existingObjects = getObjectNames();
		clickRW(1.5, -1.5);
		clickRW(5, -1.5);

		GeoRay translated = getOnlyNewObject(existingObjects, GeoRay.class);
		GeoPoint end = assertInstanceOf(GeoPoint.class, lookup("B'"));
		assertOnlyNewObjects(existingObjects, translated, translated.getStartPoint(), end);
		assertPoint(translated.getStartPoint(), 3, -2);
		assertPoint(end, 4, -3);
		assertTranslationInputs(translated.getStartPoint(), lookup("A"), vector);
		assertTranslationInputs(end, lookup("B"), vector);
		assertSame(end, translated.getParentAlgorithm().getInput()[1]);
		assertTrue(translated.isOnPath(new Coords(5, -4, 0, 1), 1E-8));
		assertFalse(translated.isOnPath(new Coords(2, -1, 0, 1), 1E-8));
	}

	@Test
	void functionShouldTranslateHorizontallyAndVertically() {
		GeoFunction source = add("f(x) = x^2 - 4");
		GeoVector vector = addVector();
		Set<String> existingObjects = getObjectNames();
		clickRW(0, -4);
		clickRW(5, -1.5);

		GeoFunction translated = getOnlyNewObject(existingObjects, GeoFunction.class);
		assertOnlyNewObjects(existingObjects, translated);
		assertTrue(translated.isDefined());
		assertEquals(-4, translated.value(1), 1E-8);
		assertEquals(-5, translated.value(2), 1E-8);
		assertEquals(-1, translated.value(4), 1E-8);
		assertTranslationInputs(translated, source, vector);
		assertEquals(-3, source.value(1), 1E-8);
		assertEquals(0, source.value(2), 1E-8);
	}

	@ParameterizedTest
	@CsvSource({"false, true", "true, true", "false, false", "true, false"})
	void polygonShouldTranslateWithExistingOrNewVector(
			boolean selectPointFirst, boolean vectorExists) {
		add("P = (1, -4)");
		GeoPolygon source = addTriangle();
		GeoPoint tail = add("D = (4, -1)");
		GeoPoint head = add("E = (6, -2)");
		GeoVector existingVector = vectorExists ? add("u = Vector(D, E)") : null;
		Set<String> existingObjects = getObjectNames();

		if (selectPointFirst) {
			clickRW(1, -4);
		}
		clickRW(1.25, -1.5);
		assertTrue(getApp().getSelectionManager().containsSelectedGeo(source));
		if (vectorExists) {
			clickRW(5, -1.5);
		} else {
			clickRW(4, -1);
			clickRW(6, -2);
		}

		GeoVector vector =
				vectorExists ? existingVector : getOnlyNewObject(existingObjects, GeoVector.class);
		GeoPolygon translated = getOnlyNewObject(existingObjects, GeoPolygon.class);
		assertVectorFromPoints(vector, tail, head);
		if (vectorExists) {
			assertOnlyNewPolygon(existingObjects, translated);
		} else {
			assertOnlyNewPolygon(existingObjects, translated, vector);
		}
		assertPolygonStructure(AlgoTranslate.class, translated, source, vector);
		assertPoint(translated.getPoint(0), 3, -2);
		assertPoint(translated.getPoint(1), 4, -2);
		assertPoint(translated.getPoint(2), 3, -4);
		assertEquals(1, translated.getArea(), 1E-8);
		assertPoint(source.getPoint(0), 1, -1);
		assertPoint(source.getPoint(1), 2, -1);
		assertPoint(source.getPoint(2), 1, -3);

		head.setCoords(7, -3, 1);
		head.updateCascade();
		assertPoint(translated.getPoint(0), 4, -3);
		assertPoint(translated.getPoint(1), 5, -3);
		assertPoint(translated.getPoint(2), 4, -5);
		source.getPoint(1).setCoords(3, -1, 1);
		source.getPoint(1).updateCascade();
		assertPoint(translated.getPoint(0), 4, -3);
		assertPoint(translated.getPoint(1), 6, -3);
		assertPoint(translated.getPoint(2), 4, -5);
		assertEquals(2, source.getArea(), 1E-8);
		assertEquals(2, translated.getArea(), 1E-8);
		assertEquals(2, translated.getSegments()[0].getLength(), 1E-8);
	}

	@Test
	void listShouldTranslateEveryElement() {
		GeoList source = add("L = {(1, -1), (2, -2)}");
		GeoPoint first = assertInstanceOf(GeoPoint.class, source.get(0));
		GeoPoint second = assertInstanceOf(GeoPoint.class, source.get(1));
		source.setEuclidianVisible(true);
		source.updateRepaint();
		GeoVector vector = addVector();
		Set<String> existingObjects = getObjectNames();
		clickRW(1, -1);
		clickRW(5, -1.5);

		GeoList translated = getOnlyNewObject(existingObjects, GeoList.class);
		assertOnlyNewObjects(existingObjects, translated);
		assertTranslationInputs(translated, source, vector);
		assertEquals(2, translated.size());
		assertPoint(assertInstanceOf(GeoPoint.class, translated.get(0)), 3, -2);
		assertPoint(assertInstanceOf(GeoPoint.class, translated.get(1)), 4, -3);
		assertNotSame(first, translated.get(0));
		assertNotSame(second, translated.get(1));
		assertPoint(first, 1, -1);
		assertPoint(second, 2, -2);
		first.setCoords(2, -1, 1);
		source.updateCascade();
		assertPoint(assertInstanceOf(GeoPoint.class, translated.get(0)), 4, -2);
		assertPoint(assertInstanceOf(GeoPoint.class, translated.get(1)), 4, -3);
	}

	@ParameterizedTest
	@ValueSource(booleans = {false, true})
	void rectangleShouldTranslateAllPointsWithExistingOrNewVector(boolean vectorExists) {
		GeoPoint first = add("A = (1, -1)");
		GeoPoint second = add("B = (2, -2)");
		GeoPoint tail = add("C = (4, -1)");
		GeoPoint head = add("D = (6, -2)");
		GeoVector existingVector = vectorExists ? add("u = Vector(C, D)") : null;
		Set<String> existingObjects = getObjectNames();
		dragRW(0.5, -0.5, 2.5, -2.5);
		if (vectorExists) {
			clickRW(5, -1.5);
		} else {
			clickRW(4, -1);
			clickRW(6, -2);
		}

		GeoPoint firstTranslated = assertInstanceOf(GeoPoint.class, lookup("A'"));
		GeoPoint secondTranslated = assertInstanceOf(GeoPoint.class, lookup("B'"));
		GeoVector vector =
				vectorExists ? existingVector : getOnlyNewObject(existingObjects, GeoVector.class);
		if (vectorExists) {
			assertOnlyNewObjects(existingObjects, firstTranslated, secondTranslated);
		} else {
			assertOnlyNewObjects(existingObjects, firstTranslated, secondTranslated, vector);
		}
		assertPoint(firstTranslated, 3, -2);
		assertPoint(secondTranslated, 4, -3);
		assertTranslationInputs(firstTranslated, first, vector);
		assertTranslationInputs(secondTranslated, second, vector);
		assertVectorFromPoints(vector, tail, head);
		tail.setCoords(3, -1, 1);
		tail.updateCascade();
		assertPoint(firstTranslated, 4, -2);
		assertPoint(secondTranslated, 5, -3);
	}

	@Test
	void hoveringShouldHighlightWithoutChoosingTranslationVector() {
		GeoPoint point = add("A = (1, -2)");
		GeoVector vector = addVector();
		GeoVector otherVector = add("v = Vector((5, -4), (7, -4))");
		Set<String> existingObjects = getObjectNames();
		moveMouseRW(1, -2);
		assertTrue(ec.getHighlightedgeos().contains(point));
		clickRW(1, -2);
		moveMouseRW(6, -4);
		assertTrue(ec.getHighlightedgeos().contains(otherVector));
		assertEquals(existingObjects, getObjectNames());
		clickRW(5, -1.5);

		GeoPoint translated = getOnlyNewObject(existingObjects, GeoPoint.class);
		assertOnlyNewObjects(existingObjects, translated);
		assertPoint(translated, 3, -3);
		assertTranslationInputs(translated, point, vector);
	}

	@Test
	void creatingVectorShouldRestoreRectangleSelectionForNextTranslation() {
		add("A = (1, -3)");
		GeoPoint first = add("B = (1, -1)");
		GeoPoint second = add("C = (2, -2)");
		add("T = (4, -1)");
		add("H = (6, -2)");
		Set<String> beforeFirstTranslation = getObjectNames();

		clickRW(1, -3);
		clickRW(4, -1);
		clickRW(6, -2);
		GeoVector vector = getOnlyNewObject(beforeFirstTranslation, GeoVector.class);
		GeoPoint firstResult = assertInstanceOf(GeoPoint.class, lookup("A'"));
		assertOnlyNewObjects(beforeFirstTranslation, vector, firstResult);
		assertPoint(firstResult, 3, -4);
		assertEquals(MODE_TRANSLATE_BY_VECTOR, getApp().getMode());
		Set<String> existingObjects = getObjectNames();

		dragRW(0.5, -0.5, 2.5, -2.5);
		clickRW(5, -1.5);

		GeoPoint firstTranslated = assertInstanceOf(GeoPoint.class, lookup("B'"));
		GeoPoint secondTranslated = assertInstanceOf(GeoPoint.class, lookup("C'"));
		assertOnlyNewObjects(existingObjects, firstTranslated, secondTranslated);
		assertPoint(firstTranslated, 3, -2);
		assertPoint(secondTranslated, 4, -3);
		assertTranslationInputs(firstTranslated, first, vector);
		assertTranslationInputs(secondTranslated, second, vector);
		assertPoint(firstResult, 3, -4);
	}

	@Test
	void clickingSelectedVectorAgainShouldSkipSelfTranslation() {
		GeoPoint point = add("A = (1, -2)");
		GeoVector vector = addVector();
		Set<String> existingObjects = getObjectNames();
		clickRW(5, -1.5);
		resetMouseLocation();
		clickRW(5, -1.5);
		assertEquals(existingObjects, getObjectNames());
		clickRW(1, -2);
		clickRW(5, -1.5);

		GeoPoint translated = getOnlyNewObject(existingObjects, GeoPoint.class);
		assertOnlyNewObjects(existingObjects, translated);
		assertPoint(translated, 3, -3);
		assertTranslationInputs(translated, point, vector);
	}

	@Test
	void draggingPointShouldCreateTranslatedCopyAndSwitchToMove() {
		GeoPoint point = add("A = (1, -2)");
		Set<String> existingObjects = getObjectNames();
		dragRW(1, -2, 3, -3);

		GeoPoint translated = getOnlyNewObject(existingObjects, GeoPoint.class);
		assertOnlyNewObjects(existingObjects, translated);
		assertPoint(translated, 3, -3);
		assertPoint(point, 1, -2);
		GeoVector vector =
				assertInstanceOf(GeoVector.class, translated.getParentAlgorithm().getInput()[1]);
		assertTranslationInputs(translated, point, vector);
		assertEquals(2, vector.getX(), 1E-8);
		assertEquals(-1, vector.getY(), 1E-8);
		assertEquals(MODE_MOVE, getApp().getMode());
		point.setCoords(2, -2, 1);
		point.updateCascade();
		assertPoint(translated, 4, -3);
	}

	@Test
	void draggingPolygonShouldUseOneSharedHiddenVector() {
		GeoPolygon source = addTriangle();
		Set<String> existingObjects = getObjectNames();
		dragRW(1.25, -1.5, 3.25, -2.5);

		GeoPolygon translated = getOnlyNewObject(existingObjects, GeoPolygon.class);
		GeoVector vector = getOnlyNewObject(existingObjects, GeoVector.class);
		assertOnlyNewPolygon(existingObjects, translated, vector);
		assertPolygonStructure(AlgoTranslate.class, translated, source, vector);
		assertPoint(translated.getPoint(0), 3, -2);
		assertPoint(translated.getPoint(1), 4, -2);
		assertPoint(translated.getPoint(2), 3, -4);
		assertFalse(vector.isEuclidianVisible());
		assertTrue(vector.isAuxiliaryObject());
		assertEquals(2, vector.getX(), 1E-8);
		assertEquals(-1, vector.getY(), 1E-8);
		assertPoint(source.getPoint(0), 1, -1);
		assertPoint(source.getPoint(1), 2, -1);
		assertPoint(source.getPoint(2), 1, -3);
		assertEquals(MODE_MOVE, getApp().getMode());
		vector.setCoords(-1, 2, 0);
		vector.updateCascade();
		assertPoint(translated.getPoint(0), 0, 1);
		assertPoint(translated.getPoint(1), 1, 1);
		assertPoint(translated.getPoint(2), 0, -1);
	}

	@Test
	void draggingVectorShouldTranslateItsEndpointsWithoutChangingComponents() {
		GeoPoint tail = add("A = (1, -1)");
		GeoPoint head = add("B = (3, -2)");
		GeoVector source = add("u = Vector(A, B)");
		Set<String> existingObjects = getObjectNames();

		dragRW(2, -1.5, 4, -2.5);

		GeoPoint translatedTail = assertInstanceOf(GeoPoint.class, lookup("A'"));
		GeoPoint translatedHead = assertInstanceOf(GeoPoint.class, lookup("B'"));
		AlgoTranslate tailAlgorithm =
				assertInstanceOf(AlgoTranslate.class, translatedTail.getParentAlgorithm());
		GeoVector displacement = assertInstanceOf(GeoVector.class, tailAlgorithm.getInput()[1]);
		GeoVector translated = getNewObjects(existingObjects, GeoVector.class).stream()
				.filter(vector -> vector != displacement)
				.findFirst()
				.orElseThrow();
		assertOnlyNewObjects(existingObjects, translatedTail, translatedHead, displacement, translated);
		assertTranslationInputs(translatedTail, tail, displacement);
		assertTranslationInputs(translatedHead, head, displacement);
		assertVectorFromPoints(translated, translatedTail, translatedHead);
		assertPoint(translatedTail, 3, -2);
		assertPoint(translatedHead, 5, -3);
		assertPoint(tail, 1, -1);
		assertPoint(head, 3, -2);
		assertEquals(source.getX(), translated.getX(), 1E-8);
		assertEquals(source.getY(), translated.getY(), 1E-8);
		assertFalse(displacement.isEuclidianVisible());
		assertTrue(displacement.isAuxiliaryObject());
		assertEquals(MODE_MOVE, getApp().getMode());
	}

	@ParameterizedTest
	@ValueSource(booleans = {false, true})
	void draggingVectorWithoutEndpointsShouldAnchorOneCopyAtDropPosition(boolean dependent) {
		GeoVector driver = add("v = (1, -0.5)");
		driver.setEuclidianVisible(false);
		driver.updateRepaint();
		GeoVector source = dependent ? add("u = 2v") : add("u = (2, -1)");
		Set<String> existingObjects = getObjectNames();

		dragRW(1, -0.5, 3, -1.5);

		GeoVector translated = getOnlyNewObject(existingObjects, GeoVector.class);
		assertTrue(translated.isDefined());
		assertEquals(2, translated.getX(), 1E-8);
		assertEquals(-1, translated.getY(), 1E-8);
		GeoPoint tail = assertInstanceOf(GeoPoint.class, translated.getStartPoint());
		GeoPoint head =
				assertInstanceOf(GeoPoint.class, translated.getParentAlgorithm().getInput()[1]);
		assertOnlyNewObjects(existingObjects, tail, head, translated);
		assertVectorFromPoints(translated, tail, head);
		assertTranslationInputs(head, tail, source);
		assertPoint(tail, 3, -1.5);
		assertPoint(head, 5, -2.5);
		assertFalse(tail.isEuclidianVisible());
		assertFalse(head.isEuclidianVisible());
		assertTrue(tail.isIndependent());
		assertEquals(MODE_TRANSLATE_BY_VECTOR, getApp().getMode());
		assertEquals(2, source.getX(), 1E-8);
		assertEquals(-1, source.getY(), 1E-8);
		assertNull(source.getStartPoint());
		if (dependent) {
			driver.setCoords(-0.5, 1, 0);
			driver.updateCascade();
		} else {
			source.setCoords(-1, 2, 0);
			source.updateCascade();
		}
		assertPoint(tail, 3, -1.5);
		assertPoint(head, 2, 0.5);
		assertEquals(-1, translated.getX(), 1E-8);
		assertEquals(2, translated.getY(), 1E-8);
		tail.setCoords(4, -2, 1);
		tail.updateCascade();
		assertPoint(head, 3, 0);
		assertEquals(-1, translated.getX(), 1E-8);
		assertEquals(2, translated.getY(), 1E-8);
	}

	@Test
	void draggingPolylineShouldTranslateAllVerticesWithOneSharedHiddenVector() {
		GeoPoint first = add("A = (1, -1)");
		GeoPoint second = add("B = (2, -1)");
		GeoPoint third = add("C = (2, -3)");
		GeoPolyLine source = add("p = Polyline(A, B, C)");
		Set<String> existingObjects = getObjectNames();

		dragRW(1.5, -1, 3.5, -2);

		GeoPolyLine translated = getOnlyNewObject(existingObjects, GeoPolyLine.class);
		GeoVector vector = getOnlyNewObject(existingObjects, GeoVector.class);
		assertEquals(3, translated.getPoints().length);
		assertOnlyNewObjects(
				existingObjects,
				translated,
				vector,
				translated.getPoint(0),
				translated.getPoint(1),
				translated.getPoint(2));
		assertTranslationInputs(translated.getPoint(0), first, vector);
		assertTranslationInputs(translated.getPoint(1), second, vector);
		assertTranslationInputs(translated.getPoint(2), third, vector);
		assertPoint(translated.getPoint(0), 3, -2);
		assertPoint(translated.getPoint(1), 4, -2);
		assertPoint(translated.getPoint(2), 4, -4);
		assertTrue(translated.isDefined());
		assertEquals(3, translated.getLength(), 1E-8);
		assertFalse(vector.isEuclidianVisible());
		assertTrue(vector.isAuxiliaryObject());
		assertEquals(2, vector.getX(), 1E-8);
		assertEquals(-1, vector.getY(), 1E-8);
		assertEquals(MODE_MOVE, getApp().getMode());
		assertPoint(first, 1, -1);
		assertPoint(second, 2, -1);
		assertPoint(third, 2, -3);
		assertEquals(3, source.getLength(), 1E-8);
		vector.setCoords(-1, 2, 0);
		vector.updateCascade();
		assertPoint(translated.getPoint(0), 0, 1);
		assertPoint(translated.getPoint(1), 1, 1);
		assertPoint(translated.getPoint(2), 1, -1);
	}

	private @NonNull GeoVector addVector() {
		return add("u = Vector((4, -1), (6, -2))");
	}

	private @NonNull GeoPolygon addTriangle() {
		add("A = (1, -1)");
		add("B = (2, -1)");
		add("C = (1, -3)");
		return add("p = Polygon(A, B, C)");
	}

	private void assertTranslationInputs(
			@NonNull GeoElementND translated, @NonNull GeoElement source, @NonNull GeoVector vector) {
		assertTransformationInputs(AlgoTranslate.class, translated, source, vector);
	}

	private void assertVectorFromPoints(
			@NonNull GeoVector vector, @NonNull GeoPoint tail, @NonNull GeoPoint head) {
		AlgoVector algorithm = assertInstanceOf(AlgoVector.class, vector.getParentAlgorithm());
		assertTrue(vector.isDefined());
		assertEquals(2, algorithm.getInput().length);
		assertSame(vector, algorithm.getVector());
		assertSame(tail, algorithm.getInput()[0]);
		assertSame(head, algorithm.getInput()[1]);
		assertEquals(head.getInhomX() - tail.getInhomX(), vector.getX(), 1E-8);
		assertEquals(head.getInhomY() - tail.getInhomY(), vector.getY(), 1E-8);
	}
}
