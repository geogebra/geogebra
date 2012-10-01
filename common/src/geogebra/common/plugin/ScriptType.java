package geogebra.common.plugin;

import geogebra.common.main.App;
import geogebra.common.plugin.script.GgbScript;
import geogebra.common.plugin.script.JsScript;
import geogebra.common.plugin.script.PythonScript;
import geogebra.common.plugin.script.Script;

public enum ScriptType {
	GGBSCRIPT("Script") {
		@Override
		public Script newScript(App app, String text) {
			return new GgbScript(app, text);
		}
	},
	JAVASCRIPT("JavaScript") {
		@Override
		public Script newScript(App app, String text) {
			return new JsScript(app, text);
		}
	},
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
	
	public abstract Script newScript(App app, String text);
	
	public String getName() {
		return name;
	}
	
}
