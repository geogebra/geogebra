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

import static org.geogebra.common.euclidian.EuclidianConstants.MODE_ROTATE_BY_ANGLE;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.jre.headless.DialogManagerNoGui;
import org.geogebra.common.kernel.algos.AlgoRotatePoint;
import org.geogebra.common.kernel.geos.GeoConic;
import org.geogebra.common.kernel.geos.GeoCurveCartesian;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.geos.GeoLine;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.geos.GeoPolygon;
import org.geogebra.common.kernel.geos.GeoRay;
import org.geogebra.common.kernel.geos.GeoSegment;
import org.geogebra.common.kernel.geos.GeoVector;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.matrix.Coords;
import org.geogebra.common.main.DialogManager;
import org.geogebra.test.commands.ErrorAccumulator;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedConstruction;
import org.mockito.Mockito;

/** Tests for rotating objects through the angle-input tool. */
class RotateByAngleToolTest extends BaseTransformationToolTest {

	@BeforeEach
	void setMode() {
		setMode(MODE_ROTATE_BY_ANGLE);
		EuclidianView view = getApp().getActiveEuclidianView();
		view.setCoordSystem(250, 250, 50, 50);
	}

	@ParameterizedTest
	@CsvSource({
		"90deg, 4, 0, -1.5707963267948966",
		"-90deg, 2, -4, 1.5707963267948966",
		"180deg, 5, -3, -3.141592653589793",
		"0deg, 1, -1, 0",
		"360deg, 1, -1, -6.283185307179586",
		"450deg, 4, 0, -7.853981633974483",
		"pi/2, 4, 0, -1.5707963267948966"
	})
	void headlessDialogShouldApplyClockwiseSignedDegreeAndRadianAngles(
			@NonNull String input, double expectedX, double expectedY, double expectedAngle) {
		GeoPoint point = add("A = (1, -1)");
		GeoPoint center = addCenter();
		Set<String> existingObjects = getObjectNames();
		prepareInput(input); // The headless dialog chooses clockwise rotation.

		clickRW(1, -1);
		assertEquals(existingObjects, getObjectNames());
		clickRW(3, -2);

		GeoPoint rotated = getOnlyNewObject(existingObjects, GeoPoint.class);
		assertOnlyNewObjects(existingObjects, rotated);
		assertPoint(rotated, expectedX, expectedY);
		assertRotationInputs(rotated, point, center, expectedAngle);
		assertPoint(point, 1, -1);
		assertPoint(center, 3, -2);
		assertFalse(getKernel().getConstruction().isSuppressLabelsActive());
	}

	@ParameterizedTest
	@ValueSource(booleans = {false, true})
	void dialogDirectionChoiceShouldControlRotation(boolean clockwise) {
		GeoPoint point = add("A = (1, -1)");
		GeoPoint center = addCenter();
		Set<String> existingObjects = getObjectNames();

		try (MockedConstruction<DialogManagerNoGui> dialogs = answerDialogs("90deg", clockwise)) {
			clickRW(1, -1);
			clickRW(3, -2);
			assertEquals(1, dialogs.constructed().size());
		}

		GeoPoint rotated = getOnlyNewObject(existingObjects, GeoPoint.class);
		assertOnlyNewObjects(existingObjects, rotated);
		assertPoint(rotated, clockwise ? 4 : 2, clockwise ? 0 : -4);
		assertRotationInputs(rotated, point, center, clockwise ? -Math.PI / 2 : Math.PI / 2);
	}

	@Test
	void secondPointShouldBeCenterWhenPointClickOrderIsReversed() {
		GeoPoint point = add("A = (1, -1)");
		GeoPoint firstSelected = addCenter();
		Set<String> existingObjects = getObjectNames();
		prepareInput("90deg");

		clickRW(3, -2);
		assertOnlyNewObjects(existingObjects);
		clickRW(1, -1);

		GeoPoint rotated = getOnlyNewObject(existingObjects, GeoPoint.class);
		assertOnlyNewObjects(existingObjects, rotated);
		assertPoint(rotated, 0, -3);
		assertRotationInputs(rotated, firstSelected, point, -Math.PI / 2);
		assertPoint(point, 1, -1);
		assertPoint(firstSelected, 3, -2);
	}

	@Test
	void choosingSamePointTwiceShouldWaitForAnObjectInsteadOfRotatingItself() {
		GeoLine source = add("g: x = 1");
		GeoPoint center = addCenter();
		Set<String> existingObjects = getObjectNames();
		prepareInput("90deg");

		clickRW(3, -2);
		resetMouseLocation();
		clickRW(3, -2);
		assertOnlyNewObjects(existingObjects);
		clickRW(1, -4);

		GeoLine rotated = getOnlyNewObject(existingObjects, GeoLine.class);
		assertOnlyNewObjects(existingObjects, rotated);
		assertLineThrough(rotated, 0, 0);
		assertLineThrough(rotated, 2, 0);
		assertRotationInputs(rotated, source, center, -Math.PI / 2);
	}

