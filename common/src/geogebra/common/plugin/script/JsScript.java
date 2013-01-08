package geogebra.common.plugin.script;

import geogebra.common.kernel.StringTemplate;
import geogebra.common.main.App;
import geogebra.common.plugin.Event;
import geogebra.common.plugin.EventType;
import geogebra.common.plugin.ScriptError;
import geogebra.common.plugin.ScriptType;

/**
 * @author arno
 * Class for JavaScript scripts
 */
public class JsScript extends Script {

	/**
	 * @param app the script's application
	 * @param text the script's source code
	 */
	public JsScript(App app, String text) {
		super(app, text);
	}

	@Override
	public void run(Event evt) throws ScriptError {
		String label = evt.target.getLabel(StringTemplate.defaultTemplate);
		boolean update = evt.type == EventType.UPDATE;
		Object[] args;
		try {
			if (app.isApplet() && app.useBrowserForJavaScript() && !update) {
				if (evt.argument == null) {
					args = new Object[] {};
				} else {
					args = new Object[] { evt.argument };
				}
				app.callAppletJavaScript("ggb" + label, args);
			} else if (app.isHTML5Applet() && app.useBrowserForJavaScript()) {
				String functionPrefix = update ? "ggbUpdate" : "ggb";
				if (evt.argument == null) {
					args = new Object[] {};
				} else {
					args = new Object[] { evt.argument };
				}
				app.callAppletJavaScript(functionPrefix + label, args);
			} else {
				app.evalJavaScript(app, text, evt.argument);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new ScriptError(
					app.getPlain(update ? "OnUpdate" : "OnClick") + " "
					+ label + ":\n" + app.getPlain("ErrorInJavaScript")
					+ "\n" + e.getLocalizedMessage()
			);
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

}
