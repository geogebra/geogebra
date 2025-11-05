/*
GeoGebra - Dynamic Mathematics for Schools
Copyright (c) GeoGebra GmbH, Altenbergerstr 69, 4040 Linz, Austria
https://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it
under the terms of the GNU General Public License as published by
the Free Software Foundation.
*/

package org.geogebra.editor.share.controller;

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
