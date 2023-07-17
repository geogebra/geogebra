package org.geogebra.common.kernel.geos;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.awt.GPoint2D;
import org.geogebra.common.kernel.matrix.Coords;
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
		assertEquals(toMove.size(), 7);
	}

	@Test
	public void testMovingFreeList() {
		GeoList list = add("{(1, 1), (3, 4)}");
		Coords dummyCoords = new Coords(7, 7, 7);
		MoveGeos.moveObjects(Collections.singletonList(list), new Coords(1, 1, 0),
				dummyCoords, dummyCoords, getApp().getActiveEuclidianView());
		assertThat(list, hasValue("{(2, 2), (4, 5)}"));
	}

	@Test
	public void testMovingDependentList() {
		add("A=(5, 6)");
		GeoList list = add("{(1, 1), (3, 4), A}");
		Coords dummyCoords = new Coords(7, 7, 7);
		MoveGeos.moveObjects(Collections.singletonList(list), new Coords(1, 1, 0),
				dummyCoords, dummyCoords, getApp().getActiveEuclidianView());
		assertThat(list, hasValue("{(2, 2), (4, 5), (6, 7)}"));
	}

	@Test
	public void testMoveObjectsWithPointList() {
		GeoList list = add("{(1, 1), (3, 4), (5, 6)}");
		Coords dummyCoords = new Coords(7, 7, 7);
		MoveGeos.moveObjects(Collections.singletonList(list), new Coords(1, 1, 0),
				dummyCoords, dummyCoords, getApp().getActiveEuclidianView());
		assertThat(list, hasValue("{(2, 2), (4, 5), (6, 7)}"));
	}

	@Test
	public void testListElementsUpdatedAfterMove() {
		GeoList list = add("{(1, 1), (3, 4), (5, 6)}");
		Coords dummyCoords = new Coords(7, 7, 7);
		MoveGeos.moveObjects(Collections.singletonList(list), new Coords(1, 1, 0),
				dummyCoords, dummyCoords, getApp().getActiveEuclidianView());
		assertTrue(MoveGeos.updateListHave(list, list.get(0), list.get(1), list.get(2)));
	}

	@Test
	public void testSegmentEndPointsUpdatedAfterMove() {
		GeoPoint A = add("A=(5, 6)");
		GeoPoint B = add("B=(6, 6)");
		GeoList list = add("{Segment(A, B)}");
		Coords dummyCoords = new Coords(7, 7, 7);
		MoveGeos.moveObjects(Collections.singletonList(list), new Coords(1, 1, 0),
				dummyCoords, dummyCoords, getApp().getActiveEuclidianView());
		assertTrue(MoveGeos.updateListHave(list, A, B));
	}
}
