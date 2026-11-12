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

import static org.geogebra.common.euclidian.EuclidianConstants.MODE_DILATE_FROM_POINT;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.gui.dialog.handler.NumberInputHandler;
import org.geogebra.common.jre.headless.DialogManagerNoGui;
import org.geogebra.common.kernel.algos.AlgoDilate;
import org.geogebra.common.kernel.geos.GeoConic;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.geos.GeoLine;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoNumberValue;
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
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

/** Tests for dilating objects through the factor-input tool. */
class DilateFromPointToolTest extends BaseTransformationToolTest {
	private MockedConstruction<DialogManagerNoGui> dialogs;
	private DialogManagerNoGui dialog;
	private String factorInput = "2";
	private boolean confirmInput = true;
	private boolean dilationSucceeded;

	@BeforeEach
	void setUpDilateTool() {
		setMode(MODE_DILATE_FROM_POINT);
		EuclidianView view = getApp().getActiveEuclidianView();
		view.setCoordSystem(250, 250, 50, 50);
		// The headless dilation dialog is a stub. Replace only its input boundary, using
		// the same number handler and dilation action as the web dialog.
		dialogs = Mockito.mockConstruction(DialogManagerNoGui.class, (mock, context) -> {
			Mockito.doAnswer(invocation -> {
						dilationSucceeded = false;
						if (confirmInput) {
							GeoPointND[] centers = invocation.getArgument(2);
							GeoElement[] sources = invocation.getArgument(3);
							NumberInputHandler handler =
									new NumberInputHandler(getKernel().getAlgebraProcessor());
							List<Boolean> inputResults = new ArrayList<>();
							handler.processInput(factorInput, mock, inputResults::add);
							assertEquals(1, inputResults.size(), "Headless number input should complete once");
							// Keep the backend call outside NumberInputHandler's catch(Throwable),
							// so an exception after creating outputs cannot be swallowed by the fixture.
							dilationSucceeded = inputResults.get(0)
									&& DialogManager.doDilate(getKernel(), handler.getNum(), centers, sources, ec);
						}
						return null;
					})
					.when(mock)
					.showNumberInputDialogDilate(
							Mockito.anyString(),
							Mockito.any(GeoPolygon[].class),
							Mockito.any(GeoPointND[].class),
							Mockito.any(GeoElement[].class),
							Mockito.same(ec));
		});
		getApp().initDialogManager(false);
		assertEquals(1, dialogs.constructed().size());
		dialog = dialogs.constructed().get(0);
	}

	@AfterEach
	void closeDialogs() {
		if (dialogs != null) {
			dialogs.close();
		}
	}

	@ParameterizedTest
	@CsvSource({
		"2, -1, 0",
		"0.5, 2, -1.5",
		"1/2, 2, -1.5",
	})
	void pointShouldRespectEnlargementReductionNegativeZeroAndIdentityFactors(
			@NonNull String input, double expectedX, double expectedY) {
		GeoPoint source = add("A = (1, -1)");
		GeoPoint center = addCenter();
		factorInput = input;
		Set<String> existingObjects = getObjectNames();

		clickRW(1, -1);
		Mockito.verifyNoInteractions(dialog);
		clickRW(3, -2);

		GeoPoint dilated = getOnlyNewObject(existingObjects, GeoPoint.class);
		assertOnlyNewObjects(existingObjects, dilated);
		assertPoint(dilated, expectedX, expectedY);
		assertDilationInputs(dilated, source, center);
		assertTrue(dilationSucceeded);
		assertPoint(source, 1, -1);
		assertPoint(center, 3, -2);
		assertFalse(getKernel().getConstruction().isSuppressLabelsActive());
	}

	@Test
	void negativeDilationShouldPreserveCollinearityAndReverseTheRay() {
		GeoPoint source = add("A = (1, -1)");
		GeoPoint center = addCenter();
		factorInput = "-1.5";
		Set<String> existingObjects = getObjectNames();
		clickRW(1, -1);
		clickRW(3, -2);

		GeoPoint dilated = getOnlyNewObject(existingObjects, GeoPoint.class);
		assertOnlyNewObjects(existingObjects, dilated);
		assertTrue(dilated.isDefined());
		double dx = dilated.getInhomX() - center.getInhomX();
		double dy = dilated.getInhomY() - center.getInhomY();
		assertEquals(0, -2 * dy - dx, 1E-8);
		assertEquals(-7.5, -2 * dx + dy, 1E-8);
		assertEquals(11.25, dx * dx + dy * dy, 1E-8);
		assertEquals(-1.5, assertDilationInputs(dilated, source, center).getDouble(), 1E-8);
		assertPoint(source, 1, -1);
		assertPoint(center, 3, -2);
	}

