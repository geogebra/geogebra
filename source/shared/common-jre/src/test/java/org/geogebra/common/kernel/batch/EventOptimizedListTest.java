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

package org.geogebra.common.kernel.batch;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.Iterator;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.kernel.geos.GeoElement;
import org.junit.Before;
import org.junit.Test;

public class EventOptimizedListTest extends BaseUnitTest {

	private EventOptimizedList eventOptimizedList;

	@Before
	public void setupEventOptimizedListTest() {
		eventOptimizedList = new EventOptimizedList();
	}

	@Test
	public void testAddingSameUpdate() {
		GeoElement element = getElementFactory().createGeoLine();

		addUpdateEvent(element);
		addUpdateEvent(element);
		addUpdateEvent(element);

		assertEquals(1, count());
	}

	@Test
	public void testAddingRemoveEvent() {
		GeoElement firstElement = getElementFactory().createGeoLine();
		GeoElement secondElement = getElementFactory().createGeoLine();
		GeoElement thirdElement = getElementFactory().createGeoLine();

		addUpdateEvent(firstElement);
		addUpdateEvent(firstElement);
		addUpdateEvent(firstElement);

		addRemoveEvent(firstElement);

		addUpdateEvent(thirdElement);

		addUpdateEvent(secondElement);
		addUpdateEvent(secondElement);
		addRemoveEvent(secondElement);

		Iterator<Event> iterator = eventOptimizedList.iterator();

		assertEquals("remove", iterator.next().getName());
		Event updateEvent = iterator.next();
		assertEquals("update", updateEvent.getName());
		assertEquals(thirdElement, updateEvent.getParameters()[0]);
		assertEquals("remove", iterator.next().getName());
		assertFalse(iterator.hasNext());
	}

	private void addUpdateEvent(GeoElement element) {
		addEvent("update", element);
	}

	private void addRemoveEvent(GeoElement element) {
		addEvent("remove", element);
	}

	private void addEvent(String name, GeoElement element) {
		addEvent(new Event(name, new Object[] { element }));
	}

	private void addEvent(Event event) {
		eventOptimizedList.add(event);
	}

	private int count() {
		int count = 0;
		Iterator<Event> iterator = eventOptimizedList.iterator();
		while (iterator.hasNext()) {
			count++;
			iterator.next();
		}
		return count;
	}
}
