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
