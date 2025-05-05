package com.himamis.retex.editor.share.controller;

import com.himamis.retex.renderer.share.serialize.SerializationAdapter;

/**
 * Expression serializer for screen reader.
 */
public interface ExpressionReader {

	/**
	 * @param key translation key
	 * @param parameters parameters
	 * @return localized string
	 */
	String localize(String key, String... parameters);

	/**
	 * @param base base
	 * @param exponent exponent
	 * @return localized description of power expression
	 */
	String power(String base, String exponent);

	/**
	 * Log text to the logger.
	 * @param label log entry
	 */
	void debug(String label);

	/**
	 * @return serialization adapter
	 */
	SerializationAdapter getAdapter();
}
