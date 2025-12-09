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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.geogebra.common.kernel.geos.GeoElement;
import org.junit.Test;

public class GroupControllerTest extends BaseEuclidianControllerTest {

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
		GeoElement p = add("p=Polygon((0,0), (0,-3), (3, -3), (3, 0))");
		GeoElement q = add("q=Polygon((4,0), (4,-3), (7, -3), (7, 0))");
		List<GeoElement> list = Arrays.asList(p, q);
		getApp().getKernel().getConstruction().createGroup(new ArrayList<>(list));
		return list;
	}

}
