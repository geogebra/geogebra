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

package org.geogebra.common.kernel.geos;

import org.geogebra.common.euclidian.draw.HasTextFormat;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.jspecify.annotations.Nullable;

/**
 * Construction element that has a text formatter.
 */
public interface HasTextFormatter extends GeoElementND {

	/**
	 * @return formatter for this element
	 */
	@Nullable HasTextFormat getFormatter();

	/**
	 * @param key formatting option name
	 * @param fallback fallback when not set / indeterminate
	 * @param <T> option type
	 * @return formatting option value or fallback
	 */
	default <T> T getFormat(String key, T fallback) {
		HasTextFormat formatter = getFormatter();
		return formatter == null ? fallback : formatter.getFormat(key, fallback);
	}

	/**
	 * @param key
	 *            formatting option
	 * @param val
	 *            value (String, int or bool, depending on key)
	 */
	default void format(String key, Object val) {
		HasTextFormat formatter = getFormatter();
		if (formatter != null) {
			formatter.format(key, val);
		}
	}
}
