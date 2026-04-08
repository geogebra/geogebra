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

import java.io.Serial;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Map with limited capacity, can be used as LRU (least recently used) cache.
 * @param <K> key type
 * @param <V> value type
 */
public class MaxSizeHashMap<K, V> extends LinkedHashMap<K, V> {

	@Serial
	private static final long serialVersionUID = 1L;

	private final int maxSize;

	/**
	 * @param maxSize maximum size
	 */
	public MaxSizeHashMap(int maxSize) {
		this.maxSize = maxSize;
	}

	@Override
	protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
		return size() > maxSize;
	}

}
