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

package org.geogebra.common.plugin.script;

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.App;
import org.geogebra.common.plugin.Event;
import org.geogebra.common.plugin.EventType;
import org.geogebra.common.plugin.ScriptError;
import org.geogebra.common.plugin.ScriptType;

/**
 * @author arno Parent class for script objects. There is one script class for
 *         each type of script. They must be added to the enum ScriptType.
 */
public abstract class Script {

	/**
	 * Application the script belongs to
	 */
	protected final App app;
	/**
	 * source code for the script
	 */
	protected String text;

	/**
	 * @param app
	 *            the script's application
	 * @param text
	 *            the script's source code
	 */
	public Script(App app, String text) {
		super();
		this.app = app;
		this.text = text;
	}

	/**
	 * Get the script's source code
	 * 
	 * @return the source code as a string
	 */
	public String getText() {
		return text;
	}

	/**
	 * Get the script's internal text (which could be different from the
	 * localized text)
	 * 
	 * @return the internal text
	 */
	public String getInternalText() {
		return text;
	}

	/**
	 * Perform actions necessary (if any) to bind the script to a GeoElement via
	 * a given EventType
	 * 
	 * @param geo
	 *            the geo
	 * @param evtType
	 *            the event type
	 */
	public void bind(GeoElement geo, EventType evtType) {
		// Do nothing by default here
	}

	/**
	 * Perform actions necessary (if any) to unbind the script from a GeoElement
	 * via a given EventType
	 * 
	 * @param geo
	 *            the geo
	 * @param evtType
	 *            the event type
	 */
	public void unbind(GeoElement geo, EventType evtType) {
		// Do nothing by default here
	}

	/**
	 * Run the script
	 * 
	 * @param evt
	 *            the event that triggered the script
	 * @return success
	 * @throws ScriptError
	 *             error thrown if the script cannot be run
	 */
	public abstract boolean run(Event evt) throws ScriptError;

	/**
	 * Get the script's type
	 * 
	 * @return the script's type
	 */
	public abstract ScriptType getType();

	/**
	 * Get the language name of the script (convenience function)
	 * 
	 * @return the language name
	 */
	public String getLanguageName() {
		return this.getType().getName();
	}

	/**
	 * @return the XML attribute name for serialization to file
	 */
	public String getXMLName() {
		return this.getType().getXMLName();
	}

	/**
	 * @return a new copy of this object
	 */
	public abstract Script copy();

	/**
	 * The text of this script is modified by changing every whole word oldLabel
	 * to newLabel.
	 * 
	 * @param oldLabel
	 *            old label
	 * @param newLabel
	 *            new label
	 * 
	 * @return whether any renaming happened
	 */
	public abstract boolean renameGeo(String oldLabel, String newLabel);
}
