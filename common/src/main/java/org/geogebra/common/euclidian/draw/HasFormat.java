package org.geogebra.common.euclidian.draw;

public interface HasFormat {
	/**
	 * @param key
	 *            formatting option
	 * @param val
	 *            value (String, int or bool, depending on key)
	 */
	void format(String key, Object val);

	/**
	 * @param key formatting option name
	 * @param fallback fallback when not set / indeterminate
	 * @param <T> option type
	 * @return formatting option value or fallback
	 */
	<T> T getFormat(String key, T fallback);
}