	@Test
	void factorDialogShouldWaitForCenterAndConfirmation() {
		GeoPoint source = add("A = (1, -1)");
		GeoPoint center = addCenter();
		Set<String> existingObjects = getObjectNames();
		confirmInput = false;
		clickRW(1, -1);
		assertOnlyNewObjects(existingObjects);
		Mockito.verifyNoInteractions(dialog);
		clickRW(3, -2);

		ArgumentCaptor<GeoPointND[]> centers = ArgumentCaptor.forClass(GeoPointND[].class);
		ArgumentCaptor<GeoElement[]> sources = ArgumentCaptor.forClass(GeoElement[].class);
		Mockito.verify(dialog)
				.showNumberInputDialogDilate(
						Mockito.anyString(),
						Mockito.any(GeoPolygon[].class),
						centers.capture(),
						sources.capture(),
						Mockito.same(ec));
		assertArrayEquals(new GeoPointND[] {center}, centers.getValue());
		assertArrayEquals(new GeoElement[] {source, center}, sources.getValue());
		assertOnlyNewObjects(existingObjects);
		assertFalse(dilationSucceeded);
		assertFalse(getKernel().getConstruction().isSuppressLabelsActive());
	}

	@Test
	void nonNumericFactorShouldReportErrorWithoutOutputsAndAllowRetry() {
		GeoPoint source = add("A = (1, -1)");
		GeoPoint center = addCenter();
		factorInput = "(1, 2)";
		Set<String> existingObjects = getObjectNames();
		clickRW(1, -1);
		clickRW(3, -2);

		Mockito.verify(dialog).showError(getApp().getLocalization().getError("NumberExpected"));
		assertFalse(dilationSucceeded);
		assertOnlyNewObjects(existingObjects);
		assertFalse(getKernel().getConstruction().isSuppressLabelsActive());
		factorInput = "2";
		clickRW(1, -1);
		clickRW(3, -2);

		GeoPoint dilated = getOnlyNewObject(existingObjects, GeoPoint.class);
		assertOnlyNewObjects(existingObjects, dilated);
		assertPoint(dilated, -1, 0);
		assertDilationInputs(dilated, source, center);
		assertTrue(dilationSucceeded);
	}

	@Test
	void backendFailureShouldPropagateInsteadOfBeingSwallowedByNumberInputHandler() {
		add("A = (1, -1)");
		addCenter();
		Set<String> existingObjects = getObjectNames();
		IllegalStateException failure = new IllegalStateException("Dilation backend failed");
		try (MockedStatic<DialogManager> backend = Mockito.mockStatic(DialogManager.class)) {
			backend
					.when(() -> DialogManager.doDilate(
							Mockito.same(getKernel()),
							Mockito.any(GeoNumberValue.class),
							Mockito.any(GeoPointND[].class),
							Mockito.any(GeoElement[].class),
							Mockito.same(ec)))
					.thenThrow(failure);
			clickRW(1, -1);
			assertSame(failure, assertThrows(IllegalStateException.class, () -> clickRW(3, -2)));
		}
		assertOnlyNewObjects(existingObjects);
		assertFalse(getKernel().getConstruction().isSuppressLabelsActive());
	}