	@Test
	void arbitraryAngleShouldPreserveRadiusAndHaveExpectedOrientation() {
		GeoPoint point = add("A = (1, -1)");
		GeoPoint center = addCenter();
		Set<String> existingObjects = getObjectNames();
		prepareInput("37deg");
		clickRW(1, -1);
		clickRW(3, -2);

		GeoPoint rotated = getOnlyNewObject(existingObjects, GeoPoint.class);
		assertOnlyNewObjects(existingObjects, rotated);
		assertTrue(rotated.isDefined());
		double dx = point.getInhomX() - center.getInhomX();
		double dy = point.getInhomY() - center.getInhomY();
		double rotatedX = rotated.getInhomX() - center.getInhomX();
		double rotatedY = rotated.getInhomY() - center.getInhomY();
		assertEquals(5, rotatedX * rotatedX + rotatedY * rotatedY, 1E-8);
		assertEquals(Math.cos(Math.toRadians(37)), (dx * rotatedX + dy * rotatedY) / 5, 1E-8);
		assertEquals(-Math.sin(Math.toRadians(37)), (dx * rotatedY - dy * rotatedX) / 5, 1E-8);
		assertRotationInputs(rotated, point, center, -Math.toRadians(37));
		assertPoint(point, 1, -1);
		assertPoint(center, 3, -2);
	}

	@Test
	void openingDialogShouldNotCreateRotationUntilAngleIsConfirmed() {
		GeoPoint point = add("A = (1, -1)");
		GeoPoint center = addCenter();
		Set<String> existingObjects = getObjectNames();

		try (MockedConstruction<DialogManagerNoGui> dialogs =
				Mockito.mockConstruction(DialogManagerNoGui.class)) {
			getApp().initDialogManager(false);
			assertEquals(1, dialogs.constructed().size());
			DialogManagerNoGui dialog = dialogs.constructed().get(0);
			clickRW(1, -1);
			Mockito.verifyNoInteractions(dialog);
			clickRW(3, -2);

			ArgumentCaptor<GeoPointND[]> centers = ArgumentCaptor.forClass(GeoPointND[].class);
			ArgumentCaptor<GeoElement[]> sources = ArgumentCaptor.forClass(GeoElement[].class);
			Mockito.verify(dialog)
					.showNumberInputDialogRotate(
							Mockito.anyString(),
							Mockito.any(GeoPolygon[].class),
							centers.capture(),
							sources.capture(),
							Mockito.same(ec));
			assertArrayEquals(new GeoPointND[] {center}, centers.getValue());
			assertArrayEquals(new GeoElement[] {point, center}, sources.getValue());
			assertOnlyNewObjects(existingObjects);
			assertFalse(getKernel().getConstruction().isSuppressLabelsActive());
		}
	}

	@Test
	void nonNumericAngleShouldReportErrorWithoutCreatingOutputsAndAllowRetry() {
		GeoPoint point = add("A = (1, -1)");
		GeoPoint center = addCenter();
		Set<String> existingObjects = getObjectNames();
		try (MockedConstruction<DialogManagerNoGui> dialogs = answerDialogs("(1, 2)", false)) {
			clickRW(1, -1);
			clickRW(3, -2);
			Mockito.verify(dialogs.constructed().get(0))
					.showError(getApp().getLocalization().getError("NumberExpected"));
			assertOnlyNewObjects(existingObjects);
			assertFalse(getKernel().getConstruction().isSuppressLabelsActive());
		}

		prepareInput("90deg");
		clickRW(1, -1);
		clickRW(3, -2);
		GeoPoint rotated = getOnlyNewObject(existingObjects, GeoPoint.class);
		assertOnlyNewObjects(existingObjects, rotated);
		assertPoint(rotated, 4, 0);
		assertRotationInputs(rotated, point, center, -Math.PI / 2);
	}

	@ParameterizedTest
	@CsvSource({"false, true", "true, false", "false, false"})
	void emptyClicksShouldCreateMissingSourceOrCenter(boolean sourceExists, boolean centerExists) {
		if (sourceExists) {
			add("A = (1, -1)");
		}
		if (centerExists) {
			addCenter();
		}
		Set<String> existingObjects = getObjectNames();
		prepareInput("90deg");
		clickRW(1, -1);
		clickRW(3, -2);

		GeoPoint source = assertInstanceOf(GeoPoint.class, lookup("A"));
		GeoPoint center = assertInstanceOf(GeoPoint.class, lookup(centerExists ? "O" : "B"));
		GeoPoint rotated = assertInstanceOf(GeoPoint.class, lookup("A'"));
		if (sourceExists) {
			assertOnlyNewObjects(existingObjects, center, rotated);
		} else if (centerExists) {
			assertOnlyNewObjects(existingObjects, source, rotated);
		} else {
			assertOnlyNewObjects(existingObjects, source, center, rotated);
		}
		assertPoint(source, 1, -1);
		assertPoint(center, 3, -2);
		assertPoint(rotated, 4, 0);
		assertTrue(source.isIndependent());
		assertTrue(center.isIndependent());
		assertRotationInputs(rotated, source, center, -Math.PI / 2);
	}

