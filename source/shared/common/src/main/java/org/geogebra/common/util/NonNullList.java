package org.geogebra.common.util;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Utility class for creating lists that automatically exclude {@code null} elements.
 */
public class NonNullList {
	/**
	 * Creates a new {@link List} containing the provided elements, excluding any {@code null} values.
	 * @param elements the elements to include in the list; may contain {@code null}s
	 * @return a list containing only non-null elements
	 * @param <T> the type of elements in the list
	 */
	@SafeVarargs
	public static <T> List<T> of(T... elements) {
		return Arrays.stream(elements).filter(Objects::nonNull).collect(Collectors.toList());
	}
}