	@ParameterizedTest
	@CsvSource({"false, true", "true, false", "false, false"})
	void emptyClicksShouldCreateMissingSourceAndCenter(boolean sourceExists, boolean centerExists) {
		if (sourceExists) {
			add("A = (1, -1)");
		}
		if (centerExists) {
			addCenter();
		}
		Set<String> existingObjects = getObjectNames();
		clickRW(1, -1);
		clickRW(3, -2);

		GeoPoint source = assertInstanceOf(GeoPoint.class, lookup("A"));
		GeoPoint center = assertInstanceOf(GeoPoint.class, lookup(centerExists ? "O" : "B"));
		GeoPoint dilated = assertInstanceOf(GeoPoint.class, lookup("A'"));
		if (sourceExists) {
			assertOnlyNewObjects(existingObjects, center, dilated);
		} else if (centerExists) {
			assertOnlyNewObjects(existingObjects, source, dilated);
		} else {
			assertOnlyNewObjects(existingObjects, source, center, dilated);
		}
		assertPoint(source, 1, -1);
		assertPoint(center, 3, -2);
		assertPoint(dilated, -1, 0);
		assertTrue(source.isIndependent());
		assertTrue(center.isIndependent());
		assertDilationInputs(dilated, source, center);
	}

	@Test
	void dilationShouldFollowSourceCenterAndNamedFactorAndRecoverFromUndefinedInputs() {
		GeoPoint source = add("A = (1, -1)");
		GeoPoint center = addCenter();
		GeoNumeric factor = add("r = 2");
		factorInput = "r";
		Set<String> existingObjects = getObjectNames();
		clickRW(1, -1);
		clickRW(3, -2);
		GeoPoint dilated = getOnlyNewObject(existingObjects, GeoPoint.class);
		assertOnlyNewObjects(existingObjects, dilated);
		assertSame(factor, assertDilationInputs(dilated, source, center));
		assertPoint(dilated, -1, 0);

		source.setCoords(2, -1, 1);
		source.updateCascade();
		assertPoint(dilated, 1, 0);
		center.setCoords(2, -2, 1);
		center.updateCascade();
		assertPoint(dilated, 2, 0);
		factor.setValue(-1);
		factor.updateCascade();
		assertPoint(dilated, 2, -3);
		center.setUndefined();
		center.updateCascade();
		assertFalse(dilated.isDefined());
		center.setCoords(2, -2, 1);
		center.updateCascade();
		assertPoint(dilated, 2, -3);
		source.setUndefined();
		source.updateCascade();
		assertFalse(dilated.isDefined());
		source.setCoords(1, -1, 1);
		source.updateCascade();
		assertPoint(dilated, 3, -3);
		factor.setUndefined();
		factor.updateCascade();
		assertFalse(dilated.isDefined());
		factor.setValue(0.5);
		factor.updateCascade();
		assertPoint(dilated, 1.5, -1.5);
		assertOnlyNewObjects(existingObjects, dilated);
	}

	@Test
	void pointClickedBeforeLineShouldRemainSourceUntilAnotherPointIsChosen() {
		GeoPoint source = add("A = (1, -1)");
		add("g: x = 4");
		GeoPoint center = addCenter();
		Set<String> existingObjects = getObjectNames();
		clickRW(1, -1);
		clickRW(4, -4);
		Mockito.verifyNoInteractions(dialog);
		assertOnlyNewObjects(existingObjects);
		clickRW(3, -2);

		GeoPoint dilated = getOnlyNewObject(existingObjects, GeoPoint.class);
		assertOnlyNewObjects(existingObjects, dilated);
		assertPoint(dilated, -1, 0);
		assertDilationInputs(dilated, source, center);
	}

	@ParameterizedTest
	@CsvSource({
		"x = 1, 2, 1, -4, -1, 0, -1, -3",
		"x = 1, -1, 1, -4, 5, 0, 5, -3",
		"x + y = -1, 2, 2, -3, 0, -3, 2, -5"
	})
	void lineShouldDilateToParallelLine(
			@NonNull String equation,
			@NonNull String input,
			double clickX,
			double clickY,
			double x1,
			double y1,
			double x2,
			double y2) {
		GeoLine source = add("g: " + equation);
		GeoPoint center = addCenter();
		factorInput = input;
		Set<String> existingObjects = getObjectNames();
		clickRW(clickX, clickY);
		clickRW(3, -2);

		GeoLine dilated = getOnlyNewObject(existingObjects, GeoLine.class);
		assertOnlyNewObjects(existingObjects, dilated);
		assertLineThrough(dilated, x1, y1);
		assertLineThrough(dilated, x2, y2);
		assertDilationInputs(dilated, source, center);
		assertLineThrough(source, clickX, clickY);
	}

