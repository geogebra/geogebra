package geogebra.common.plugin;

import geogebra.common.main.App;
import geogebra.common.plugin.script.GgbScript;
import geogebra.common.plugin.script.JsScript;
import geogebra.common.plugin.script.PythonScript;
import geogebra.common.plugin.script.Script;

/**
 * @author arno
 * Script classes should get registered here.
 */
public enum ScriptType {
	/**
	 * GgbScript
	 */
	GGBSCRIPT("Script") {
		@Override
		public Script newScript(App app, String text) {
			return new GgbScript(app, text);
		}
	},
	/**
	 * JsScript
	 */
	JAVASCRIPT("JavaScript") {
		@Override
		public Script newScript(App app, String text) {
			return new JsScript(app, text);
		}
	},
	/**
	 * PythonScript
	 */
	PYTHON("Python") {
		@Override
		public Script newScript(App app, String text) {
			return new PythonScript(app, text);
		}
	};

	private String name;

	ScriptType(String name) {
		this.name = name;
	}
	
	/**
	 * Create a new script of this type
	 * @param app the application where the script lives
	 * @param text the source code of the script
	 * @return a new Script object
	 */
	public abstract Script newScript(App app, String text);
	
	/**
	 * Get the script type's name
	 * @return the name of the script type
	 */
	public String getName() {
		return name;
	}
	
}
