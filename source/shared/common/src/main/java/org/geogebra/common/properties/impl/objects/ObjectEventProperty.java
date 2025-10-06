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
