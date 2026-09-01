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

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.Test;

class StateTests {
	@Test
	void testMutableStateValueChangesWithoutListeners() {
		MutableState<@NonNull String> mutableState = new MutableState<>("Initial value");
		assertEquals("Initial value", mutableState.get());
		mutableState.set("First change");
		assertEquals("First change", mutableState.get());
		mutableState.set("Second change");
		assertEquals("Second change", mutableState.get());
	}

	@Test
	void testMutableStateValueChangesWithSingleListener() {
		MutableState<@NonNull String> mutableState = new MutableState<>("Initial value");
		List<@NonNull String> changedValues = new ArrayList<>();

		mutableState.set("First change");
		State.Subscription subscription = mutableState.subscribe(changedValues::add);
		mutableState.set("Second change");
		mutableState.set("Third change");
		subscription.cancel();
		mutableState.set("Fourth change");

		assertEquals(List.of("Second change", "Third change"), changedValues);
	}

	@Test
	void testMutableStateValueChangesWithMultipleListeners() {
		MutableState<@NonNull String> mutableState = new MutableState<>("Initial value");
		List<@NonNull String> changedValues1 = new ArrayList<>();
		List<@NonNull String> changedValues2 = new ArrayList<>();

		mutableState.set("First change");
		State.Subscription firstSubscription = mutableState.subscribe(changedValues1::add);
		mutableState.set("Second change");
		State.Subscription secondSubscription = mutableState.subscribe(changedValues2::add);
		mutableState.set("Third change");
		firstSubscription.cancel();
		mutableState.set("Fourth change");
		secondSubscription.cancel();
		mutableState.set("Fifth change");

		assertEquals(List.of("Second change", "Third change"), changedValues1);
		assertEquals(List.of("Third change", "Fourth change"), changedValues2);
	}

	@Test
	void testMutableStateValueChangedToTheSameValue() {
		MutableState<@NonNull String> mutableState = new MutableState<>("Initial value");
		List<@NonNull String> changedValues = new ArrayList<>();

		State.Subscription subscription = mutableState.subscribe(changedValues::add);
		mutableState.set("New value");
		mutableState.set("New value");
		mutableState.set("Another value");
		mutableState.set("Another value");
		subscription.cancel();

		assertEquals(List.of("New value", "Another value"), changedValues);
	}

	@Test
	void testMutableStateSubscriptionsForTheSameListenerAreIndependent() {
		MutableState<@NonNull String> mutableState = new MutableState<>("Initial value");
		List<@NonNull String> changedValues = new ArrayList<>();
		State.Listener<@NonNull String> listener = changedValues::add;

		State.Subscription firstSubscription = mutableState.subscribe(listener);
		State.Subscription secondSubscription = mutableState.subscribe(listener);
		firstSubscription.cancel();
		mutableState.set("New value");
		secondSubscription.cancel();

		assertEquals(List.of("New value"), changedValues);
	}

	@Test
	void testMutableStateSubscriptionCancellationIsIdempotent() {
		MutableState<@NonNull String> mutableState = new MutableState<>("Initial value");
		List<@NonNull String> changedValues = new ArrayList<>();
		State.Listener<@NonNull String> listener = changedValues::add;

		State.Subscription oldSubscription = mutableState.subscribe(listener);
		oldSubscription.cancel();
		State.Subscription currentSubscription = mutableState.subscribe(listener);
		oldSubscription.cancel();
		mutableState.set("New value");
		currentSubscription.cancel();

		assertEquals(List.of("New value"), changedValues);
	}

	@Test
	void testOneStateDependentDerivedStateValueChangesWithoutListeners() {
		MutableState<@NonNull Integer> mutableState = new MutableState<>(1);
		State<@NonNull Integer> derivedState = DerivedState.of(mutableState, value -> value * 10);

		assertEquals(10, derivedState.get());
		mutableState.set(2);
		assertEquals(20, derivedState.get());
		mutableState.set(3);
		assertEquals(30, derivedState.get());
	}

	@Test
	void testOneStateDependentDerivedStateValueChangesWithSingleListener() {
		MutableState<@NonNull Integer> mutableState = new MutableState<>(1);
		State<@NonNull Integer> derivedState = DerivedState.of(mutableState, a -> a * 10);
		List<@NonNull Integer> changedValues = new ArrayList<>();

		mutableState.set(2);
		State.Subscription subscription = derivedState.subscribe(changedValues::add);
		mutableState.set(3);
		mutableState.set(4);
		subscription.cancel();
		mutableState.set(5);

		assertEquals(List.of(30, 40), changedValues);
	}

