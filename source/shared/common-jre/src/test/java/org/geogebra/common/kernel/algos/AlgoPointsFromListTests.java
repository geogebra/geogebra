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

package org.geogebra.common.kernel.algos;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.geogebra.common.SuiteSubApp;
import org.geogebra.common.gui.view.algebra.AlgebraViewItem;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.test.BaseAppTestSetup;
import org.geogebra.test.annotation.Issue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class AlgoPointsFromListTests extends BaseAppTestSetup {
	@BeforeEach
	public void setupApp() {
		setupApp(SuiteSubApp.GRAPHING);
	}

	@Test
	public void testPointFromFlat2DList() {
		evaluate("Point({1, 2})");
		assertEquals("A = (1, 2)", print("A"));
	}

	@Test
	public void testPointFromFlat3DList() {
		evaluate("Point({1, 2, 3})");

		assertEquals("A = (1, 2, 3)", print("A"));
		assertTrue(lookup("A").isGeoElement3D());
	}

	@Test
	public void test2DPointFromShortList() {
		evaluate("Point({1})");
		assertEquals("A = (1, 0)", print("A"));
	}

	@Test
	public void test3DListIgnoresExtraCoordinates() {
		evaluate("Point({1, 2, 3, 4})");

		assertEquals("A = (1, 2, 3)", print("A"));
		assertTrue(lookup("A").isGeoElement3D());
	}

	@Test
	public void testPointsFromNested2DList() {
		evaluate("Point({{1, 2}, {3, 4}})");

		assertEquals("A = (1, 2)", print("A"));
		assertEquals("B = (3, 4)", print("B"));
	}

	@Test
	public void testPointsFromNested3DList() {
		evaluate("Point({{1, 2, 3}, {4, 5, 6}})");

		assertEquals("A = (1, 2, 3)", print("A"));
		assertEquals("B = (4, 5, 6)", print("B"));
		assertTrue(lookup("A").isGeoElement3D());
		assertTrue(lookup("B").isGeoElement3D());
	}

	@Test
	public void testPointsFromMixedNestedList() {
		evaluate("l1 = {{1, 2}, {1, 2, 3}, {10, 20}}");
		evaluate("Point(l1)");

		assertEquals("A = (1, 2)", print("A"));
		assertEquals("B = (1, 2, 3)", print("B"));
		assertEquals("C = (10, 20)", print("C"));
		assertFalse(lookup("A").isGeoElement3D());
		assertTrue(lookup("B").isGeoElement3D());
		assertFalse(lookup("C").isGeoElement3D());
	}

	@Test
	public void testMixedPointOutputsArePackedInAlgebraView() {
		evaluate("l1 = {{1, 2}, {3, 4, 5}, {6, 7}}");
		evaluate("Point(l1)");

		AlgebraViewItem itemA = new AlgebraViewItem(lookup("A"));
		AlgebraViewItem itemB = new AlgebraViewItem(lookup("B"));
		AlgebraViewItem itemC = new AlgebraViewItem(lookup("C"));

		assertTrue(itemA.getInputRow().isVisible());
		assertFalse(itemB.getInputRow().isVisible());
		assertFalse(itemC.getInputRow().isVisible());
		assertTrue(itemB.getOutputRow().isVisible());
		assertTrue(itemC.getOutputRow().isVisible());
		assertNull(itemB.getOutputRow().getOutputFormat());
		assertNull(itemC.getOutputRow().getOutputFormat());
		assertTrue(itemB.getOutputRow().getLaTeX().contains("B"));
		assertTrue(itemC.getOutputRow().getLaTeX().contains("C"));
	}

	@Test
	public void testPointsFromNestedListWithListReferences() {
		evaluate("l1 = {1, 2}");
		evaluate("l2 = {3, 4, 5}");
		evaluate("Point({{10, 20, 30}, l1, {40, 50}, l2})");

		assertEquals("A = (10, 20, 30)", print("A"));
		assertEquals("B = (1, 2)", print("B"));
		assertEquals("C = (40, 50)", print("C"));
		assertEquals("D = (3, 4, 5)", print("D"));
		assertTrue(lookup("A").isGeoElement3D());
		assertFalse(lookup("B").isGeoElement3D());
		assertFalse(lookup("C").isGeoElement3D());
		assertTrue(lookup("D").isGeoElement3D());
	}

	@Test
	public void testNested3DListIgnoresExtraCoordinates() {
		evaluate("Point({{1, 2, 3}, {4, 5, 6, 7}})");

		assertEquals("A = (1, 2, 3)", print("A"));
		assertEquals("B = (4, 5, 6)", print("B"));
		assertTrue(lookup("A").isGeoElement3D());
		assertTrue(lookup("B").isGeoElement3D());
	}

	@Test
	public void testNested2DListWithShortRowDoesNotCreateUndefinedPoint() {
		evaluate("Point({{1, 2}, {3}})");

		assertEquals("A = (1, 2)", print("A"));
		assertEquals("B = (3, 0)", print("B"));
	}

	@Test
	public void testOutputsGrowWhenListGrows() {
		evaluate("l1 = {{1, 2}}");
		evaluate("Point(l1)");
		evaluate("SetValue(l1, {{1, 2}, {3, 4}, {5, 6}})");

		assertEquals("A = (1, 2)", print("A"));
		assertEquals("B = (3, 4)", print("B"));
		assertEquals("C = (5, 6)", print("C"));
		assertNull(lookup("D"));
		assertFalse(lookup("A").isGeoElement3D());
		assertFalse(lookup("B").isGeoElement3D());
		assertFalse(lookup("C").isGeoElement3D());
	}

	@Test
	public void testOutputsBecomeUndefinedWhenListShrinks() {
		evaluate("l1 = {{1, 2}, {3, 4}, {5, 6}}");
		evaluate("Point(l1)");
		evaluate("SetValue(l1, {{7, 8}})");

		assertEquals("A = (7, 8)", print("A"));
		assertEquals("B = (?, ?)", print("B"));
		assertEquals("C = (?, ?)", print("C"));
		assertNull(lookup("D"));
		assertFalse(lookup("A").isGeoElement3D());
		assertFalse(lookup("B").isDefined());
		assertFalse(lookup("C").isDefined());
	}

	@Test
	public void testOutputsRecoverWhenListGrowsAfterShrink() {
		evaluate("l1 = {{1, 2}, {3, 4}, {5, 6}}");
		evaluate("Point(l1)");
		evaluate("SetValue(l1, {{7, 8}})");
		evaluate("SetValue(l1, {{9, 10}, {11, 12}})");

		assertEquals("A = (9, 10)", print("A"));
		assertEquals("B = (11, 12)", print("B"));
		assertEquals("C = (?, ?)", print("C"));
		assertNull(lookup("D"));
		assertFalse(lookup("A").isGeoElement3D());
		assertFalse(lookup("B").isGeoElement3D());
		assertFalse(lookup("C").isDefined());
	}

	@Test
	public void testMixedOutputsKeepTheirInitialDimensionWhenSourceChanges() {
		evaluate("l1 = {{1, 2}, {3, 4, 5}, {6, 7}}");
		evaluate("Point(l1)");
		evaluate("SetValue(l1, {{8, 9, 10}, {11, 12}, {13, 14, 15}})");

		assertEquals("A = (?, ?)", print("A"));
		assertEquals("B = (11, 12, 0)", print("B"));
		assertEquals("C = (?, ?)", print("C"));
		assertNull(lookup("D"));
		assertFalse(lookup("A").isGeoElement3D());
		assertTrue(lookup("B").isGeoElement3D());
		assertFalse(lookup("C").isGeoElement3D());
		assertFalse(lookup("A").isDefined());
		assertTrue(lookup("B").isDefined());
		assertFalse(lookup("C").isDefined());
	}

	@Test
	public void testChangingThree2DPointsToOne2DPointThenTwo3DPointsMakesOutputsUndefined() {
		evaluate("l1 = {{1, 2}, {3, 4}, {5, 6}}");
		evaluate("Point(l1)");
		evaluate("SetValue(l1, {{7, 8}})");
		evaluate("SetValue(l1, {{9, 10, 11}, {12, 13, 14}})");

		assertEquals("A = (?, ?)", print("A"));
		assertEquals("B = (?, ?)", print("B"));
		assertEquals("C = (?, ?)", print("C"));
		assertNull(lookup("D"));
		assertFalse(lookup("A").isGeoElement3D());
		assertFalse(lookup("B").isGeoElement3D());
		assertFalse(lookup("C").isGeoElement3D());
		assertFalse(lookup("A").isDefined());
		assertFalse(lookup("B").isDefined());
		assertFalse(lookup("C").isDefined());
	}

	@Test
	public void testChangingOne2DPointToOne3DPointMakesExistingPointUndefined() {
		evaluate("l1 = {1, 2}");
		evaluate("Point(l1)");
		evaluate("SetValue(l1, {3, 4, 5})");

		assertEquals("A = (?, ?)", print("A"));
		assertNull(lookup("B"));
		assertFalse(lookup("A").isGeoElement3D());
		assertFalse(lookup("A").isDefined());
	}

	@Test
	public void testChangingOne3DPointToOne2DPointUpdatesExistingPoint() {
		evaluate("l1 = {1, 2, 3}");
		evaluate("Point(l1)");
		evaluate("SetValue(l1, {4, 5})");

		assertEquals("A = (4, 5, 0)", print("A"));
		assertNull(lookup("B"));
		assertTrue(lookup("A").isGeoElement3D());
		assertTrue(lookup("A").isDefined());
	}

	@Test
	public void testChangingTwo2DPointsToTwo3DPointsMakesDependentOutputUndefined() {
		evaluate("l1 = {{1, 2}, {3, 4}}");
		evaluate("Point(l1)");
		evaluate("Midpoint(A, B)");
		evaluate("SetValue(l1, {{1, 2, 3}, {4, 5, 6}})");

		assertEquals("A = (?, ?)", print("A"));
		assertEquals("B = (?, ?)", print("B"));
		assertEquals("C = (?, ?)", print("C"));
		assertFalse(lookup("A").isGeoElement3D());
		assertFalse(lookup("B").isGeoElement3D());
		assertFalse(lookup("C").isGeoElement3D());
		assertFalse(lookup("A").isDefined());
		assertFalse(lookup("B").isDefined());
		assertFalse(lookup("C").isDefined());
	}

	@Test
	public void testOutputsBecomeUndefinedWhenSourceListIsEmpty() {
		evaluate("l1 = {{1, 2}, {3, 4}}");
		evaluate("Point(l1)");
		evaluate("SetValue(l1, {})");

		assertFalse(lookup("A").isDefined());
		assertFalse(lookup("B").isDefined());
	}

	@Test
	public void testSourceChangingToUnsupportedShapeUndefinesOutputs() {
		evaluate("l1 = {{1, 2}, {3, 4}}");
		evaluate("Point(l1)");
		evaluate("SetValue(l1, {1, 2, 3, 4})");

		assertFalse(lookup("A").isDefined());
		assertFalse(lookup("B").isDefined());
	}

	@Test
	public void testChangingThree2DPointsToOne3DPointMakesExistingPointsUndefined() {
		evaluate("l1 = {{1, 2}, {3, 4}, {5, 6}}");
		evaluate("Point(l1)");
		evaluate("SetValue(l1, {{7, 8, 9}})");

		assertEquals("A = (?, ?)", print("A"));
		assertEquals("B = (?, ?)", print("B"));
		assertEquals("C = (?, ?)", print("C"));
		assertFalse(lookup("A").isGeoElement3D());
		assertFalse(lookup("B").isGeoElement3D());
		assertFalse(lookup("C").isGeoElement3D());
		assertFalse(lookup("A").isDefined());
		assertFalse(lookup("B").isDefined());
		assertFalse(lookup("C").isDefined());
	}

	@Test
	public void testChangingTwo2DPointsToTwo3DPointsMakesExistingPointsUndefined() {
		evaluate("l1 = {{1, 2}, {3, 4}}");
		evaluate("Point(l1)");
		evaluate("SetValue(l1, {{1, 2, 3}, {4, 5, 6}})");

		assertEquals("A = (?, ?)", print("A"));
		assertEquals("B = (?, ?)", print("B"));
		assertNull(lookup("C"));
		assertNull(lookup("D"));
		assertFalse(lookup("A").isGeoElement3D());
		assertFalse(lookup("B").isGeoElement3D());
		assertFalse(lookup("A").isDefined());
		assertFalse(lookup("B").isDefined());
	}

	@Test
	public void testChangingTwo3DPointsToTwo2DPointsUpdatesExistingPoints() {
		evaluate("l1 = {{1, 2, 3}, {4, 5, 6}}");
		evaluate("Point(l1)");
		evaluate("SetValue(l1, {{10, 20}, {30, 40}})");

		assertEquals("A = (10, 20, 0)", print("A"));
		assertEquals("B = (30, 40, 0)", print("B"));
		assertNull(lookup("C"));
		assertNull(lookup("D"));
		assertTrue(lookup("A").isGeoElement3D());
		assertTrue(lookup("B").isGeoElement3D());
		assertTrue(lookup("A").isDefined());
		assertTrue(lookup("B").isDefined());
	}

	@Test
	public void testChangingTwo2DPointsToTwo3DPointsThenToTwo2DPointsUpdatesExistingPoints() {
		evaluate("l1 = {{1, 2}, {3, 4}}");
		evaluate("Point(l1)");
		evaluate("SetValue(l1, {{1, 2, 3}, {4, 5, 6}})");
		evaluate("SetValue(l1, {{7, 8}, {9, 10}})");

		assertEquals("A = (7, 8)", print("A"));
		assertEquals("B = (9, 10)", print("B"));
		assertNull(lookup("C"));
		assertNull(lookup("D"));
		assertFalse(lookup("A").isGeoElement3D());
		assertFalse(lookup("B").isGeoElement3D());
		assertTrue(lookup("A").isDefined());
		assertTrue(lookup("B").isDefined());
	}

	@Test
	public void testChangingTwo2DPointsToThree3DPointsMakesExistingPointsUndefinedAndAddsPoint() {
		evaluate("l1 = {{1, 2}, {3, 4}}");
		evaluate("Point(l1)");
		evaluate("SetValue(l1, {{5, 6, 7}, {8, 9, 10}, {11, 12, 13}})");

		assertEquals("A = (?, ?)", print("A"));
		assertEquals("B = (?, ?)", print("B"));
		assertEquals("C = (11, 12, 13)", print("C"));
		assertNull(lookup("D"));
		assertFalse(lookup("A").isGeoElement3D());
		assertFalse(lookup("B").isGeoElement3D());
		assertTrue(lookup("C").isGeoElement3D());
		assertFalse(lookup("A").isDefined());
		assertFalse(lookup("B").isDefined());
		assertTrue(lookup("C").isDefined());
	}

	@Test
	public void testChangingTwo3DPointsToThree2DPointsUpdatesExistingPointsAndAddsPoint() {
		evaluate("l1 = {{1, 2, 3}, {4, 5, 6}}");
		evaluate("Point(l1)");
		evaluate("SetValue(l1, {{7, 8}, {9, 10}, {11, 12}})");

		assertEquals("A = (7, 8, 0)", print("A"));
		assertEquals("B = (9, 10, 0)", print("B"));
		assertEquals("C = (11, 12)", print("C"));
		assertNull(lookup("D"));
		assertTrue(lookup("A").isGeoElement3D());
		assertTrue(lookup("B").isGeoElement3D());
		assertFalse(lookup("C").isGeoElement3D());
		assertTrue(lookup("A").isDefined());
		assertTrue(lookup("B").isDefined());
		assertTrue(lookup("C").isDefined());
	}

	@Test
	public void testChangingTwo3DPointsToThree2DPointsThenThree3DPointsUpdatesAndUndefinesAdded() {
		evaluate("l1 = {{1, 2, 3}, {4, 5, 6}}");
		evaluate("Point(l1)");
		evaluate("SetValue(l1, {{7, 8}, {9, 10}, {11, 12}})");
		evaluate("SetValue(l1, {{13, 14, 15}, {16, 17, 18}, {19, 20, 21}})");

		assertEquals("A = (13, 14, 15)", print("A"));
		assertEquals("B = (16, 17, 18)", print("B"));
		assertEquals("C = (?, ?)", print("C"));
		assertNull(lookup("D"));
		assertTrue(lookup("A").isGeoElement3D());
		assertTrue(lookup("B").isGeoElement3D());
		assertFalse(lookup("C").isGeoElement3D());
		assertTrue(lookup("A").isDefined());
		assertTrue(lookup("B").isDefined());
		assertFalse(lookup("C").isDefined());
	}

	@Test
	public void testRemovingOutputPoint() {
		evaluate("Point({{1, 2, 3}, {4, 5}})");

		assertDoesNotThrow(() -> lookup("A").remove());
		assertNull(lookup("A"));
		assertNotNull(lookup("B"));
	}

	@Test
	public void testRemovingOutputPointWithDependentObjectUndefinesAndHidesPoint() {
		evaluate("Point({{1, 2}, {3, 4}})");
		evaluate("Midpoint(A, B)");

		assertDoesNotThrow(() -> lookup("A").remove());
		assertFalse(lookup("A").isDefined());
		assertFalse(lookup("A").showInAlgebraView());
		assertFalse(lookup("C").isDefined());
	}

	@Test
	@Issue("APPS-7441")
	public void testRemovingPointAfterListChangesFrom2DTo3D() {
		evaluate("l1 = {{1, 2}, {3, 4}}");
		evaluate("Point(l1)");
		evaluate("SetValue(l1, {{1, 2, 3}, {4, 5, 6}})");

		assertDoesNotThrow(() -> lookup("A").remove());
		assertNull(lookup("A"));
		assertNotNull(lookup("B"));
	}

	@Test
	@Issue("APPS-7441")
	public void testRemovingPointAfterListChangesFrom3DTo2D() {
		evaluate("l1 = {{1, 2, 3}, {4, 5, 6}}");
		evaluate("Point(l1)");
		evaluate("SetValue(l1, {{1, 2}, {3, 4}})");

		assertDoesNotThrow(() -> lookup("A").remove());
		assertNull(lookup("A"));
		assertNotNull(lookup("B"));
	}

	private String print(String label) {
		return lookup(label).toString(StringTemplate.editTemplate);
	}
}
