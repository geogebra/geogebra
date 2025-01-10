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

	private JavaScriptBeautifier() throws ScriptException {
		engine = new ScriptEngineManager().getEngineByName("nashorn");

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
				javascriptBeautifierForJava = new JavaScriptBeautifier();
			}
			return javascriptBeautifierForJava.beautify(unformattedJs);
		} catch (Exception e) {
			Log.error("problem beautifying " + unformattedJs + " "
					+ e.getLocalizedMessage());
			return unformattedJs;
		}
	}
}
