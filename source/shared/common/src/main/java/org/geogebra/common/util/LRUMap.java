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

import java.util.LinkedHashMap;
import java.util.Map;

public class LRUMap<K, V> extends LinkedHashMap<K, V> {

	private static final int MAX_ENTRIES = 100;

	/**
	 * https://docs.oracle.com/javase/6/docs/api/java/util/LinkedHashMap.html#
	 * removeEldestEntry(java.util.Map.Entry)
	 * 
	 * @param eldest
	 *            entry
	 * @return whether eldest enry should be removed
	 */
	@Override
	protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
		return size() > MAX_ENTRIES;
	}

}