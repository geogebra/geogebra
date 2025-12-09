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
 * See https://www.geogebra.org/license for full licensing details'
 */

package org.geogebra.common.gui.view.algebra;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.geogebra.common.SuiteSubApp;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.test.BaseAppTestSetup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@SuppressWarnings({"checkstyle:variableDeclarationUsageDistanceCheck"})
public class AlgebraViewUIAdapterTest extends BaseAppTestSetup {

	private AlgebraViewUIAdapter algebraView;
	private Listener listener;

	@BeforeEach
	public void setup() {
		setupApp(SuiteSubApp.GRAPHING);
		algebraView = new AlgebraViewUIAdapter(getApp());
		algebraView.setVisible(true);
		listener = new Listener();
		algebraView.getItems().listener = listener;
	}

	@Test
	public void testAddPoint() {
		GeoElement geo = evaluateGeoElement("(1,2)");
		assertTrue(listener.itemsChanged);
		assertEquals(1, algebraView.getItems().getNumberOfItems());
		AlgebraViewItem item = algebraView.getItems().getItem(0);
		assertEquals(geo, item.geo);
		assertEquals(AlgebraViewItem.MarbleState.ACTIVE, item.getHeader().marbleState);
		assertTrue(item.getInputRow().isVisible);
		assertFalse(item.getOutputRow().isVisible);
	}

	@Test
	public void testAddTangent() {
		GeoElement c = evaluateGeoElement("c: Circle((0, 0), 5)");
		GeoElement a = evaluateGeoElement("A = (6, 6)");
		// this will create two output rows, tangents f & g
		GeoElementND[] geos = evaluate("Tangent(A, c)");
		GeoElement f = (GeoElement) geos[0];
		GeoElement g = (GeoElement) geos[1];

		assertEquals(4, algebraView.getItems().getNumberOfItems());

		AlgebraViewItem itemC = algebraView.getItems().getItem(0);
		assertEquals(c, itemC.geo);
		assertTrue(itemC.getInputRow().isVisible);
		assertTrue(itemC.getOutputRow().isVisible);

		AlgebraViewItem itemA = algebraView.getItems().getItem(1);
		assertEquals(a, itemA.geo);
		assertTrue(itemA.getInputRow().isVisible);
		assertFalse(itemA.getOutputRow().isVisible);

		AlgebraViewItem itemF = algebraView.getItems().getItem(2);
		assertEquals(f, itemF.geo);
		assertTrue(itemF.getInputRow().isVisible);
		assertTrue(itemF.getOutputRow().isVisible);

		AlgebraViewItem itemG = algebraView.getItems().getItem(3);
		assertEquals(g, itemG.geo);
		assertFalse(itemG.getInputRow().isVisible);
		assertTrue(itemG.getOutputRow().isVisible);
	}

	@Test
	public void testPolynomialRoots() {
		evaluateGeoElement("x^(3) - 2x^(2) - 5x+6");
		assertEquals(1, algebraView.getItems().getNumberOfItems());
		GeoElementND[] roots = evaluate("roots(f)");
		assertEquals(4, algebraView.getItems().getNumberOfItems());
		GeoElement rootA = (GeoElement) roots[0];
		GeoElement rootB = (GeoElement) roots[1];
		GeoElement rootC = (GeoElement) roots[2];

		AlgebraViewItem itemA = algebraView.getItems().getItem(1);
		assertEquals(rootA, itemA.geo);
		assertTrue(itemA.getInputRow().isVisible);
		assertTrue(itemA.getOutputRow().isVisible);

		AlgebraViewItem itemB = algebraView.getItems().getItem(2);
		assertEquals(rootB, itemB.geo);
		assertEquals(itemA.geo.getParentAlgorithm(), itemB.geo.getParentAlgorithm());
		assertFalse(itemB.getInputRow().isVisible);
		assertTrue(itemB.getOutputRow().isVisible);

		AlgebraViewItem itemC = algebraView.getItems().getItem(3);
		assertEquals(rootC, itemC.geo);
		assertEquals(itemA.geo.getParentAlgorithm(), itemC.geo.getParentAlgorithm());
		assertFalse(itemC.getInputRow().isVisible);
		assertTrue(itemC.getOutputRow().isVisible);
	}

	private static class Listener implements AlgebraViewItems.Listener {

		boolean itemsChanged = false;
		boolean forceReload = false;
		AlgebraViewItem changedItem = null;

		@Override
		public void itemsChanged(boolean forceReload) {
			itemsChanged = true;
			this.forceReload = forceReload;
		}

		@Override
		public void itemChanged(AlgebraViewItem item) {
			changedItem = item;
		}
	}
}
