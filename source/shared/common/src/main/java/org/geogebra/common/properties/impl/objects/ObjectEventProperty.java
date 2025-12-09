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

package org.geogebra.common.properties.impl.objects;

import org.geogebra.common.plugin.ScriptType;
import org.geogebra.common.properties.Property;

/**
 * Property API for object-related event scripts. Implementations manage
 * the script text and its {@link org.geogebra.common.plugin.ScriptType},
 * as well as any global JavaScript that should run in response to events.
 *
 */
public interface ObjectEventProperty extends Property {

	/**
	 * gets the script text associated with this object.
	 *
	 * @return the event script text
	 */
	String getScriptText();

	/**
	 * Sets the event script text associated with this object.
	 *
	 * @param text script source to store; {@code null} or empty clears it
	 */
	void setScriptText(String text);

	/**
	 * Returns the type of the current event script.
	 *
	 * @return the {@link ScriptType} describing how the script should be interpreted
	 */
	ScriptType getScriptType();

	/**
	 * Sets the type of the current event script.
	 *
	 * @param value {@link ScriptType} to use
	 */
	void setScriptType(ScriptType value);

	/**
	 * @param jsEnabled whether JS is enabled in the app
	 */
	void setJsEnabled(boolean jsEnabled);

	/**
	 * @return true if JS is enabled in the app; false otherwise;
	 */
	boolean isJsEnabled();
}
