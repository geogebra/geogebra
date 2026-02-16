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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

public class ChartSegmentSelectionTests {
	@Test
	public void testApplyingWithSingleSegmentSelection() {
		ChartSegmentSelection chartSegmentSelection = new ChartSegmentSelection();
		List<Integer> setterCalledForIndexes = new ArrayList<>();

		chartSegmentSelection.setIndex(1);
		chartSegmentSelection.forEachSelectedSegment(3, setterCalledForIndexes::add);
		assertEquals(List.of(1), setterCalledForIndexes);
	}

	@Test
	public void testApplyingWithAllSegmentsSelected() {
		ChartSegmentSelection chartSegmentSelection = new ChartSegmentSelection();
		List<Integer> setterCalledForIndexes = new ArrayList<>();

		chartSegmentSelection.setIndex(0);
		chartSegmentSelection.forEachSelectedSegment(4, setterCalledForIndexes::add);
		assertEquals(List.of(1, 2, 3, 4), setterCalledForIndexes);
	}

	@Test
	public void testGettingUniformValueOrNullWithSingleSegmentSelected() {
		ChartSegmentSelection chartSegmentSelection = new ChartSegmentSelection();
		Function<Integer, Integer> getter = index -> switch (index) {
			case 1 -> 111;
			case 2 -> 222;
			case 3 -> 333;
			default -> throw new IndexOutOfBoundsException();
		};

		chartSegmentSelection.setIndex(1);
		assertEquals(111, chartSegmentSelection.getUniformValueOrNull(3, getter));

		chartSegmentSelection.setIndex(2);
		assertEquals(222, chartSegmentSelection.getUniformValueOrNull(3, getter));

		chartSegmentSelection.setIndex(3);
		assertEquals(333, chartSegmentSelection.getUniformValueOrNull(3, getter));
	}

	@Test
	public void testGettingUniformValueOrNullWithAllSegmentsSelectedWithDifferentValues() {
		ChartSegmentSelection chartSegmentSelection = new ChartSegmentSelection();
		Function<Integer, Integer> getter = index -> switch (index) {
			case 1 -> 111;
			case 2 -> 222;
			case 3 -> 333;
			default -> throw new IndexOutOfBoundsException();
		};
		chartSegmentSelection.setIndex(0);
		assertNull(chartSegmentSelection.getUniformValueOrNull(3, getter));
	}

	@Test
	public void testGettingUniformValueOrNullWithAllSegmentsSelectedWithUniformValues() {
		ChartSegmentSelection chartSegmentSelection = new ChartSegmentSelection();
		Function<Integer, Integer> getter = index -> switch (index) {
			case 1, 2, 3 -> 123;
			default -> throw new IndexOutOfBoundsException();
		};
		chartSegmentSelection.setIndex(0);
		assertEquals(123, chartSegmentSelection.getUniformValueOrNull(3, getter));
	}

	@Test
	public void testGettingFirstValueWithSingleSegmentSelected() {
		ChartSegmentSelection chartSegmentSelection = new ChartSegmentSelection();
		Function<Integer, Integer> getter = index -> switch (index) {
			case 1 -> 111;
			case 2 -> 222;
			case 3 -> 333;
			default -> throw new IndexOutOfBoundsException();
		};
		chartSegmentSelection.setIndex(2);
		assertEquals(222, chartSegmentSelection.<Integer>getFirstValue(3, getter));
	}

	@Test
	public void testGettingFirstValueWithAllSegmentsSelected() {
		ChartSegmentSelection chartSegmentSelection = new ChartSegmentSelection();
		Function<Integer, Integer> getter = index -> switch (index) {
			case 1 -> 111;
			case 2 -> 222;
			case 3 -> 333;
			default -> throw new IndexOutOfBoundsException();
		};
		chartSegmentSelection.setIndex(0);
		assertEquals(111, chartSegmentSelection.<Integer>getFirstValue(3, getter));
	}

	@Test
	public void testMappingWithAllSegmentSelected() {
		ChartSegmentSelection chartSegmentSelection = new ChartSegmentSelection();
		Function<Integer, Integer> mapper = index -> switch (index) {
			case 1 -> 11;
			case 2 -> 22;
			case 3 -> 33;
			default -> throw new IndexOutOfBoundsException();
		};
		chartSegmentSelection.setIndex(0);
		assertEquals(List.of(11, 22, 33), chartSegmentSelection.mapSelectedSegments(3, mapper)
				.collect(Collectors.toList()));
	}

	@Test
	public void testMappingWithSingleSegmentSelection() {
		ChartSegmentSelection chartSegmentSelection = new ChartSegmentSelection();
		Function<Integer, Integer> mapper = index -> switch (index) {
			case 1 -> 11;
			case 2 -> 22;
			case 3 -> 33;
			default -> throw new IndexOutOfBoundsException();
		};
		chartSegmentSelection.setIndex(2);
		assertEquals(List.of(22), chartSegmentSelection.mapSelectedSegments(3, mapper)
				.collect(Collectors.toList()));
	}

	@Test
	public void testHandlingIndexOutOfBoundsExceptionForGettingUniformValueOrNull() {
		ChartSegmentSelection chartSegmentSelection = new ChartSegmentSelection();
		Function<Integer, Integer> getter = index -> {
			throw new IndexOutOfBoundsException();
		};
		chartSegmentSelection.setIndex(0);
		assertNull(chartSegmentSelection.getUniformValueOrNull(0, getter));
	}

	@Test
	public void testHandlingGettingFirstValueWithoutAnySegment() {
		ChartSegmentSelection chartSegmentSelection = new ChartSegmentSelection();
		Function<Integer, Integer> getter = index -> {
			throw new IndexOutOfBoundsException();
		};
		assertNull(chartSegmentSelection.getFirstValue(0, getter));
	}

}