	@Test
	void testOneStateDependentDerivedStateValueChangedToTheSameValue() {
		MutableState<@NonNull Integer> mutableState = new MutableState<>(1);
		State<@NonNull Boolean> derivedState = DerivedState.of(mutableState, a -> a % 2 == 0);
		List<@NonNull Boolean> changedValues = new ArrayList<>();

		State.Subscription subscription = derivedState.subscribe(changedValues::add);
		mutableState.set(2); // 2 % 2 == 0 true
		mutableState.set(4); // 4 % 2 == 0 true
		mutableState.set(5); // 5 % 2 == 0 false
		mutableState.set(7); // 7 % 2 == 0 false
		subscription.cancel();

		assertEquals(List.of(true, false), changedValues);
	}

	@Test
	void testDerivedStateSubscriptionsForTheSameListenerAreIndependent() {
		MutableState<@NonNull Integer> mutableState = new MutableState<>(1);
		State<@NonNull Integer> derivedState = DerivedState.of(mutableState, value -> value * 10);
		List<@NonNull Integer> changedValues = new ArrayList<>();
		State.Listener<@NonNull Integer> listener = changedValues::add;

		State.Subscription firstSubscription = derivedState.subscribe(listener);
		State.Subscription secondSubscription = derivedState.subscribe(listener);
		firstSubscription.cancel();
		mutableState.set(2);
		secondSubscription.cancel();

		assertEquals(List.of(20), changedValues);
	}

	@Test
	void testDerivedStateSubscriptionCancellationIsIdempotent() {
		MutableState<@NonNull Integer> mutableState = new MutableState<>(1);
		State<@NonNull Integer> derivedState = DerivedState.of(mutableState, value -> value * 10);
		List<@NonNull Integer> changedValues = new ArrayList<>();
		State.Listener<@NonNull Integer> listener = changedValues::add;

		State.Subscription oldSubscription = derivedState.subscribe(listener);
		oldSubscription.cancel();
		State.Subscription currentSubscription = derivedState.subscribe(listener);
		oldSubscription.cancel();
		mutableState.set(2);
		currentSubscription.cancel();

		assertEquals(List.of(20), changedValues);
	}

	@Test
	void testTwoStateDependentDerivedStateValueChangesWithoutListeners() {
		MutableState<@NonNull String> mutableState1 = new MutableState<>("Initial value 1");
		MutableState<@NonNull String> mutableState2 = new MutableState<>("Initial value 2");
		State<@NonNull String> derivedState = DerivedState.of(mutableState1, mutableState2,
				(value1, value2) -> String.join(" - ", value1, value2));

		assertEquals("Initial value 1 - Initial value 2", derivedState.get());
		mutableState1.set("First change 1");
		assertEquals("First change 1 - Initial value 2", derivedState.get());
		mutableState2.set("First change 2");
		assertEquals("First change 1 - First change 2", derivedState.get());
		mutableState1.set("Second change 1");
		assertEquals("Second change 1 - First change 2", derivedState.get());
	}

	@Test
	void testTwoStateDependentDerivedStateHasUpToDateValueAfterTheFirstListenerIsAttached() {
		MutableState<@NonNull String> mutableState1 = new MutableState<>("Initial value 1");
		MutableState<@NonNull String> mutableState2 = new MutableState<>("Initial value 2");
		State<@NonNull String> derivedState = DerivedState.of(mutableState1, mutableState2,
				(value1, value2) -> String.join(" - ", value1, value2));

		mutableState1.set("First change 1");
		State.Subscription subscription = derivedState.subscribe(ignored -> { });

		assertEquals("First change 1 - Initial value 2", derivedState.get());
		subscription.cancel();
	}