	@Test
	void rotationShouldFollowSourceCenterAndNamedAngleAndRecoverFromUndefinedInputs() {
		GeoPoint source = add("A = (1, -1)");
		GeoPoint center = addCenter();
		GeoNumeric angle = add("a = 90deg");
		Set<String> existingObjects = getObjectNames();
		prepareInput("a");
		clickRW(1, -1);
		clickRW(3, -2);
		GeoPoint rotated = getOnlyNewObject(existingObjects, GeoPoint.class);
		assertOnlyNewObjects(existingObjects, rotated);
		assertRotationInputs(rotated, source, center, -Math.PI / 2);
		assertTrue(angle.isParentOf(rotated));
		assertPoint(rotated, 4, 0);

		source.setCoords(2, -1, 1);
		source.updateCascade();
		assertPoint(rotated, 4, -1);
		center.setCoords(2, -2, 1);
		center.updateCascade();
		assertPoint(rotated, 3, -2);
		angle.setValue(Math.PI);
		angle.updateCascade();
		assertPoint(rotated, 2, -3);
		center.setUndefined();
		center.updateCascade();
		assertFalse(rotated.isDefined());
		center.setCoords(2, -2, 1);
		center.updateCascade();
		assertPoint(rotated, 2, -3);
		source.setUndefined();
		source.updateCascade();
		assertFalse(rotated.isDefined());
		source.setCoords(1, -1, 1);
		source.updateCascade();
		assertPoint(rotated, 3, -3);
		angle.setUndefined();
		angle.updateCascade();
		assertFalse(rotated.isDefined());
		angle.setValue(Math.PI / 2);
		angle.updateCascade();
		assertPoint(rotated, 3, -1);
		assertPoint(source, 1, -1);
		assertPoint(center, 2, -2);
		assertOnlyNewObjects(existingObjects, rotated);
	}

	@ParameterizedTest
	@ValueSource(booleans = {false, true})
	void lineAndCenterShouldWorkInEitherClickOrder(boolean centerFirst) {
		GeoLine source = add("g: x = 1");
		GeoPoint center = addCenter();
		Set<String> existingObjects = getObjectNames();
		prepareInput("90deg");
		if (centerFirst) {
			clickRW(3, -2);
			clickRW(1, -4);
		} else {
			clickRW(1, -4);
			clickRW(3, -2);
		}

		GeoLine rotated = getOnlyNewObject(existingObjects, GeoLine.class);
		assertOnlyNewObjects(existingObjects, rotated);
		assertLineThrough(rotated, 0, 0);
		assertLineThrough(rotated, 2, 0);
		assertRotationInputs(rotated, source, center, -Math.PI / 2);
		assertLineThrough(source, 1, -1);
		assertLineThrough(source, 1, -4);
	}

	@Test
	void circleShouldRotateCenterAndPreserveRadius() {
		GeoConic source = add("c: (x - 1)^2 + (y + 1)^2 = 0.25");
		GeoPoint center = addCenter();
		Set<String> existingObjects = getObjectNames();
		prepareInput("90deg");
		clickRW(1, -0.5);
		clickRW(3, -2);

		GeoConic rotated = getOnlyNewObject(existingObjects, GeoConic.class);
		assertOnlyNewObjects(existingObjects, rotated);
		assertTrue(rotated.isDefined());
		assertTrue(rotated.isCircle());
		assertEquals(4, rotated.getMidpoint().getX(), 1E-8);
		assertEquals(0, rotated.getMidpoint().getY(), 1E-8);
		assertEquals(0.5, rotated.getCircleRadius(), 1E-8);
		assertRotationInputs(rotated, source, center, -Math.PI / 2);
		assertEquals(1, source.getMidpoint().getX(), 1E-8);
		assertEquals(-1, source.getMidpoint().getY(), 1E-8);
		assertEquals(0.5, source.getCircleRadius(), 1E-8);
	}

	@Test
	void segmentShouldRotateBothEndpointsAndPreserveLength() {
		add("A = (1, -1)");
		add("B = (2, -1)");
		GeoSegment source = add("s = Segment(A, B)");
		GeoPoint center = addCenter();
		Set<String> existingObjects = getObjectNames();
		prepareInput("90deg");
		clickRW(1.5, -1);
		clickRW(3, -2);

		GeoSegment rotated = getOnlyNewObject(existingObjects, GeoSegment.class);
		assertOnlyNewObjects(existingObjects, rotated, rotated.getStartPoint(), rotated.getEndPoint());
		assertPoint(rotated.getStartPoint(), 4, 0);
		assertPoint(rotated.getEndPoint(), 4, -1);
		assertEquals(1, rotated.getLength(), 1E-8);
		GeoNumeric angle =
				assertRotationInputs(rotated.getStartPoint(), lookup("A"), center, -Math.PI / 2);
		assertTransformationInputs(
				AlgoRotatePoint.class, rotated.getEndPoint(), lookup("B"), angle, center);
		assertPoint(source.getStartPoint(), 1, -1);
		assertPoint(source.getEndPoint(), 2, -1);
	}

