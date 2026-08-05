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

package org.geogebra.common.util;

import java.util.ArrayList;
import java.util.List;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

/**
 * A multicast (i.e., one-to-many) eventing / notification mechanism.
 *
 * @param <T> The type of the event arguments.
 */
@SuppressWarnings("PMD.FieldDeclarationsShouldBeAtStartOfClass")
public final class MulticastEvent<T> {

	/**
	 * A "value" for no-payload events.
	 */
	public static final Void VOID = new Void();

	/**
	 * An argument type for events that have no payload.
	 */
	public static final class Void {
		Void() {
			// prevent instantiation outside this file
		}
	}

	/**
	 * The notification listener interface.
	 * @param <T> The type of the event arguments.
	 */
	@FunctionalInterface
	public interface Listener<T> {
		/**
		 * @param argument event argument
		 */
		void notify(@Nullable T argument);
	}

	private final List<MulticastEvent.Listener<T>> listeners = new ArrayList<>();

	/**
	 * Add a listener to the list of listeners (if not yet registered).
	 * @param listener A listener.
	 */
	public void addListener(MulticastEvent.@NonNull Listener<T> listener) {
		if (listeners.contains(listener)) {
			return;
		}
		listeners.add(listener);
	}

	/**
	 * Remove a listener from the list of listeners (if registered).
	 * @param listener A listener.
	 */
	public void removeListener(MulticastEvent.@NonNull Listener<T> listener) {
		listeners.remove(listener);
	}

	/**
	 * Notify all listeners, forwarding the {@code argument}.
	 * @param argument event argument (payload)
	 */
	public void notifyListeners(@Nullable T argument) {
		listeners.forEach(listener -> listener.notify(argument));
	}
}