	@Test
	void lineShouldBecomeUndefinedAtZeroFactorAndRecoverWhenFactorChanges() {
		GeoLine source = add("g: x = 1");
		GeoPoint center = addCenter();
		GeoNumeric factor = add("r = 2");
		factorInput = "r";
		Set<String> existingObjects = getObjectNames();
		clickRW(1, -4);
		clickRW(3, -2);
		GeoLine dilated = getOnlyNewObject(existingObjects, GeoLine.class);
		assertSame(factor, assertDilationInputs(dilated, source, center));

		factor.setValue(0);
		factor.updateCascade();
		assertFalse(dilated.isDefined());
		factor.setValue(-1);
		factor.updateCascade();
		assertLineThrough(dilated, 5, 0);
		assertLineThrough(dilated, 5, -3);
		assertLineThrough(source, 1, -4);
		assertOnlyNewObjects(existingObjects, dilated);
	}

	@ParameterizedTest
	@CsvSource({"2, -1, 0, 1", "-2, 7, -4, 1", "0.5, 2, -1.5, 0.25"})
	void circleShouldDilateCenterAndScaleRadiusByAbsoluteFactor(
			@NonNull String input, double expectedX, double expectedY, double expectedRadius) {
		GeoConic source = add("c: (x - 1)^2 + (y + 1)^2 = 0.25");
		GeoPoint center = addCenter();
		factorInput = input;
		Set<String> existingObjects = getObjectNames();
		clickRW(1, -0.5);
		clickRW(3, -2);

		GeoConic dilated = getOnlyNewObject(existingObjects, GeoConic.class);
		assertOnlyNewObjects(existingObjects, dilated);
		assertTrue(dilated.isDefined());
		assertTrue(dilated.isCircle());
		assertEquals(expectedX, dilated.getMidpoint().getX(), 1E-8);
		assertEquals(expectedY, dilated.getMidpoint().getY(), 1E-8);
		assertEquals(expectedRadius, dilated.getCircleRadius(), 1E-8);
		assertDilationInputs(dilated, source, center);
		assertEquals(1, source.getMidpoint().getX(), 1E-8);
		assertEquals(-1, source.getMidpoint().getY(), 1E-8);
		assertEquals(0.5, source.getCircleRadius(), 1E-8);
	}

	@Test
	void segmentShouldDilateEndpointsAndScaleLength() {
		GeoPoint first = add("A = (1, -1)");
		GeoPoint second = add("B = (2, -1)");
		GeoSegment source = add("s = Segment(A, B)");
		GeoPoint center = addCenter();
		factorInput = "-2";
		Set<String> existingObjects = getObjectNames();
		clickRW(1.5, -1);
		clickRW(3, -2);

		GeoSegment dilated = getOnlyNewObject(existingObjects, GeoSegment.class);
		assertOnlyNewObjects(existingObjects, dilated, dilated.getStartPoint(), dilated.getEndPoint());
		assertPoint(dilated.getStartPoint(), 7, -4);
		assertPoint(dilated.getEndPoint(), 5, -4);
		assertEquals(2, dilated.getLength(), 1E-8);
		assertTrue(dilated.isOnPath(new Coords(6, -4, 0, 1), 1E-8));
		assertFalse(dilated.isOnPath(new Coords(4, -4, 0, 1), 1E-8));
		GeoNumeric factor = assertDilationInputs(dilated.getStartPoint(), first, center);
		assertTransformationInputs(AlgoDilate.class, dilated.getEndPoint(), second, factor, center);
		assertPoint(first, 1, -1);
		assertPoint(second, 2, -1);
		assertEquals(1, source.getLength(), 1E-8);
	}