	@Test
	void rayShouldRotateDefiningPointsAndKeepInfiniteDirection() {
		GeoPoint first = add("A = (1, -1)");
		GeoPoint second = add("B = (2, -1)");
		add("ray = Ray(A, B)");
		GeoPoint center = addCenter();
		Set<String> existingObjects = getObjectNames();
		prepareInput("90deg");
		clickRW(1.5, -1);
		clickRW(3, -2);

		GeoRay rotated = getOnlyNewObject(existingObjects, GeoRay.class);
		GeoPoint end = assertInstanceOf(GeoPoint.class, lookup("B'"));
		assertOnlyNewObjects(existingObjects, rotated, rotated.getStartPoint(), end);
		assertPoint(rotated.getStartPoint(), 4, 0);
		assertPoint(end, 4, -1);
		GeoNumeric angle =
				assertRotationInputs(rotated.getStartPoint(), lookup("A"), center, -Math.PI / 2);
		assertTransformationInputs(AlgoRotatePoint.class, end, lookup("B"), angle, center);
		assertSame(end, rotated.getParentAlgorithm().getInput()[1]);
		assertTrue(rotated.isOnPath(new Coords(4, -3, 0, 1), 1E-8));
		assertFalse(rotated.isOnPath(new Coords(4, 1, 0, 1), 1E-8));
		assertPoint(first, 1, -1);
		assertPoint(second, 2, -1);
	}

	@Test
	void vectorShouldRotateDirectionWithoutUsingCenterAsTranslation() {
		GeoVector source = add("u = Vector((1, -1), (3, -4))");
		GeoPoint center = addCenter();
		Set<String> existingObjects = getObjectNames();
		prepareInput("90deg");
		clickRW(2, -2.5);
		clickRW(3, -2);

		GeoVector rotated = getOnlyNewObject(existingObjects, GeoVector.class);
		assertOnlyNewObjects(existingObjects, rotated);
		assertTrue(rotated.isDefined());
		assertEquals(-3, rotated.getX(), 1E-8);
		assertEquals(-2, rotated.getY(), 1E-8);
		assertRotationInputs(rotated, source, center, -Math.PI / 2);
		assertEquals(2, source.getX(), 1E-8);
		assertEquals(-3, source.getY(), 1E-8);
		center.setCoords(4, -3, 1);
		center.updateCascade();
		assertEquals(-3, rotated.getX(), 1E-8);
		assertEquals(-2, rotated.getY(), 1E-8);
	}

	@Test
	void functionShouldBecomeRotatedCurve() {
		GeoFunction source = add("f(x) = x^2 - 4");
		GeoPoint center = addCenter();
		Set<String> existingObjects = getObjectNames();
		prepareInput("90deg");
		clickRW(0, -4);
		clickRW(3, -2);

		GeoCurveCartesian rotated = getOnlyNewObject(existingObjects, GeoCurveCartesian.class);
		assertOnlyNewObjects(existingObjects, rotated);
		assertRotationInputs(rotated, source, center, -Math.PI / 2);
		assertCurvePoint(rotated, 0, 1, 1);
		assertCurvePoint(rotated, 1, 2, 0);
		assertCurvePoint(rotated, 2, 5, -1);
		assertEquals(-3, source.value(1), 1E-8);
	}

	@ParameterizedTest
	@CsvSource({"false, false", "true, false", "false, true"})
	void polygonShouldRotateWithCenterFirstOrAfterAnotherObject(
			boolean centerFirst, boolean selectLineFirst) {
		GeoLine unrelated = add("g: y = -4");
		GeoPolygon source = addTriangle();
		GeoPoint center = addCenter();
		Set<String> existingObjects = getObjectNames();
		prepareInput("90deg");
		if (selectLineFirst) {
			clickRW(0, -4);
		}
		if (centerFirst) {
			clickRW(3, -2);
		}
		clickRW(1.25, -1.5);
		if (!centerFirst) {
			clickRW(3, -2);
		}

		GeoPolygon rotated = getOnlyNewObject(existingObjects, GeoPolygon.class);
		assertOnlyNewPolygon(existingObjects, rotated);
		GeoNumeric angle =
				assertRotationInputs(rotated.getPoint(0), source.getPoint(0), center, -Math.PI / 2);
		assertPolygonStructure(AlgoRotatePoint.class, rotated, source, angle, center);
		assertPoint(rotated.getPoint(0), 4, 0);
		assertPoint(rotated.getPoint(1), 4, -1);
		assertPoint(rotated.getPoint(2), 2, 0);
		assertEquals(1, rotated.getArea(), 1E-8);
		assertPoint(source.getPoint(0), 1, -1);
		assertPoint(source.getPoint(1), 2, -1);
		assertPoint(source.getPoint(2), 1, -3);
		assertLineThrough(unrelated, 0, -4);
		source.getPoint(1).setCoords(3, -1, 1);
		source.getPoint(1).updateCascade();
		assertPoint(rotated.getPoint(1), 4, -2);
		assertPoint(rotated.getPoint(0), 4, 0);
		assertPoint(rotated.getPoint(2), 2, 0);
		assertEquals(2, source.getArea(), 1E-8);
		assertEquals(2, rotated.getArea(), 1E-8);
		assertEquals(2, rotated.getSegments()[0].getLength(), 1E-8);
	}

