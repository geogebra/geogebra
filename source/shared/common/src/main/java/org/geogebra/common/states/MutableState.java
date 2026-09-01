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

package org.geogebra.common.states;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.jspecify.annotations.NonNull;

/**
 * Mutable implementation of {@link State} with an additional setter to change its value.
 * <p>
 * <b>Example:</b>
 * <pre>{@code
 *     private final MutableState<@Nonnull Boolean> buttonVisible = new MutableState<>(false);
 *
 *     // Modify the button visibility internally
 *     private void someInternalLogic() {
 *         ...
 *         buttonVisible.set(true)
 *         ...
 *     }
 *
 *     // Expose the read-only part of the state for the UI to attach to
 *     public @NonNull State<@Nonnull Boolean> getState() {
 *         return buttonVisible;
 *     }
 * }</pre>
 * @param <T> the type of the value
 */
public final class MutableState<T> implements State<T> {
	private T value;
	private final @NonNull List<Registration<T>> registrations = new ArrayList<>();

	/**
	 * Constructs the state with the given initial value.
	 * @param value the initial value
	 */
	public MutableState(T value) {
		this.value = value;
	}

	/**
	 * Setter for updating the value.
	 * @param value the new value
	 */
	public void set(T value) {
		if (Objects.equals(value, this.value)) {
			return;
		}
		this.value = value;
		List.copyOf(registrations).forEach(registration ->
				registration.listener.valueChanged(value));
	}

	@Override
	public T get() {
		return value;
	}

	@Override
	public State.@NonNull Subscription subscribe(State.@NonNull Listener<T> listener) {
		Registration<T> registration = new Registration<>(listener);
		registrations.add(registration);
		return () -> registrations.remove(registration);
	}
}
