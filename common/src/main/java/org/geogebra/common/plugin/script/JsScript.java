package org.geogebra.common.plugin.script;

import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.main.App;
import org.geogebra.common.plugin.Event;
import org.geogebra.common.plugin.EventType;
import org.geogebra.common.plugin.ScriptError;
import org.geogebra.common.plugin.ScriptType;

/**
 * @author arno Class for JavaScript scripts
 */
public class JsScript extends Script {

	/**
	 * @param app
	 *            the script's application
	 * @param text
	 *            the script's source code
	 */
	public JsScript(App app, String text) {
		super(app, text);
	}

	@Override
	public boolean run(Event evt) throws ScriptError {
		String label = evt.target.getLabel(StringTemplate.defaultTemplate);
		boolean update = evt.type == EventType.UPDATE;
		try {
			if (app.isApplet() && app.useBrowserForJavaScript() && !update) {
			    app.callAppletJavaScript("ggb" + label, evt.argument);
			} else if (app.isHTML5Applet() && app.useBrowserForJavaScript()) {
				String functionPrefix = update ? "ggbUpdate" : "ggb";
				app.callAppletJavaScript(functionPrefix + label, evt.argument);
			} else {
				app.evalJavaScript(app, text, evt.argument);
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			throw new ScriptError(app.getLocalization()
					.getMenu(update ? "OnUpdate" : "OnClick") + " " + label
					+ ":\n"
					+ app.getLocalization().getMenuDefault("ErrorInJavaScript",
							"Error in JavaScript")
					+ "\n" + e.getLocalizedMessage());
		}
	}

	@Override
	public ScriptType getType() {
		return ScriptType.JAVASCRIPT;
	}

	@Override
	public Script copy() {
		return new JsScript(app, text);
	}

	/**
	 * The text of this script is modified by changing every whole word oldLabel
	 * to newLabel.
	 * 
	 * @return whether any renaming happened
	 */
	@Override
	public boolean renameGeo(String oldLabel, String newLabel) {
		// TODO: this method is hard to write,
		// as JavaScript might contain many kinds of strings
		// which may clash with oldLabel...
		return false;
	}

}