	@Test
	void listShouldRotateEachElement() {
		GeoList source = add("L = {(1, -1), (2, -1)}");
		GeoPoint first = assertInstanceOf(GeoPoint.class, source.get(0));
		GeoPoint second = assertInstanceOf(GeoPoint.class, source.get(1));
		source.setEuclidianVisible(true);
		source.updateRepaint();
		GeoPoint center = addCenter();
		Set<String> existingObjects = getObjectNames();
		prepareInput("90deg");
		clickRW(1, -1);
		clickRW(3, -2);

		GeoList rotated = getOnlyNewObject(existingObjects, GeoList.class);
		assertOnlyNewObjects(existingObjects, rotated);
		assertRotationInputs(rotated, source, center, -Math.PI / 2);
		assertEquals(2, rotated.size());
		assertPoint(assertInstanceOf(GeoPoint.class, rotated.get(0)), 4, 0);
		assertPoint(assertInstanceOf(GeoPoint.class, rotated.get(1)), 4, -1);
		assertNotSame(first, rotated.get(0));
		assertNotSame(second, rotated.get(1));
		assertPoint(first, 1, -1);
		assertPoint(second, 2, -1);
		first.setCoords(2, -2, 1);
		source.updateCascade();
		assertPoint(assertInstanceOf(GeoPoint.class, rotated.get(0)), 3, -1);
		assertPoint(assertInstanceOf(GeoPoint.class, rotated.get(1)), 4, -1);
	}

	@ParameterizedTest
	@ValueSource(booleans = {false, true})
	void rectangleShouldRotateAllPointsAndExcludeCenter(boolean includeCenter) {
		GeoPoint first = add("A = (1, -1)");
		GeoPoint second = add("B = (2, -1)");
		GeoPoint center = addCenter();
		Set<String> existingObjects = getObjectNames();
		prepareInput("90deg");
		dragRW(0.5, -0.5, includeCenter ? 3.5 : 2.5, -2.5);
		clickRW(3, -2);

		GeoPoint firstRotated = assertInstanceOf(GeoPoint.class, lookup("A'"));
		GeoPoint secondRotated = assertInstanceOf(GeoPoint.class, lookup("B'"));
		assertOnlyNewObjects(existingObjects, firstRotated, secondRotated);
		assertPoint(firstRotated, 4, 0);
		assertPoint(secondRotated, 4, -1);
		GeoNumeric angle = assertRotationInputs(firstRotated, first, center, -Math.PI / 2);
		assertTransformationInputs(AlgoRotatePoint.class, secondRotated, second, angle, center);
		assertPoint(center, 3, -2);
	}

	@Test
	void rectangleShouldRotatePolygonAndIndependentPointWithoutDuplicatingVertices() {
		GeoPoint independent = add("P = (2, -3)");
		GeoPolygon source = addTriangle();
		GeoPoint center = addCenter();
		Set<String> existingObjects = getObjectNames();
		prepareInput("90deg");
		dragRW(0.5, -0.5, 2.5, -3.5);
		clickRW(3, -2);

		GeoPolygon rotated = getOnlyNewObject(existingObjects, GeoPolygon.class);
		GeoPoint rotatedIndependent = assertInstanceOf(GeoPoint.class, lookup("P'"));
		assertOnlyNewPolygon(existingObjects, rotated, rotatedIndependent);
		GeoNumeric angle = assertRotationInputs(rotatedIndependent, independent, center, -Math.PI / 2);
		assertPolygonStructure(AlgoRotatePoint.class, rotated, source, angle, center);
		assertPoint(rotatedIndependent, 2, -1);
		assertPoint(rotated.getPoint(0), 4, 0);
		assertPoint(rotated.getPoint(1), 4, -1);
		assertPoint(rotated.getPoint(2), 2, 0);
	}

