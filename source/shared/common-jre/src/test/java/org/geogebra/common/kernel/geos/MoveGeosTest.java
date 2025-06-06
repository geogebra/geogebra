package org.geogebra.common.kernel.geos;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.awt.GPoint2D;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.matrix.Coords;
import org.geogebra.test.annotation.Issue;
import org.junit.Test;

public class MoveGeosTest extends BaseUnitTest {

	@Test
	public void testAddWithSiblingsAndChildNodes() {
		GPoint2D loc = new GPoint2D();
		GeoMindMapNode root = new GeoMindMapNode(getConstruction(), loc);
		GeoMindMapNode child1 = new GeoMindMapNode(getConstruction(), loc);
		child1.setParent(root, GeoMindMapNode.NodeAlignment.LEFT);
		GeoMindMapNode child2 = new GeoMindMapNode(getConstruction(), loc);
		child2.setParent(child1, GeoMindMapNode.NodeAlignment.LEFT);
		GeoMindMapNode child3 = new GeoMindMapNode(getConstruction(), loc);
		child3.setParent(child2, GeoMindMapNode.NodeAlignment.LEFT);
		GeoConic c1 = new GeoConic(getConstruction());
		getConstruction().createGroup(new ArrayList<>(Arrays.asList(child1, c1)));
		GeoConic c2 = new GeoConic(getConstruction());
		getConstruction().createGroup(new ArrayList<>(Arrays.asList(child2, c2)));
		GeoConic c3 = new GeoConic(getConstruction());
		getConstruction().createGroup(new ArrayList<>(Arrays.asList(child3, c3)));
		ArrayList<GeoElement> toMove = new ArrayList<>();
		MoveGeos.addWithSiblingsAndChildNodes(root, toMove, getApp().getActiveEuclidianView());
		assertEquals(7, toMove.size());
	}

	@Test
	public void testMovingFreeList() {
		GeoList list = add("{(1, 1), (3, 4)}");
		moveListDownRightByUnit(list);
		assertThat(list, hasValue("{(2, 2), (4, 5)}"));
	}

	@Test
	public void testMovingDependentList1() {
		add("A=(5, 6)");
		GeoList list = add("{(1, 1), (3, 4), A}");
		moveListDownRightByUnit(list);
		assertThat(list, hasValue("{(2, 2), (4, 5), (6, 7)}"));
	}

	@Test
	public void testMovingDependentList2() {
		GeoPoint A = add("A = (1, 2)");
		GeoPoint B = add("B = (4, 3)");
		GeoList list = add("{Line(A, B)}");
		moveListDownRightByUnit(list);
		assertThat(list, hasValue("{-x + 3y = 7}"));
		assertArrayEquals(new double[]{2, 3}, A.getInhomCoords().get());
		assertArrayEquals(new double[]{5, 4}, B.getInhomCoords().get());
	}

	@Test
	@Issue("APPS-6035")
	public void testMovingDependentList3() {
		GeoList list = add("Sequence(Text(k,(k,k)),k,1,5)");
		GeoPointND startPoint = ((GeoText) list.get(0)).getStartPoint();
		assertArrayEquals(new double[]{1, 1}, startPoint.getInhomCoords().get());
		moveListDownRightByUnit(list);
		assertArrayEquals(new double[]{1, 1}, startPoint.getInhomCoords().get());
	}

	@Test
	public void testMoveObjectsWithPointList() {
		GeoList list = add("{(1, 1), (3, 4), (5, 6)}");
		moveListDownRightByUnit(list);
		assertThat(list, hasValue("{(2, 2), (4, 5), (6, 7)}"));
	}

	@Test
	public void testListElementsUpdatedAfterMove() {
		GeoList list = add("{(1, 1), (3, 4), (5, 6)}");
		moveListDownRightByUnit(list);
		assertTrue(MoveGeos.updateListHave(list, list.get(0), list.get(1), list.get(2)));
	}

	@Test
	public void testSegmentEndPointsUpdatedAfterMove() {
		GeoPoint A = add("A=(5, 6)");
		GeoPoint B = add("B=(6, 6)");
		GeoList list = add("{Segment(A, B)}");
		moveListDownRightByUnit(list);
		assertArrayEquals(new double[]{6, 7}, A.getInhomCoords().get());
		assertArrayEquals(new double[]{7, 7}, B.getInhomCoords().get());
	}

	private void moveListDownRightByUnit(GeoList list) {
		Coords dummyCoords = new Coords(7, 7, 7);
		MoveGeos.moveObjects(Collections.singletonList(list), new Coords(1, 1, 0),
				dummyCoords, dummyCoords, getApp().getActiveEuclidianView());
	}

	@Test
	public void testCircleInListWithoutPoint() {
		shouldValueBeEqualAfterMove("{Circle((2, 2), 1)}", "(x - 3)² + (y - 3)² = 1");
	}

	@Test
	public void testMatrixElementFixed() {
		// also relevant for Element(CSolve({x^2+1=0}),1)
		shouldValueBeEqualAfterMove("Element({{(1,2)}},1)", "(1, 2)");
	}

	private void shouldValueBeEqualAfterMove(String command, String expected) {
		GeoList list = add(command);
		moveListDownRightByUnit(list);
		GeoElement element = list.get(0);
		assertThat(element, hasValue(expected));
	}

	@Test
	public void testEllipseInListWithoutPoint() {
		shouldValueBeEqualAfterMove("{Ellipse((-1, 0), (0, 0), (0, 1))}",
				"19.31x² + 23.31y² - 19.31x - 46.63y = 0");

	}

	@Test
	public void testPolygonInListWithoutPoint() {
		GeoList list = add("{Polygon((-1, 0), (1, 0), (0, 1))}");
		moveListDownRightByUnit(list);
		GeoElement element = list.get(0);
		assertThat(element.getDefinition(StringTemplate.defaultTemplate),
				is("Polygon((0, 1), (2, 1), (1, 2))"));

	}
}
