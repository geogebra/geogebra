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
// This code has been written initially for Scilab (http://www.scilab.org/).

package org.geogebra.desktop.gui.editor;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.geogebra.common.util.debug.Log;

/**
 * format JavaScript code nicely. Uses beautify.js code (run using Nashorn)
 */
public class JavaScriptBeautifier {

	// name of beautifier function
	private static final String JS_METHOD_NAME = "js_beautify";

	private static JavaScriptBeautifier javascriptBeautifierForJava;

	private final ScriptEngine engine;

	private JavaScriptBeautifier(ScriptEngine engine) throws ScriptException {
		this.engine = engine;
		// this is needed to make self invoking function modules work
		// otherwise you won't be able to invoke your function
		engine.eval("var global = this;");

		engine.eval(BeautifyJS.BEAUTIFY_JS);
	}

	private String beautify(String javascriptCode)
			throws ScriptException, NoSuchMethodException {
		return (String) ((Invocable) engine)
				.invokeFunction(JS_METHOD_NAME, javascriptCode);
	}

	/**
	 * @param unformattedJs
	 *            JavaScript code
	 * @return unformattedJs formatted nicely
	 */
	public static String format(String unformattedJs) {
		try {
			if (javascriptBeautifierForJava == null) {
				ScriptEngine engine = new ScriptEngineManager().getEngineByName("rhino");
				if (engine == null) {
					Log.error("Rhino not loaded");
					return unformattedJs;
				}
				javascriptBeautifierForJava = new JavaScriptBeautifier(engine);
			}
			return javascriptBeautifierForJava.beautify(unformattedJs);
		} catch (Exception e) {
			Log.error("problem beautifying " + unformattedJs + " "
					+ e.getLocalizedMessage());
			return unformattedJs;
		}
	}
}
