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

import org.jspecify.annotations.NonNull;

/**
 * A read-only observable value. Clients can read its current value and listen for later changes.
 * <p>
 * <b>Example:</b>
 * <pre>{@code
 *     private @Nullable Subscription subscription;
 *     private final @NonNull State<@Nullable Content> content;
 *
 *     public SomeView(@NonNull ViewModel viewModel) {
 *         content = viewModel.getContent();
 *         updateContent(content.get());
 *     }
 *
 *     // Subscribe a listener to update the view when the state updates
 *     void onViewAppeared() {
 *         subscription = content.subscribe(this::updateContent);
 *     }
 *
 *     // Cancel the subscription to stop updating the view when it is not shown
 *     void onViewDisappeared() {
 *         subscription.cancel();
 *         subscription = null;
 *     }
 * }</pre>
 * @param <T> the type of the value
 */
public interface State<T> {

	/** A cancellable subscription to value-change notifications. */
	@FunctionalInterface
	interface Subscription {
		/** Cancels subscription, stopping future notifications. */
		void cancel();
	}

	/**
	 * Listener notified when the value changes.
	 * @param <T> the type of the value
	 */
	interface Listener<T> {
		/**
		 * Method called to notify that the value has been changed.
		 * @param newValue the new value
		 */
		void valueChanged(T newValue);
	}

	/**
	 * @return the value
	 */
	T get();

	/**
	 * Subscribes a listener to future value-change notifications.
	 * @param listener the listener to subscribe
	 * @return a subscription for later cancellation. Each call to subscribe() must be matched with
	 * a call to cancel() on the corresponding subscription.
	 */
	@NonNull Subscription subscribe(@NonNull Listener<T> listener);
}
