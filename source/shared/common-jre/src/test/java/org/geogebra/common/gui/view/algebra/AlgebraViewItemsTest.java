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

package org.geogebra.common.gui.view.algebra;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Set;

import org.geogebra.common.SuiteSubApp;
import org.geogebra.common.contextmenu.ContextMenuFactory;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.geos.GeoBoolean;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.test.BaseAppTestSetup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class AlgebraViewItemsTest extends BaseAppTestSetup {

	private Construction construction;
	private AlgebraViewItems items;

	@BeforeEach
	public void setup() {
		setupApp(SuiteSubApp.GRAPHING);
		construction = getApp().getKernel().getConstruction();
		items = new AlgebraViewItems(getApp());
	}

	@Test
	public void testAdd2Remove() {
		GeoElement geo1 = new GeoBoolean(construction, false);
		GeoElement geo2 = new GeoBoolean(construction, true);
		items.onGeoAdded(geo1);
		items.onGeoAdded(geo2);
		assertEquals(List.of(1, 2), items.getItemIds());
		assertTrue(items.getModifiedItemIds().isEmpty());
		items.onGeoRemoved(geo1);
		assertEquals(List.of(2), items.getItemIds());
		assertTrue(items.getModifiedItemIds().isEmpty());
	}

	@Test
	public void testAdd2Modify() {
		GeoElement geo1 = new GeoBoolean(construction, false);
		GeoElement geo2 = new GeoBoolean(construction, true);
		items.onGeoAdded(geo1);
		items.onGeoAdded(geo2);
		assertEquals(List.of(1, 2), items.getItemIds());
		assertTrue(items.getModifiedItemIds().isEmpty());
		items.onGeoUpdated(geo1);
		assertEquals(List.of(1, 2), items.getItemIds());
		assertEquals(Set.of(1), items.getModifiedItemIds());
	}

	@Test
	public void testAdd2ModifyRemove() {
		GeoElement geo1 = new GeoBoolean(construction, false);
		GeoElement geo2 = new GeoBoolean(construction, true);
		items.onGeoAdded(geo1);
		items.onGeoAdded(geo2);
		assertEquals(List.of(1, 2), items.getItemIds());
		assertTrue(items.getModifiedItemIds().isEmpty());
		items.onGeoUpdated(geo1);
		assertEquals(List.of(1, 2), items.getItemIds());
		assertEquals(Set.of(1), items.getModifiedItemIds());
		items.onGeoRemoved(geo1);
		assertEquals(List.of(2), items.getItemIds());
		assertTrue(items.getModifiedItemIds().isEmpty());
	}

	@Test
	public void testAddModifyClear() {
		GeoElement geo1 = new GeoBoolean(construction, false);
		items.onGeoAdded(geo1);
		assertTrue(items.getModifiedItemIds().isEmpty());
		items.onGeoRenamed(geo1);
		assertEquals(Set.of(1), items.getModifiedItemIds());
		items.onGeoUpdated(geo1);
		assertEquals(Set.of(1), items.getModifiedItemIds());
		items.clear();
		assertTrue(items.getModifiedItemIds().isEmpty());
	}

	@Test
	public void testIndexRenumbering() {
		GeoElement geo1 = new GeoBoolean(construction, false);
		Integer id1 = items.onGeoAdded(geo1);
		AlgebraViewItem item1 = items.getItemById(id1);
		assertEquals(0, item1.getIndex());

		GeoElement geo2 = new GeoBoolean(construction, false);
		Integer id2 = items.onGeoAdded(geo2);
		AlgebraViewItem item2 = items.getItemById(id2);
		assertEquals(1, item2.getIndex());

		items.onGeoRemoved(geo1);
		item2 = items.getItemById(id2);
		assertEquals(0, item2.getIndex());

		items.clear();
		assertTrue(items.getModifiedItemIds().isEmpty());
	}
}
