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

import java.util.Iterator;
import java.util.LinkedHashMap;

public class MaxSizeHashMap<V, T> extends LinkedHashMap<V, T> {

	private static final long serialVersionUID = 1L;

	private int maxSize;

	public MaxSizeHashMap(int maxSize) {
		this.maxSize = maxSize;
	}

	@Override
	public T put(V key, T value) {
		if (size() >= maxSize) {
			Iterator<?> it = entrySet().iterator();
			it.next();
			it.remove();
		}

		return super.put(key, value);
	}

}