	@ParameterizedTest
	@ValueSource(booleans = {false, true})
	void rayShouldPreserveOrReverseDirectionAccordingToFactorSign(boolean negative) {
		GeoPoint first = add("A = (1, -1)");
		GeoPoint second = add("B = (2, -1)");
		add("ray = Ray(A, B)");
		GeoPoint center = addCenter();
		factorInput = negative ? "-2" : "2";
		Set<String> existingObjects = getObjectNames();
		clickRW(1.5, -1);
		clickRW(3, -2);

		GeoRay dilated = getOnlyNewObject(existingObjects, GeoRay.class);
		GeoPoint end = assertInstanceOf(GeoPoint.class, lookup("B'"));
		assertOnlyNewObjects(existingObjects, dilated, dilated.getStartPoint(), end);
		assertPoint(dilated.getStartPoint(), negative ? 7 : -1, negative ? -4 : 0);
		assertPoint(end, negative ? 5 : 1, negative ? -4 : 0);
		GeoNumeric factor = assertDilationInputs(dilated.getStartPoint(), first, center);
		assertTransformationInputs(AlgoDilate.class, end, second, factor, center);
		assertSame(end, dilated.getParentAlgorithm().getInput()[1]);
		assertTrue(dilated.isOnPath(new Coords(3, negative ? -4 : 0, 0, 1), 1E-8));
		assertFalse(dilated.isOnPath(new Coords(negative ? 8 : -2, negative ? -4 : 0, 0, 1), 1E-8));
		assertPoint(first, 1, -1);
		assertPoint(second, 2, -1);
	}

	@ParameterizedTest
	@ValueSource(doubles = {2, -2, 0})
	void vectorShouldScaleComponentsWithoutTranslationByCenter(double factorValue) {
		GeoVector source = add("u = Vector((1, -1), (3, -4))");
		GeoPoint center = addCenter();
		factorInput = Double.toString(factorValue);
		Set<String> existingObjects = getObjectNames();
		clickRW(2, -2.5);
		clickRW(3, -2);

		GeoVector dilated = getOnlyNewObject(existingObjects, GeoVector.class);
		assertOnlyNewObjects(existingObjects, dilated);
		assertTrue(dilated.isDefined());
		assertEquals(2 * factorValue, dilated.getX(), 1E-8);
		assertEquals(-3 * factorValue, dilated.getY(), 1E-8);
		assertEquals(factorValue, assertDilationInputs(dilated, source, center).getDouble(), 1E-8);
		center.setCoords(4, -3, 1);
		center.updateCascade();
		assertEquals(2 * factorValue, dilated.getX(), 1E-8);
		assertEquals(-3 * factorValue, dilated.getY(), 1E-8);
		assertEquals(2, source.getX(), 1E-8);
		assertEquals(-3, source.getY(), 1E-8);
	}

	@ParameterizedTest
	@ValueSource(doubles = {2, -1})
	void functionShouldDilateHorizontallyAndVertically(double factorValue) {
		GeoFunction source = add("f(x) = x^2 - 4");
		GeoPoint center = addCenter();
		factorInput = Double.toString(factorValue);
		Set<String> existingObjects = getObjectNames();
		clickRW(0, -4);
		clickRW(3, -2);

		GeoFunction dilated = getOnlyNewObject(existingObjects, GeoFunction.class);
		assertOnlyNewObjects(existingObjects, dilated);
		assertTrue(dilated.isDefined());
		if (factorValue > 0) {
			assertEquals(-6, dilated.value(-3), 1E-8);
			assertEquals(-4, dilated.value(-1), 1E-8);
			assertEquals(2, dilated.value(1), 1E-8);
		} else {
			assertEquals(-4, dilated.value(4), 1E-8);
			assertEquals(-1, dilated.value(5), 1E-8);
			assertEquals(0, dilated.value(6), 1E-8);
		}
		assertEquals(factorValue, assertDilationInputs(dilated, source, center).getDouble(), 1E-8);
		assertEquals(-3, source.value(1), 1E-8);
		assertEquals(0, source.value(2), 1E-8);
	}

	@Test
	void functionShouldBecomeUndefinedAtZeroFactorAndRecoverWhenFactorChanges() {
		GeoFunction source = add("f(x) = x^2 - 4");
		GeoPoint center = addCenter();
		GeoNumeric factor = add("r = 2");
		factorInput = "r";
		Set<String> existingObjects = getObjectNames();
		clickRW(0, -4);
		clickRW(3, -2);
		GeoFunction dilated = getOnlyNewObject(existingObjects, GeoFunction.class);
		assertSame(factor, assertDilationInputs(dilated, source, center));

		factor.setValue(0);
		factor.updateCascade();
		assertFalse(dilated.isDefined());
		factor.setValue(0.5);
		factor.updateCascade();
		assertTrue(dilated.isDefined());
		assertEquals(-2.5, dilated.value(1), 1E-8);
		assertEquals(-2.5, dilated.value(2), 1E-8);
		assertEquals(1.5, dilated.value(3), 1E-8);
		assertEquals(-3, source.value(1), 1E-8);
		assertOnlyNewObjects(existingObjects, dilated);
	}