	@Test
	void hoveringCenterShouldHighlightWithoutOpeningDialogOrReplacingCenter() {
		GeoPoint source = add("A = (1, -1)");
		GeoPoint center = addCenter();
		GeoPoint otherCenter = add("P = (5, -3)");
		Set<String> existingObjects = getObjectNames();
		prepareInput("90deg");
		moveMouseRW(1, -1);
		assertTrue(ec.getHighlightedgeos().contains(source));
		clickRW(1, -1);
		moveMouseRW(5, -3);
		assertTrue(ec.getHighlightedgeos().contains(otherCenter));
		assertOnlyNewObjects(existingObjects);
		clickRW(3, -2);

		GeoPoint rotated = getOnlyNewObject(existingObjects, GeoPoint.class);
		assertOnlyNewObjects(existingObjects, rotated);
		assertPoint(rotated, 4, 0);
		assertRotationInputs(rotated, source, center, -Math.PI / 2);
	}

	@Test
	void toolShouldUseNewCenterAndAngleAfterCompletion() {
		GeoPoint first = add("A = (1, -1)");
		GeoPoint second = add("B = (2, -1)");
		GeoPoint center = addCenter();
		GeoPoint nextCenter = add("P = (5, -3)");
		Set<String> beforeFirstRotation = getObjectNames();
		prepareInput("90deg", "180deg");
		clickRW(1, -1);
		clickRW(3, -2);
		GeoPoint firstRotated = getOnlyNewObject(beforeFirstRotation, GeoPoint.class);
		assertOnlyNewObjects(beforeFirstRotation, firstRotated);
		assertRotationInputs(firstRotated, first, center, -Math.PI / 2);
		Set<String> existingObjects = getObjectNames();
		clickRW(2, -1);
		assertOnlyNewObjects(existingObjects);
		clickRW(5, -3);

		GeoPoint secondRotated = getOnlyNewObject(existingObjects, GeoPoint.class);
		assertOnlyNewObjects(existingObjects, secondRotated);
		assertPoint(secondRotated, 8, -5);
		assertRotationInputs(secondRotated, second, nextCenter, -Math.PI);
		assertPoint(firstRotated, 4, 0);
	}

	@ParameterizedTest
	@CsvSource({
		"false, 90°, 90°, true", "true, 90°, 90°, true",
		"false, pi/2, 45°, true", "true, pi/2, 45°, true",
		"false, 90°, 90°, false", "true, 90°, 90°, false"
	})
	void rotationCallbackShouldReturnRememberedAngleAfterOutputsAreCreated(
			boolean polygon,
			@NonNull String input,
			@NonNull String rememberedAngle,
			boolean withCallback) {
		GeoElement source = polygon ? addTriangle() : add("A = (1, -1)");
		GeoPoint center = addCenter();
		Set<String> existingObjects = getObjectNames();
		ErrorAccumulator errors = new ErrorAccumulator();
		List<String> results = new ArrayList<>();
		List<GeoElement> outputsAtCompletion = new ArrayList<>();

		DialogManager.rotateObject(
				getApp(),
				input,
				true,
				polygon ? new GeoPolygon[] {(GeoPolygon) source} : new GeoPolygon[0],
				new DialogManager.CreateGeoForRotatePoint(center),
				new GeoElement[] {source, center},
				ec,
				errors,
				withCallback
						? result -> {
							results.add(result);
							outputsAtCompletion.addAll(ec.getJustCreatedGeos());
						}
						: null);

		assertEquals(withCallback ? List.of(rememberedAngle) : List.of(), results);
		assertEquals("", errors.getErrors());
		assertFalse(getKernel().getConstruction().isSuppressLabelsActive());
		if (polygon) {
			GeoPolygon rotated = getOnlyNewObject(existingObjects, GeoPolygon.class);
			assertOnlyNewPolygon(existingObjects, rotated);
			GeoNumeric angle = assertRotationInputs(
					rotated.getPoint(0), ((GeoPolygon) source).getPoint(0), center, -Math.PI / 2);
			assertPolygonStructure(AlgoRotatePoint.class, rotated, (GeoPolygon) source, angle, center);
			assertPoint(rotated.getPoint(0), 4, 0);
			assertPoint(rotated.getPoint(1), 4, -1);
			assertPoint(rotated.getPoint(2), 2, 0);
			assertEquals(1, rotated.getArea(), 1E-8);
			assertTrue(ec.getJustCreatedGeos().contains(rotated));
			assertPoint(((GeoPolygon) source).getPoint(0), 1, -1);
			assertPoint(((GeoPolygon) source).getPoint(1), 2, -1);
			assertPoint(((GeoPolygon) source).getPoint(2), 1, -3);
		} else {
			GeoPoint rotated = getOnlyNewObject(existingObjects, GeoPoint.class);
			assertOnlyNewObjects(existingObjects, rotated);
			assertPoint(rotated, 4, 0);
			assertRotationInputs(rotated, source, center, -Math.PI / 2);
			assertEquals(List.of(rotated), ec.getJustCreatedGeos());
			assertPoint((GeoPoint) source, 1, -1);
		}
		assertPoint(center, 3, -2);
		assertEquals(withCallback ? ec.getJustCreatedGeos() : List.of(), outputsAtCompletion);
	}

