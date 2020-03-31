package org.geogebra.common.euclidian.draw;

public interface HasFormat {
	/**
	 * @param key
	 *            formatting option
	 * @param val
	 *            value (String, int or bool, depending on key)
	 */
	void format(String key, Object val);
}
