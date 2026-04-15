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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.geogebra.common.kernel.geos.GeoElement;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class GroupControllerTest extends BaseEuclidianControllerTest {

	@BeforeEach
	public void setUp() {
		setUpController();
	}

	@Test
	public void clickShouldSelectAllGeosInGroup() {
		setMode(EuclidianConstants.MODE_SELECT_MOW);
		List<GeoElement> polygons = prepareGroupedGeos();
		click(100, 100);
		assertTrue(polygons.get(0).isSelected());
		assertTrue(polygons.get(1).isSelected());
	}

	@Test
	public void secondClickShouldMakeFocusedSelection() {
		setMode(EuclidianConstants.MODE_SELECT_MOW);
		List<GeoElement> polygons = prepareGroupedGeos();
		click(100, 100);
		click(100, 130);
		assertEquals(polygons.get(0), getApp().getSelectionManager().getFocusedGroupElement());
	}

	@Test
	public void dragShouldNotSelectPartialGroup() {
		setMode(EuclidianConstants.MODE_SELECT_MOW);
		List<GeoElement> polygons = prepareGroupedGeos();
		dragStart(0, 0);
		dragEnd(250, 250);
		assertEquals(0, getApp().getSelectionManager().getSelectedGeos().size());
	}

	private List<GeoElement> prepareGroupedGeos() {
		GeoElement p = add("p=Polygon({(1,-1), (1,-4), (4, -4), (4, -1)})");
		GeoElement q = add("q=Polygon({(5,-1), (5,-4), (8, -4), (8, -1)})");

		List<GeoElement> list = Arrays.asList(p, q);
		getApp().getKernel().getConstruction().createGroup(new ArrayList<>(list));
		return list;
	}

}