	@ParameterizedTest
	@CsvSource({"false, 2", "true, 2", "false, -2", "true, -2"})
	void polygonShouldDilateAlongWithAnyPreviouslySelectedPoint(
			boolean selectPointFirst, double factorValue) {
		GeoPoint independent = add("P = (5, 1)");
		GeoPolygon source = addTriangle();
		GeoPoint center = addCenter();
		factorInput = Double.toString(factorValue);
		Set<String> existingObjects = getObjectNames();
		if (selectPointFirst) {
			clickRW(5, 1);
		}
		clickRW(1.25, -1.5);
		assertTrue(getApp().getSelectionManager().containsSelectedGeo(source));
		clickRW(3, -2);

		GeoPolygon dilated = getOnlyNewObject(existingObjects, GeoPolygon.class);
		GeoNumeric factor = assertDilationInputs(dilated.getPoint(0), source.getPoint(0), center);
		assertEquals(factorValue, factor.getDouble(), 1E-8);
		assertPolygonStructure(AlgoDilate.class, dilated, source, factor, center);
		if (selectPointFirst) {
			GeoPoint dilatedIndependent = assertInstanceOf(GeoPoint.class, lookup("P'"));
			assertOnlyNewPolygon(existingObjects, dilated, dilatedIndependent);
			assertPoint(dilatedIndependent, factorValue > 0 ? 7 : -1, factorValue > 0 ? 4 : -8);
			assertTransformationInputs(AlgoDilate.class, dilatedIndependent, independent, factor, center);
		} else {
			assertOnlyNewPolygon(existingObjects, dilated);
		}
		assertPoint(dilated.getPoint(0), factorValue > 0 ? -1 : 7, factorValue > 0 ? 0 : -4);
		assertPoint(dilated.getPoint(1), factorValue > 0 ? 1 : 5, factorValue > 0 ? 0 : -4);
		assertPoint(dilated.getPoint(2), factorValue > 0 ? -1 : 7, factorValue > 0 ? -4 : 0);
		assertEquals(4, dilated.getArea(), 1E-8);
		assertPoint(source.getPoint(0), 1, -1);
		assertPoint(source.getPoint(1), 2, -1);
		assertPoint(source.getPoint(2), 1, -3);
		assertPoint(independent, 5, 1);
		source.getPoint(1).setCoords(3, -1, 1);
		source.getPoint(1).updateCascade();
		assertPoint(dilated.getPoint(1), 3, factorValue > 0 ? 0 : -4);
		assertPoint(dilated.getPoint(0), factorValue > 0 ? -1 : 7, factorValue > 0 ? 0 : -4);
		assertPoint(dilated.getPoint(2), factorValue > 0 ? -1 : 7, factorValue > 0 ? -4 : 0);
		assertEquals(2, source.getArea(), 1E-8);
		assertEquals(8, dilated.getArea(), 1E-8);
		assertEquals(4, dilated.getSegments()[0].getLength(), 1E-8);
	}

	@Test
	void listClickShouldBeIgnoredWithoutBlockingSubsequentPointDilation() {
		GeoList list = add("L = {(6, -2), (7, -3)}");
		list.setEuclidianVisible(true);
		list.updateRepaint();
		GeoPoint source = add("A = (1, -1)");
		GeoPoint center = addCenter();
		Set<String> existingObjects = getObjectNames();

		moveMouseRW(6, -2);
		assertTrue(getApp().getActiveEuclidianView().getHits().contains(list));
		assertFalse(ec.getHighlightedgeos().contains(list));
		clickRW(6, -2);
		Mockito.verifyNoInteractions(dialog);
		assertOnlyNewObjects(existingObjects);
		clickRW(1, -1);
		clickRW(3, -2);

		GeoPoint dilated = getOnlyNewObject(existingObjects, GeoPoint.class);
		assertOnlyNewObjects(existingObjects, dilated);
		assertPoint(dilated, -1, 0);
		assertDilationInputs(dilated, source, center);
	}

