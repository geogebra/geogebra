package org.geogebra.common.kernel.batch;

import org.geogebra.common.kernel.kernelND.GeoElementND;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;

class EventOptimizedList {

	private final LinkedList<Event> events;
	private final Set<String> single;

	EventOptimizedList() {
		events = new LinkedList<>();
		single = new HashSet<>(Arrays.asList("update", "updateAuxiliaryObject",
				"updateHighlight", "rename", "repaintView"));
	}

	void add(Event event) {
		events.add(event);
		optimize();
	}

	void clear() {
		events.clear();
	}

	private void optimize() {
		Iterator<Event> iterator = events.descendingIterator();
		Event event = iterator.next();
		String name = event.getName();
		if (single.contains(name)) {
			optimizeSingle(iterator, event);
		} else if ("remove".equals(name)) {
			optimizeRemove(iterator, event);
		}
	}

	// Removes all previous occurences of this event with these parameter
	private void optimizeSingle(Iterator<Event> iterator, Event addedEvent) {
		while (iterator.hasNext()) {
			Event event = iterator.next();
			if (event.equals(addedEvent)) {
				iterator.remove();
			}
		}
	}

	// Removes all events which refer to this element
	private void optimizeRemove(Iterator<Event> iterator, Event addedEvent) {
		Object[] newParameters = addedEvent.getParameters();
		GeoElementND removingGeo = (GeoElementND) newParameters[0];
		while (iterator.hasNext()) {
			Event event = iterator.next();
			Object[] parameters = event.getParameters();
			if (parameters.length > 0 && parameters[0] == removingGeo) {
				iterator.remove();
			}
		}
	}

	Iterator<Event> iterator() {
		return events.iterator();
	}
}
