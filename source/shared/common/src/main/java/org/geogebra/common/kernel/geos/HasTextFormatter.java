package org.geogebra.common.kernel.geos;

import javax.annotation.CheckForNull;

import org.geogebra.common.euclidian.draw.HasTextFormat;

/**
 * Construction element that has a text formatter.
 */
public interface HasTextFormatter {

	/**
	 * @return formatter for this element
	 */
	@CheckForNull HasTextFormat getFormatter();

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
