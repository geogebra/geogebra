package org.geogebra.common.util;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

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
	public interface Listener<T> {
		void notify(@Nullable T arguments);
	}

	private final List<MulticastEvent.Listener<T>> listeners = new ArrayList<>();

	/**
	 * Add a listener to the list of listeners (if not yet registered).
	 * @param listener A listener.
	 */
	public void addListener(@Nonnull MulticastEvent.Listener<T> listener) {
		if (listeners.contains(listener)) {
			return;
		}
		listeners.add(listener);
	}

	/**
	 * Remove a listener from the list of listeners (if registered).
	 * @param listener A listener.
	 */
	public void removeListener(@Nonnull MulticastEvent.Listener<T> listener) {
		listeners.remove(listener);
	}

	/**
	 * Notify all listeners, forwarding the {@code arguments}.
	 * @param arguments event arguments (payload)
	 */
	public void notifyListeners(@Nullable T arguments) {
		listeners.forEach(listener -> listener.notify(arguments));
	}
}