	@ParameterizedTest
	@ValueSource(booleans = {false, true})
	void rectangleShouldDilateAllPointsAndExcludeCenter(boolean includeCenter) {
		GeoPoint first = add("A = (1, -1)");
		GeoPoint second = add("B = (2, -1)");
		GeoPoint center = addCenter();
		factorInput = "-1";
		Set<String> existingObjects = getObjectNames();
		dragRW(0.5, -0.5, includeCenter ? 3.5 : 2.5, -2.5);
		clickRW(3, -2);

		GeoPoint firstDilated = assertInstanceOf(GeoPoint.class, lookup("A'"));
		GeoPoint secondDilated = assertInstanceOf(GeoPoint.class, lookup("B'"));
		assertOnlyNewObjects(existingObjects, firstDilated, secondDilated);
		assertPoint(firstDilated, 5, -3);
		assertPoint(secondDilated, 4, -3);
		GeoNumeric factor = assertDilationInputs(firstDilated, first, center);
		assertTransformationInputs(AlgoDilate.class, secondDilated, second, factor, center);
		assertPoint(first, 1, -1);
		assertPoint(second, 2, -1);
		assertPoint(center, 3, -2);
	}

	@Test
	void rectangleShouldDilatePolygonWithoutDuplicatingVertices() {
		GeoPolygon source = addTriangle();
		GeoPoint center = addCenter();
		Set<String> existingObjects = getObjectNames();
		dragRW(0.5, -0.5, 2.5, -3.5);
		clickRW(3, -2);

		GeoPolygon dilated = getOnlyNewObject(existingObjects, GeoPolygon.class);
		assertOnlyNewPolygon(existingObjects, dilated);
		GeoNumeric factor = assertDilationInputs(dilated.getPoint(0), source.getPoint(0), center);
		assertPolygonStructure(AlgoDilate.class, dilated, source, factor, center);
		assertPoint(dilated.getPoint(0), -1, 0);
		assertPoint(dilated.getPoint(1), 1, 0);
		assertPoint(dilated.getPoint(2), -1, -4);
		assertEquals(4, dilated.getArea(), 1E-8);
	}

	@Test
	void selectingSamePointAsSourceAndCenterShouldSkipSelfDilationAndResetSelection() {
		GeoPoint source = add("A = (1, -1)");
		GeoPoint second = add("B = (2, -1)");
		GeoPoint center = addCenter();
		Set<String> existingObjects = getObjectNames();
		clickRW(1, -1);
		resetMouseLocation();
		clickRW(1, -1);
		assertFalse(dilationSucceeded);
		assertOnlyNewObjects(existingObjects);
		clickRW(2, -1);
		assertOnlyNewObjects(existingObjects);
		clickRW(3, -2);

		GeoPoint dilated = getOnlyNewObject(existingObjects, GeoPoint.class);
		assertOnlyNewObjects(existingObjects, dilated);
		assertPoint(dilated, 1, 0);
		assertDilationInputs(dilated, second, center);
		assertTrue(dilationSucceeded);
		assertPoint(source, 1, -1);
	}

	@Test
	void hoveringShouldHighlightCenterWithoutSubmittingFactor() {
		GeoPoint source = add("A = (1, -1)");
		GeoPoint center = addCenter();
		GeoPoint otherCenter = add("P = (5, -3)");
		Set<String> existingObjects = getObjectNames();
		clickRW(1, -1);
		moveMouseRW(5, -3);
		assertTrue(ec.getHighlightedgeos().contains(otherCenter));
		Mockito.verifyNoInteractions(dialog);
		assertOnlyNewObjects(existingObjects);
		clickRW(3, -2);

		GeoPoint dilated = getOnlyNewObject(existingObjects, GeoPoint.class);
		assertOnlyNewObjects(existingObjects, dilated);
		assertPoint(dilated, -1, 0);
		assertDilationInputs(dilated, source, center);
	}

