package org.geogebra.web.full.main.activity;

import org.geogebra.common.util.ExternalAccess;
import org.geogebra.web.html5.main.ApiExporter;
import org.geogebra.web.html5.main.GgbAPIW;
import org.geogebra.web.html5.main.ScriptManagerW;

import com.google.gwt.core.client.JavaScriptObject;

/**
 * API exporter for evaluator app
 */
public class EvaluatorApiExporter extends ApiExporter {

	private EvaluatorActivity evaluatorActivity;

	/**
	 * @param evaluatorActivity
	 *            evaluator activity
	 */
	public EvaluatorApiExporter(EvaluatorActivity evaluatorActivity) {
		this.evaluatorActivity = evaluatorActivity;
	}

	@Override
	protected void addFunctions(JavaScriptObject api, GgbAPIW ggbAPI) {
		addEditorState(api);
	}

	@ExternalAccess
	private JavaScriptObject getEditorState() {
		JavaScriptObject jsObject = JavaScriptObject.createObject();
		ScriptManagerW
				.addToJsObject(jsObject, evaluatorActivity.getEditorAPI().getEvaluatorValue());

		return jsObject;
	}

	@ExternalAccess
	private void setEditorState(String state) {
		evaluatorActivity.getEditorAPI().setEditorState(state);
	}

	@ExternalAccess
	private void evalLaTeX(String formula) {
		evaluatorActivity.getEditorAPI().evalLaTeX(formula);
	}

	@ExternalAccess
	private Object exportImage(String type) {
		return evaluatorActivity.exportImage(type);
	}

	private native void addEditorState(JavaScriptObject api) /*-{
		var that = this;
		api.getEditorState = function() {
			return that.@org.geogebra.web.full.main.activity.EvaluatorApiExporter::getEditorState()();
		};

		api.evalLaTeX = function(formula) {
			return that.@org.geogebra.web.full.main.activity.EvaluatorApiExporter::evalLaTeX(Ljava/lang/String;)(formula);
		};

		api.exportImage = function(settings) {
			return that.@org.geogebra.web.full.main.activity.EvaluatorApiExporter::exportImage(Ljava/lang/String;)(settings && settings.type);
		};

		api.setEditorState = function(state, label) {
			var stateString = typeof state == "string" ? state : JSON
					.stringify(state);
			that.@org.geogebra.web.full.main.activity.EvaluatorApiExporter::setEditorState(Ljava/lang/String;)(stateString);
		};
	}-*/;

	@Override
	protected void addListenerFunctions(JavaScriptObject api, GgbAPIW ggbAPI,
			JavaScriptObject getId) {
		addClientListener(api, ggbAPI, getId);
	}

}
