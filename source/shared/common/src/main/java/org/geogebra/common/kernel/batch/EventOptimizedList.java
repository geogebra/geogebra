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

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;

import org.geogebra.common.kernel.geos.GeoElement;

class EventOptimizedList {

	private static final Set<String> SINGLE = new HashSet<>(
			Arrays.asList("update", "updateAuxiliaryObject",
			"updateHighlight", "rename", "repaintView"));

	private final LinkedList<Event> events;

	EventOptimizedList() {
		events = new LinkedList<>();
	}

	private EventOptimizedList(LinkedList<Event> events) {
		this.events = events;
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
		if (SINGLE.contains(name)) {
			optimizeSingle(iterator, event);
		} else if ("remove".equals(name)) {
			optimizeRemove(iterator, event);
		}
	}

	// Removes all previous occurrences of this event with these parameters
	private static void optimizeSingle(Iterator<Event> iterator, Event addedEvent) {
		while (iterator.hasNext()) {
			Event event = iterator.next();
			if (event.equals(addedEvent)) {
				iterator.remove();
			}
		}
	}

	// Removes all events which refer to this element
	private static void optimizeRemove(Iterator<Event> iterator, Event addedEvent) {
		Object[] newParameters = addedEvent.getParameters();
		GeoElement removingGeo = (GeoElement) newParameters[0];
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

	EventOptimizedList copy() {
		return new EventOptimizedList(new LinkedList<>(events));
	}
}