	@Test
	void toolShouldUseNewCenterAndFactorAfterCompletion() {
		GeoPoint first = add("A = (1, -1)");
		GeoPoint second = add("B = (2, -1)");
		GeoPoint center = addCenter();
		GeoPoint nextCenter = add("P = (5, -3)");
		Set<String> beforeFirstDilation = getObjectNames();
		clickRW(1, -1);
		clickRW(3, -2);
		GeoPoint firstDilated = getOnlyNewObject(beforeFirstDilation, GeoPoint.class);
		assertOnlyNewObjects(beforeFirstDilation, firstDilated);
		assertDilationInputs(firstDilated, first, center);
		Set<String> existingObjects = getObjectNames();
		factorInput = "-1";
		clickRW(2, -1);
		Mockito.verify(dialog)
				.showNumberInputDialogDilate(
						Mockito.anyString(),
						Mockito.any(GeoPolygon[].class),
						Mockito.any(GeoPointND[].class),
						Mockito.any(GeoElement[].class),
						Mockito.same(ec));
		clickRW(5, -3);

		GeoPoint secondDilated = getOnlyNewObject(existingObjects, GeoPoint.class);
		assertOnlyNewObjects(existingObjects, secondDilated);
		assertPoint(secondDilated, 8, -5);
		assertDilationInputs(secondDilated, second, nextCenter);
		assertPoint(firstDilated, -1, 0);
	}

	@ParameterizedTest
	@ValueSource(ints = {0, 1, 2})
	void dilationBackendShouldRejectEmptyPivotOnlyAndUnsupportedSelections(int selection) {
		GeoPoint center = addCenter();
		GeoElement text = add("message = \"ignored\"");
		GeoNumeric factor = add("k = 2");
		GeoElement[] sources =
				selection == 0 ? new GeoElement[0] : new GeoElement[] {selection == 1 ? center : text};
		Set<String> existingObjects = getObjectNames();

		assertFalse(
				DialogManager.doDilate(getKernel(), factor, new GeoPointND[] {center}, sources, ec));

		assertOnlyNewObjects(existingObjects);
		assertTrue(ec.getJustCreatedGeos().isEmpty());
		assertPoint(center, 3, -2);
	}

	@Test
	void dilationBackendShouldTransformListWhileSkippingUnsupportedInputsAndCenter() {
		GeoList source = add("L = {(1, -1), (2, -1)}");
		GeoPoint first = assertInstanceOf(GeoPoint.class, source.get(0));
		GeoPoint second = assertInstanceOf(GeoPoint.class, source.get(1));
		GeoPoint center = addCenter();
		GeoNumeric factor = add("k = 2");
		GeoElement text = add("message = \"ignored\"");
		Set<String> existingObjects = getObjectNames();

		// Lists are supported by the backend, although the tool's hit filter ignores them.
		assertTrue(DialogManager.doDilate(
				getKernel(),
				factor,
				new GeoPointND[] {center},
				new GeoElement[] {text, source, center},
				ec));

		GeoList dilated = getOnlyNewObject(existingObjects, GeoList.class);
		assertOnlyNewObjects(existingObjects, dilated);
		assertEquals(List.of(dilated), ec.getJustCreatedGeos());
		assertTransformationInputs(AlgoDilate.class, dilated, source, factor, center);
		assertEquals(2, dilated.size());
		assertPoint(assertInstanceOf(GeoPoint.class, dilated.get(0)), -1, 0);
		assertPoint(assertInstanceOf(GeoPoint.class, dilated.get(1)), 1, 0);
		assertNotSame(first, dilated.get(0));
		assertNotSame(second, dilated.get(1));
		assertPoint(first, 1, -1);
		assertPoint(second, 2, -1);
		first.setCoords(2, -2, 1);
		source.updateCascade();
		assertPoint(assertInstanceOf(GeoPoint.class, dilated.get(0)), 1, -2);
		assertPoint(assertInstanceOf(GeoPoint.class, dilated.get(1)), 1, 0);
		factor.setValue(-1);
		factor.updateCascade();
		assertPoint(assertInstanceOf(GeoPoint.class, dilated.get(0)), 4, -2);
		assertPoint(assertInstanceOf(GeoPoint.class, dilated.get(1)), 4, -3);
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

	private @NonNull GeoNumeric assertDilationInputs(
			@NonNull GeoElementND dilated, @NonNull GeoElement source, @NonNull GeoPoint center) {
		AlgoDilate algorithm = assertInstanceOf(AlgoDilate.class, dilated.getParentAlgorithm());
		GeoNumeric factor = assertInstanceOf(GeoNumeric.class, algorithm.getInput()[1]);
		assertTransformationInputs(AlgoDilate.class, dilated, source, factor, center);
		return factor;
	}
}
