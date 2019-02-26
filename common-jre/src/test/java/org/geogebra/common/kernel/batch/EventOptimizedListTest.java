package org.geogebra.common.kernel.batch;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.kernel.geos.GeoElement;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Iterator;

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

		Assert.assertEquals(1, count());
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

		Assert.assertEquals("remove", iterator.next().getName());
		Event updateEvent = iterator.next();
		Assert.assertEquals("update", updateEvent.getName());
		Assert.assertEquals(thirdElement, updateEvent.getParameters()[0]);
		Assert.assertEquals("remove", iterator.next().getName());
		Assert.assertFalse(iterator.hasNext());
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