	@Test
	void rotationShouldRestorePreviouslySuppressedLabelsBeforeCompleting() {
		GeoPoint source = add("A = (1, -1)");
		GeoPoint center = addCenter();
		Set<String> existingObjects = getObjectNames();
		List<Boolean> suppressionAtCompletion = new ArrayList<>();
		ErrorAccumulator errors = new ErrorAccumulator();
		getKernel().getConstruction().setSuppressLabelCreation(true);
		try {
			DialogManager.rotateObject(
					getApp(),
					"90°",
					true,
					new GeoPolygon[0],
					new DialogManager.CreateGeoForRotatePoint(center),
					new GeoElement[] {source, center},
					ec,
					errors,
					result ->
							suppressionAtCompletion.add(getKernel().getConstruction().isSuppressLabelsActive()));

			assertEquals(List.of(true), suppressionAtCompletion);
			assertTrue(getKernel().getConstruction().isSuppressLabelsActive());
			assertEquals(1, ec.getJustCreatedGeos().size());
			GeoPoint rotated =
					assertInstanceOf(GeoPoint.class, ec.getJustCreatedGeos().get(0));
			assertFalse(rotated.isLabelSet());
			assertPoint(rotated, 4, 0);
			assertRotationInputs(rotated, source, center, -Math.PI / 2);
			assertOnlyNewObjects(existingObjects);
			assertEquals("", errors.getErrors());
		} finally {
			getKernel().getConstruction().setSuppressLabelCreation(false);
		}
	}

	@ParameterizedTest
	@ValueSource(booleans = {false, true})
	void malformedAngleShouldRestoreLabelStateWithoutCallingCompletion(boolean suppressLabels) {
		GeoPoint source = add("A = (1, -1)");
		GeoPoint center = addCenter();
		Set<String> existingObjects = getObjectNames();
		ErrorAccumulator errors = new ErrorAccumulator();
		List<String> results = new ArrayList<>();
		getKernel().getConstruction().setSuppressLabelCreation(suppressLabels);
		try {
			DialogManager.rotateObject(
					getApp(),
					")",
					false,
					new GeoPolygon[0],
					new DialogManager.CreateGeoForRotatePoint(center),
					new GeoElement[] {source, center},
					ec,
					errors,
					results::add);

			assertTrue(results.isEmpty());
			assertFalse(errors.getErrors().isEmpty());
			assertEquals(suppressLabels, getKernel().getConstruction().isSuppressLabelsActive());
			assertOnlyNewObjects(existingObjects);
			assertTrue(ec.getJustCreatedGeos().isEmpty());
		} finally {
			getKernel().getConstruction().setSuppressLabelCreation(false);
		}
	}

	@Test
	void nonNumericAngleShouldCompleteOnceWithNullAndNoOutputs() {
		GeoPoint source = add("A = (1, -1)");
		GeoPoint center = addCenter();
		Set<String> existingObjects = getObjectNames();
		ErrorAccumulator errors = new ErrorAccumulator();
		List<String> results = new ArrayList<>();

		DialogManager.rotateObject(
				getApp(),
				"(1, 2)",
				false,
				new GeoPolygon[0],
				new DialogManager.CreateGeoForRotatePoint(center),
				new GeoElement[] {source, center},
				ec,
				errors,
				results::add);

		assertEquals(Collections.singletonList(null), results);
		assertEquals(getApp().getLocalization().getError("NumberExpected"), errors.getErrors());
		assertFalse(getKernel().getConstruction().isSuppressLabelsActive());
		assertOnlyNewObjects(existingObjects);
		assertTrue(ec.getJustCreatedGeos().isEmpty());
	}

	@ParameterizedTest
	@ValueSource(booleans = {false, true})
	void commandWithoutNumericResultShouldNotCreateRotation(boolean withCallback) {
		GeoPoint source = add("A = (1, -1)");
		GeoPoint center = addCenter();
		GeoNumeric value = add("k = 2");
		Set<String> existingObjects = getObjectNames();
		ErrorAccumulator errors = new ErrorAccumulator();
		List<String> results = new ArrayList<>();

		DialogManager.rotateObject(
				getApp(),
				"SetValue(k, 3)",
				false,
				new GeoPolygon[0],
				new DialogManager.CreateGeoForRotatePoint(center),
				new GeoElement[] {source, center},
				ec,
				errors,
				withCallback ? results::add : null);

		assertEquals(withCallback ? Collections.singletonList(null) : List.of(), results);
		assertEquals("", errors.getErrors());
		assertEquals(3, value.getDouble(), 1E-8);
		assertFalse(getKernel().getConstruction().isSuppressLabelsActive());
		assertOnlyNewObjects(existingObjects);
		assertTrue(ec.getJustCreatedGeos().isEmpty());
	}

