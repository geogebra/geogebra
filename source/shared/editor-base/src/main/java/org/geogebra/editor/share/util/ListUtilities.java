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

package org.geogebra.editor.share.util;

import java.util.function.Predicate;

import javax.annotation.CheckForNull;

public class ListUtilities {

	/**
	 * Finds the first item in the list that matches the predicate
	 * @param iterable list to search items in
	 * @param predicate predicate
	 * @param <T> type
	 * @return first item or null
	 */
	@CheckForNull public static <T> T findFirst(Iterable<T> iterable, Predicate<T> predicate) {
		for (T item : iterable) {
			if (predicate.test(item)) {
				return item;
			}
		}
		return null;
	}
}
