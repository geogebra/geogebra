package org.geogebra.common.euclidian;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.geogebra.common.kernel.geos.GeoElement;
import org.junit.Test;

public class GroupControllerTest extends BaseControllerTest {

	@Test
	public void clickShouldSelectAllGeosInGroup() {
		setMode(EuclidianConstants.MODE_SELECT_MOW);
		List<GeoElement> polygons = prepareGroupedGeos();
		click(50, 50);
		assertTrue(polygons.get(0).isSelected());
		assertTrue(polygons.get(1).isSelected());
	}

	@Test
	public void secondClickShouldMakeFocusedSelection() {
		setMode(EuclidianConstants.MODE_SELECT_MOW);
		List<GeoElement> polygons = prepareGroupedGeos();
		click(50, 50);
		click(50, 80);
		assertEquals(polygons.get(0), getApp().getSelectionManager().getFocusedGroupElement());
	}

	private List<GeoElement> prepareGroupedGeos() {
		add("p=Polygon((0,0), (0,-3), (3, -3), (3, 0))");
		add("q=Polygon((4,0), (4,-3), (7, -3), (7, 0))");
		GeoElement p = lookup("p");
		GeoElement q = lookup("q");
		List<GeoElement> list = Arrays.asList(p, q);
		getApp().getKernel().getConstruction().createGroup(new ArrayList<>(list));
		return list;
	}

}
