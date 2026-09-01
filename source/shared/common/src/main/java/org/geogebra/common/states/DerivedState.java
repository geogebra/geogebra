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
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.google.j2objc.annotations.Weak;

/**
 * A read-only state computed from source states.
 * <p>
 * <b>Examples:</b>
 * <pre>{@code
 *     private final MutableState<@NonNull String> input = new MutableState<>("");
 *     // Initialize the derived state using a single source state
 *     private final DerivedState<@Nullable String> errorMessage = DerivedState.of(input,
 *             // There is no error message if the input is valid, otherwise it is "Invalid input"
 *             input -> isValid(input) ? null : "Invalid input");
 *
 *     // Change the input state internally
 *     private void internalLogic() {
 *         ...
 *         input.set(newInput);
 *         ...
 *     }
 *
 *     // Expose the state of the error message for the UI to attach to
 *     public @NonNull State<@Nullable String> getErrorMessageState() {
 *         return errorMessage;
 *     }
 * }</pre>
 * <pre>{@code
 *     enum Button { ACTIVE, INACTIVE, HIDDEN }
 *
 *     private final MutableState<@NonNull Boolean> buttonVisible = new MutableState<>(false);
 *     private final MutableState<@Nullable View> view = new MutableState<>(null);
 *     // Initialize the derived state by combining multiple states
 *     private final DerivedState<@NonNull Button> button = DerivedState.of(buttonVisible, view,
 *             // The button is ACTIVE if the view is shown and button visible,
 *             // INACTIVE if the button is visible but the view is not shown
 *             // and HIDDEN if the button is not visible
 *             (buttonVisible, view) -> buttonVisible ? view != null ? ACTIVE : INACTIVE : HIDDEN)
 *
 *     // Change the related states internally
 *     private void internalLogic() {
 *         ...
 *         buttonVisible.set(true);
 *         ...
 *         view.set(new View());
 *         ...
 *     }
 *
 *     // Expose the combined button state for the UI to attach to
 *     public @NonNull State<@NonNull Button> getButtonState() {
 *         return button;
 *     }
 * }</pre>
 * @param <T> the type of the derived state
 */
public final class DerivedState<T> implements State<T> {
	private final @NonNull List<State<?>> sourceStates;
	private final @NonNull List<Subscription> subscriptions = new ArrayList<>();
	private final @NonNull Supplier<T> derive;
	private final @NonNull List<Registration<T>> registrations = new ArrayList<>();
	private T value;

	// Uses a weak owner to avoid a retain cycle after J2ObjC translation.
	private static final class SourceListener<S, T> implements State.Listener<S> {
		private final @Weak @Nullable DerivedState<T> owner;

		private SourceListener(@NonNull DerivedState<T> owner) {
			this.owner = owner;
		}

		@Override
		public void valueChanged(S ignored) {
			DerivedState<T> currentOwner = owner;
			if (currentOwner != null) {
				currentOwner.updateDerivedState();
			}
		}
	}

	private DerivedState(@NonNull List<State<?>> sourceStates, @NonNull Supplier<T> derive) {
		this.derive = derive;
		this.sourceStates = sourceStates;
	}

	/**
	 * Creates a state derived from one source state.
	 * @param state the source state
	 * @param derive the function that derives the value
	 * @param <A> the source value type
	 * @param <T> the derived value type
	 * @return the derived state
	 */
	public static <A, T> @NonNull State<T> of(@NonNull State<A> state,
			@NonNull Function<A, T> derive) {
		return new DerivedState<>(List.of(state), () -> derive.apply(state.get()));
	}

	/**
	 * Creates a state derived from two source states.
	 * @param firstState the first source state
	 * @param secondState the second source state
	 * @param derive the function that derives the value
	 * @param <A> the first source value type
	 * @param <B> the second source value type
	 * @param <T> the derived value type
	 * @return the derived state
	 */
	public static <A, B, T> @NonNull State<T> of(@NonNull State<A> firstState,
			@NonNull State<B> secondState, @NonNull BiFunction<A, B, T> derive) {
		return new DerivedState<>(List.of(firstState, secondState),
				() -> derive.apply(firstState.get(), secondState.get()));
	}

	/**
	 * Creates a state derived from three source states.
	 * @param firstState the first source state
	 * @param secondState the second source state
	 * @param thirdState the third source state
	 * @param derive the function that derives the value
	 * @param <A> the first source value type
	 * @param <B> the second source value type
	 * @param <C> the third source value type
	 * @param <T> the derived value type
	 * @return the derived state
	 */
	public static <A, B, C, T> @NonNull State<T> of(@NonNull State<A> firstState,
			@NonNull State<B> secondState, @NonNull State<C> thirdState,
			@NonNull TriFunction<A, B, C, T> derive) {
		return new DerivedState<>(List.of(firstState, secondState, thirdState),
				() -> derive.apply(firstState.get(), secondState.get(), thirdState.get()));
	}

	private void updateDerivedState() {
		T updatedValue = derive.get();
		if (Objects.equals(value, updatedValue)) {
			return;
		}
		value = updatedValue;
		List.copyOf(registrations).forEach(registration ->
				registration.listener.valueChanged(updatedValue));
	}

	@Override
	public T get() {
		return registrations.isEmpty() ? derive.get() : value;
	}

	@Override
	public @NonNull Subscription subscribe(@NonNull Listener<T> listener) {
		boolean wasEmpty = registrations.isEmpty();
		Registration<T> registration = new Registration<>(listener);
		registrations.add(registration);
		if (wasEmpty) {
			value = derive.get();
			sourceStates.forEach(sourceState ->
					subscriptions.add(sourceState.subscribe(new SourceListener<>(this))));
		}
		return () -> {
			registrations.remove(registration);
			if (registrations.isEmpty()) {
				subscriptions.forEach(Subscription::cancel);
				subscriptions.clear();
			}
		};
	}
}
