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

package org.geogebra.common.euclidian;

import static org.geogebra.common.BaseUnitTest.hasValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Collections;

import org.geogebra.common.cas.MockedCasGiac;
import org.geogebra.common.gui.dialog.options.model.AbsoluteScreenPositionModel;
import org.geogebra.common.kernel.geos.GeoCasCell;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoText;
import org.geogebra.common.kernel.geos.MoveGeos;
import org.geogebra.common.kernel.matrix.Coords;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MoveGeosWithArrowKeysTest extends BaseEuclidianControllerTest {

	@BeforeEach
	void setUp() {
		setUpController();
	}

	@Test
	void arrowKeyShouldMoveSegment() {
		add("A = (0,0)");
		GeoElement segment = add("f = Segment(A, (1,-1))");

		moveObjectWithArrowKey(segment, 1, -2);

		checkContent("A = (1, -2)", "f = 1.41421");
	}

	@Test
	void arrowKeyShouldMoveVector() {
		GeoElement vector = add("v = Vector((1,-1))");

		moveObjectWithArrowKey(vector, 1, -2);

		checkContent("v = (2, -3)");
	}

	@Test
	void arrowKeyShouldMoveListContainingVector() {
		GeoElement list = add("list = {Vector((1,-1))}");

		moveObjectWithArrowKey(list, 1, -2);

		checkContent("list = {(2, -3)}");
	}

	@Test
	void arrowKeyShouldNotMoveCasList() {
		MockedCasGiac mockGiac = setupGiac();
		mockGiac.memorize("Intersect(x² + y² = 2, (x - 2)² + y² = 2)", "{(1,1),(1,-1)}");
		GeoCasCell cell = new GeoCasCell(getKernel().getConstruction());
		getKernel().getConstruction().addToConstructionList(cell, false);
		cell.setInput("l5:=Intersect(x^2+y^2=2,(x-2)^2+y^2=2)");
		cell.computeOutput();
		GeoList list = (GeoList) cell.getTwinGeo();
		list.setLabel("l5");

		moveObjectWithArrowKey(list, 1, -2);

		assertThat(list, hasValue("{(1, 1), (1, -1)}"));
	}

	@Test
	void arrowKeyShouldNotMoveFreeCasList() {
		MockedCasGiac mockGiac = setupGiac();
		mockGiac.memorize("Evaluate({(1, -1), (1, 1)})", "{(1,-1),(1,1)}");
		GeoCasCell cell = new GeoCasCell(getKernel().getConstruction());
		getKernel().getConstruction().addToConstructionList(cell, false);
		cell.setInput("l5:={(1, -1), (1, 1)}");
		cell.computeOutput();
		GeoList list = (GeoList) cell.getTwinGeo();
		list.setLabel("l5");

		moveObjectWithArrowKey(list, 1, -2);

		assertThat(list, hasValue("{(1, -1), (1, 1)}"));
	}

	@Test
	void arrowKeyShouldMovePolygonWithFreeInputPoints() {
		add("A = (0,0)");
		GeoElement polygon = add("q = Polygon(A, (0,-1), 4)");

		moveObjectWithArrowKey(polygon, 1, -2);

		checkContent(
				"A = (1, -2)", "q = 1", "f = 1", "g = 1", "B = (2, -3)", "C = (2, -2)", "h = 1", "i = 1");
	}

	@Test
	void arrowKeyShouldMoveChangeablePolygonWithoutMovingSourcePoint() {
		GeoElement point = add("A = (0,0)");
		GeoElement polygon = add("q = Polygon((x(A), y(A)), (2, 0), (2, -2), (0, -2))");

		moveObjectWithArrowKey(polygon, 1, -1);

		assertThat(point, hasValue("(0, 0)"));
		assertThat(polygon, hasValue("6"));
	}

	@Test
	void arrowKeyShouldMovePolygonThroughSharedSourcePoint() {
		GeoElement point = add("A = (0,0)");
		GeoElement polygon = add("q = Polygon(A, A + (2, 0), A + (2, -2), A + (0, -2))");

		moveObjectWithArrowKey(polygon, 1, -1);

		assertThat(point, hasValue("(1, -1)"));
		assertThat(polygon, hasValue("4"));
	}

	@Test
	void arrowKeyShouldNotMoveFixedSegment() {
		add("A = (0,0)");
		GeoElement segment = add("f = Segment(A, (1,-1))");
		add("SetFixed(f,true)");

		moveObjectWithArrowKey(segment, 1, -2);

		checkContent("A = (0, 0)", "f = 1.41421");
	}

	@Test
	void arrowKeyShouldNotMoveFixedPolygon() {
		add("A = (0,0)");
		GeoElement polygon = add("q = Polygon(A, (0,-1), 4)");
		add("SetFixed(q,true)");

		moveObjectWithArrowKey(polygon, 1, -2);

		checkContent(
				"A = (0, 0)", "q = 1", "f = 1", "g = 1", "B = (1, -1)", "C = (1, 0)", "h = 1", "i = 1");
	}

	@Test
	void arrowKeyShouldNotChangeInfiniteCircle() {
		add("A=(1,-1)");
		add("B=(2,-2)");
		add("C=(3,-3)");
		GeoElement circle = add("Circle(A,B,C)");

		moveObjectWithArrowKey(circle, 1, -1);

		assertThat(circle, hasValue("(-0.71x - 0.71y) (∞) = 0"));
	}

	@Test
	void arrowKeyShouldMoveCircleWithCenterPoint() {
		add("A=(1, -1)");
		GeoElement circle = add("Circle(A, 2)");

		moveObjectWithArrowKey(circle, 1, -1);

		assertThat(circle, hasValue("(x - 2)² + (y + 2)² = 4"));
	}

	@Test
	void arrowKeyShouldMoveCircleWithoutLabeledCenterPoint() {
		GeoElement circle = add("c = Circle((1, -1), 2)");

		moveObjectWithArrowKey(circle, 1, -1);

		checkContent("c: (x - 2)² + (y + 2)² = 4");
	}

	@Test
	void arrowKeyShouldMoveEllipseWithMovableInputPoints() {
		GeoElement ellipse = add("e = Ellipse((1, 1), (2, 2), (3, 3))");

		moveObjectWithArrowKey(ellipse, 1, -1);

		checkContent("e: 17x² - 2x y + 17y² - 84x - 12y = -36");
	}

	@Test
	void arrowKeyShouldNotMoveEllipseWithoutMovableInputPoints() {
		GeoElement ellipse = add("e = Ellipse((2, 2), (1, 0.6), 2)");

		moveObjectWithArrowKey(ellipse, 1, -1);

		checkContent("e: 60x² - 11.2x y + 56.16y² - 165.44x - 129.216y = 0.5696");
	}

	@Test
	void arrowKeyShouldMoveRay() {
		GeoElement ray = add("r = Ray((0, 0), (1, -1))");

		moveObjectWithArrowKey(ray, 2, -1);

		checkContent("r: x + y = 1");
	}

	@Test
	void arrowKeyShouldMoveDependentPointWithChangeableCoordinates() {
		add("a = 1");
		add("b = -1");
		GeoElement point = add("A = (a, b)");

		moveObjectWithArrowKey(point, 1, -1);

		checkContent("A = (2, -2)");
	}

	@Test
	void arrowKeyShouldMove3DPolygon() {
		GeoElement pointA = add("A = (1, -1, 0)");
		add("B = (2, -1, 0)");
		add("C = (2, -2, 0)");
		add("D = (1, -2, 0)");
		GeoElement polygon = add("Polygon(A, B, C, D)");

		moveObjectWithArrowKey(polygon, 1, -1);

		assertThat(pointA, hasValue("(2, -2, 0)"));
	}

	@Test
	void arrowKeyShouldMoveTranslatedPoint() {
		add("A = (2, 2)");
		add("v = Vector((-1, -3))");
		GeoElement point = add("Translate(A, v)");

		moveObjectWithArrowKey(point, 1, -1);

		assertThat(point, hasValue("(2, -2)"));
	}

	@Test
	void arrowKeyShouldMoveListContainingTranslatedPoint() {
		add("A = (2, 2)");
		add("v = Vector((-1, -3))");
		GeoElement list = add("{Translate(A, v)}");

		moveObjectWithArrowKey(list, 1, -1);

		assertThat(list, hasValue("{(2, -2)}"));
	}

	@Test
	void arrowKeyShouldNotMoveAnchoredText() {
		GeoText text = add("t=Text(\"T\",(1, 2))");

		moveObjectWithArrowKey(text, 1, -1);

		assertThat(text.getStartPoint(), hasValue("(1, 2)"));
	}

	@Test
	void arrowKeyShouldMoveFreeText() {
		GeoText text = add("t=Text(\"T\")");
		add("SetCoords(t,3,5)");

		moveObjectWithArrowKey(text, 1, -1);

		assertThat(text.getStartPoint(), hasValue("(4, 4)"));
	}

	@Test
	void arrowKeyShouldNotMoveTextWithDependentAbsolutePosition() {
		add("posX = 100");
		add("posY = 100");
		GeoText text = add("Text(\"Try me\")");
		text.setAbsoluteScreenLocActive(true);
		AbsoluteScreenPositionModel modelForX = new AbsoluteScreenPositionModel.ForX(getApp());
		modelForX.setGeos(new GeoElement[] {text});
		modelForX.applyChanges("posX");
		AbsoluteScreenPositionModel modelForY = new AbsoluteScreenPositionModel.ForY(getApp());
		modelForY.setGeos(new GeoElement[] {text});
		modelForY.applyChanges("posY");

		moveObjectWithArrowKey(text, 1, -1);

		assertEquals(100, text.getAbsoluteScreenLocX());
		assertEquals(100, text.getAbsoluteScreenLocY());
	}

	private MockedCasGiac setupGiac() {
		MockedCasGiac mockedCasGiac = new MockedCasGiac();
		mockedCasGiac.applyTo(getApp());
		return mockedCasGiac;
	}

	private void moveObjectWithArrowKey(GeoElement geo, int x, int y) {
		MoveGeos.moveObjects(
				Collections.singletonList(geo),
				new Coords(x, y, 0, 0),
				null,
				null,
				getApp().getActiveEuclidianView());
	}
}
