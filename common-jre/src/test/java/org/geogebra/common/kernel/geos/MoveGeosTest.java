package org.geogebra.common.kernel.geos;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.awt.GPoint2D;
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
}
