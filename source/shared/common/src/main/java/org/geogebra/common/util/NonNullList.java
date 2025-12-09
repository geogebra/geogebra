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
