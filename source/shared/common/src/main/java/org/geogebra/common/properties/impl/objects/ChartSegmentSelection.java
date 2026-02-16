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

package org.geogebra.common.properties.impl.objects;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import javax.annotation.CheckForNull;

/**
 * Chart segment selection logic, holding the index of the selected bar/slice, enabling passing
 * between {@code Property}s, updating via {@code ChartSegmentSelectionProperty}, reading by other
 * related {@code Property}s, and registering/unregistering listeners.
 */
public final class ChartSegmentSelection {
	private int index = 0;
	private final Set<Listener> listeners = new HashSet<>();

	/**
	 * Listener for changes in selection.
	 */
	public interface Listener {
		/**
		 * Method called when the selection changed.
		 */
		void chartSegmentSelectionUpdated();
	}

	void setIndex(int index) {
		this.index = index;
		listeners.forEach(Listener::chartSegmentSelectionUpdated);
	}

	/**
	 * @return the index of the selected bar/slice (indexed from 1),
	 * or {@code 0} if all bars/slices are selected
	 */
	public int getIndex() {
		return index;
	}

	/**
	 * Register a listener for selection change notifications.
	 * @param listener the listener to be registered
	 */
	public void registerListener(Listener listener) {
		listeners.add(listener);
	}

	/**
	 * Unregisters a previously registered listener,
	 * undoing the effects of {@link ChartSegmentSelection#registerListener}.
	 * @param listener the listener to be unregistered
	 */
	public void unregisterListener(Listener listener) {
		listeners.remove(listener);
	}

	/**
	 * Applies the setter to the selected segments.
	 * <p>
	 *     If the selection is set to all segments, call the setter on all valid indexes.
	 *     Otherwise, call the setter once for the selected segment.
	 * </p>
	 * @param chartSegmentCount the number of chart segments
	 * @param setter the setter to apply
	 */
	public void forEachSelectedSegment(int chartSegmentCount, Consumer<Integer> setter) {
		selectedSegmentIndexStream(chartSegmentCount).forEach(setter::accept);
	}

	/**
	 * Finds the uniform value that the given getter returns
	 * for segments based on the selection.
	 * <p>
	 *     If the selection is set to all segments, call the getter on all segments
	 *     and return the result only if all values match; otherwise, return {@code null}.
	 *     If a single segment is selected, return the result of the getter for that segment.
	 * </p>
	 * @param chartSegmentCount the number of chart segments
	 * @param getter the getter to call on segments
	 * @return If all segments are selected and the getter returns the same value for all segments,
	 * return that value; return null if the values are different.
	 * If a single segment is selected, return the getter's value for that segment.
	 * @param <T> value type
	 */
	public <T> @CheckForNull T getUniformValueOrNull(
			int chartSegmentCount, Function<Integer, T> getter) {
		List<T> values = selectedSegmentIndexStream(chartSegmentCount)
				.mapToObj(getter::apply).distinct().limit(2).collect(Collectors.toList());
		return values.size() == 1 ? values.get(0) : null;
	}

	/**
	 * Get the value of the first selected segment.
	 * @param chartSegmentCount the number of chart segments
	 * @param getter the getter to call on segments
	 * @return the result of the getter for the first selected segment, which is either the first
	 * segment when all are selected, or the selected segment when there is a single selection.
	 * @param <T> value type
	 */
	public <T> @CheckForNull T getFirstValue(int chartSegmentCount, Function<Integer, T> getter) {
		if (chartSegmentCount == 0) {
			return null;
		}
		if (index == 0) {
			return getter.apply(1);
		}
		return getter.apply(index);
	}

	/**
	 * Maps the selected segments with the given mapper.
	 * @param chartSegmentCount the number of chart segments
	 * @param mapper the mapper to call on segments
	 * @return the stream of mapped selected segments
	 * @param <T> value type
	 */
	public <T> Stream<T> mapSelectedSegments(int chartSegmentCount, Function<Integer, T> mapper) {
		return selectedSegmentIndexStream(chartSegmentCount).mapToObj(mapper::apply);
	}

	private IntStream selectedSegmentIndexStream(int chartSegmentCount) {
		return index == 0 ? IntStream.rangeClosed(1, chartSegmentCount) : IntStream.of(index);
	}
}