	@ParameterizedTest
	@ValueSource(ints = {0, 1, 2})
	void rotationBackendShouldIgnoreEmptyPivotOnlyAndUnsupportedSelections(int selection) {
		GeoPoint center = addCenter();
		GeoElement text = add("message = \"ignored\"");
		GeoElement[] sources =
				selection == 0 ? new GeoElement[0] : new GeoElement[] {selection == 1 ? center : text};
		Set<String> existingObjects = getObjectNames();
		List<String> results = new ArrayList<>();
		ErrorAccumulator errors = new ErrorAccumulator();

		DialogManager.rotateObject(
				getApp(),
				"pi/2",
				true,
				new GeoPolygon[0],
				new DialogManager.CreateGeoForRotatePoint(center),
				sources,
				ec,
				errors,
				results::add);

		assertEquals(List.of("45°"), results);
		assertEquals("", errors.getErrors());
		assertFalse(getKernel().getConstruction().isSuppressLabelsActive());
		assertOnlyNewObjects(existingObjects);
		assertTrue(ec.getJustCreatedGeos().isEmpty());
	}

	@ParameterizedTest
	@ValueSource(booleans = {false, true})
	void nullPolygonRotationResultShouldNotBeRecordedAsCreated(boolean withCallback) {
		GeoPolygon source = addTriangle();
		GeoPoint center = addCenter();
		DialogManager.CreateGeoForRotate creator = Mockito.mock(DialogManager.CreateGeoForRotate.class);
		Mockito.when(creator.createGeos(Mockito.same(ec), Mockito.same(source), Mockito.any()))
				.thenReturn(null);
		Set<String> existingObjects = getObjectNames();
		List<String> results = new ArrayList<>();
		ErrorAccumulator errors = new ErrorAccumulator();

		DialogManager.rotateObject(
				getApp(),
				"90°",
				true,
				new GeoPolygon[] {source},
				creator,
				new GeoElement[] {source, center},
				ec,
				errors,
				withCallback ? results::add : null);

		Mockito.verify(creator).createGeos(Mockito.same(ec), Mockito.same(source), Mockito.any());
		assertEquals(withCallback ? List.of("90°") : List.of(), results);
		assertEquals("", errors.getErrors());
		assertFalse(getKernel().getConstruction().isSuppressLabelsActive());
		assertOnlyNewObjects(existingObjects);
		assertTrue(ec.getJustCreatedGeos().isEmpty());
	}

	private @NonNull GeoPoint addCenter() {
		return add("O = (3, -2)");
	}

	private @NonNull GeoPolygon addTriangle() {
		add("A = (1, -1)");
		add("B = (2, -1)");
		add("C = (1, -3)");
		return add("p = Polygon(A, B, C)");
	}

	private @NonNull GeoNumeric assertRotationInputs(
			@NonNull GeoElementND rotated,
			@NonNull GeoElement source,
			@NonNull GeoPoint center,
			double expectedAngle) {
		AlgoRotatePoint algorithm =
				assertInstanceOf(AlgoRotatePoint.class, rotated.getParentAlgorithm());
		GeoNumeric angle = assertInstanceOf(GeoNumeric.class, algorithm.getInput()[1]);
		assertTransformationInputs(AlgoRotatePoint.class, rotated, source, angle, center);
		assertEquals(expectedAngle, angle.getDouble(), 1E-8);
		return angle;
	}

	private void assertCurvePoint(
			@NonNull GeoCurveCartesian curve, double parameter, double expectedX, double expectedY) {
		double[] coordinates = new double[2];
		curve.evaluateCurve(parameter, coordinates);
		assertTrue(curve.isDefined());
		assertEquals(expectedX, coordinates[0], 1E-8);
		assertEquals(expectedY, coordinates[1], 1E-8);
	}

	private @NonNull MockedConstruction<DialogManagerNoGui> answerDialogs(
			@NonNull String input, boolean clockwise) {
		MockedConstruction<DialogManagerNoGui> dialogs =
				Mockito.mockConstruction(DialogManagerNoGui.class, (dialog, context) -> {
					Mockito.doAnswer(invocation -> {
								GeoPolygon[] polygons = invocation.getArgument(1);
								GeoPointND[] centers = invocation.getArgument(2);
								GeoElement[] sources = invocation.getArgument(3);
								DialogManager.rotateObject(
										getApp(),
										input,
										clockwise,
										polygons,
										new DialogManager.CreateGeoForRotatePoint(centers[0]),
										sources,
										ec,
										dialog,
										ignored -> {});
								return null;
							})
							.when(dialog)
							.showNumberInputDialogRotate(
									Mockito.anyString(),
									Mockito.any(GeoPolygon[].class),
									Mockito.any(GeoPointND[].class),
									Mockito.any(GeoElement[].class),
									Mockito.same(ec));
				});
		getApp().initDialogManager(false);
		return dialogs;
	}
}
