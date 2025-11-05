/*
GeoGebra - Dynamic Mathematics for Schools
Copyright (c) GeoGebra GmbH, Altenbergerstr 69, 4040 Linz, Austria
https://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it
under the terms of the GNU General Public License as published by
the Free Software Foundation.
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