	@Test
	void testTwoStateDependentDerivedStateValueChangesWithSingleListener() {
		MutableState<@NonNull String> mutableState1 = new MutableState<>("Initial value 1");
		MutableState<@NonNull String> mutableState2 = new MutableState<>("Initial value 2");
		State<@NonNull String> derivedState = DerivedState.of(mutableState1, mutableState2,
				(value1, value2) -> String.join(" - ", value1, value2));
		List<@NonNull String> changedValues = new ArrayList<>();

		mutableState1.set("First change 1");
		State.Subscription subscription = derivedState.subscribe(changedValues::add);
		mutableState1.set("Second change 1");
		mutableState2.set("First change 2");
		subscription.cancel();
		mutableState2.set("Second change 2");

		assertEquals(List.of(
				"Second change 1 - Initial value 2",
				"Second change 1 - First change 2"), changedValues);
	}

	@Test
	void testTwoStateDependentDerivedStateValueChangesWithMultipleListeners() {
		MutableState<@NonNull String> mutableState1 = new MutableState<>("Initial value 1");
		MutableState<@NonNull String> mutableState2 = new MutableState<>("Initial value 2");
		State<@NonNull String> derivedState = DerivedState.of(mutableState1, mutableState2,
				(value1, value2) -> String.join(" - ", value1, value2));
		List<@NonNull String> changedValues1 = new ArrayList<>();
		List<@NonNull String> changedValues2 = new ArrayList<>();

		mutableState1.set("First change 1");
		State.Subscription firstSubscription = derivedState.subscribe(changedValues1::add);
		mutableState1.set("Second change 1");
		State.Subscription secondSubscription = derivedState.subscribe(changedValues2::add);
		mutableState2.set("First change 2");
		firstSubscription.cancel();
		mutableState1.set("Third change 1");
		secondSubscription.cancel();
		mutableState2.set("Second change 2");

		assertEquals(List.of(
				"Second change 1 - Initial value 2",
				"Second change 1 - First change 2"), changedValues1);
		assertEquals(List.of(
				"Second change 1 - First change 2",
				"Third change 1 - First change 2"), changedValues2);
	}

	@Test
	void testTwoStateDependentDerivedStateValueChangedToTheSameValue() {
		MutableState<@NonNull Integer> mutableState1 = new MutableState<>(1);
		MutableState<@NonNull Integer> mutableState2 = new MutableState<>(0);
		State<@NonNull Boolean> derivedState = DerivedState.of(mutableState1, mutableState2,
				(a, b) -> a % 3 == b);
		List<@NonNull Boolean> changedValues = new ArrayList<>();

		State.Subscription subscription = derivedState.subscribe(changedValues::add);
		mutableState1.set(3); // 3 % 3 == 0 true
		mutableState1.set(6); // 6 % 3 == 0 true
		mutableState2.set(1); // 6 % 3 == 1 false
		mutableState2.set(2); // 6 % 3 == 2 false
		mutableState1.set(8); // 8 % 3 == 2 true
		mutableState1.set(5); // 5 % 3 == 2 true
		subscription.cancel();

		assertEquals(List.of(true, false, true), changedValues);
	}

	@Test
	void testThreeStateDependentDerivedStateValueChangesWithMultipleListeners() {
		MutableState<@NonNull String> mutableState1 = new MutableState<>("Initial value 1");
		MutableState<@NonNull String> mutableState2 = new MutableState<>("Initial value 2");
		MutableState<@NonNull String> mutableState3 = new MutableState<>("Initial value 3");
		State<@NonNull String> derivedState = DerivedState.of(mutableState1, mutableState2,
				mutableState3, (value1, value2, value3) ->
						String.join(" - ", value1, value2, value3));
		List<@NonNull String> changedValues1 = new ArrayList<>();
		List<@NonNull String> changedValues2 = new ArrayList<>();

		mutableState1.set("First change 1");
		State.Subscription firstSubscription = derivedState.subscribe(changedValues1::add);
		mutableState1.set("Second change 1");
		State.Subscription secondSubscription = derivedState.subscribe(changedValues2::add);
		mutableState2.set("First change 2");
		firstSubscription.cancel();
		mutableState3.set("First change 3");
		secondSubscription.cancel();
		mutableState3.set("Second change 3");

		assertEquals(List.of(
				"Second change 1 - Initial value 2 - Initial value 3",
				"Second change 1 - First change 2 - Initial value 3"), changedValues1);
		assertEquals(List.of(
				"Second change 1 - First change 2 - Initial value 3",
				"Second change 1 - First change 2 - First change 3"), changedValues2);
	}
}
