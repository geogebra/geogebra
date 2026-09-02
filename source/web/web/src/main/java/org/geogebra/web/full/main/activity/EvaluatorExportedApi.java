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

package org.geogebra.web.full.main.activity;

import org.geogebra.web.editor.MathFieldExporter;
import org.geogebra.web.html5.main.ExportedApi;
import org.geogebra.web.html5.main.GgbAPIW;
import org.geogebra.web.html5.main.JsEval;
import org.geogebra.web.html5.main.ScriptManagerW;

import elemental2.core.Global;
import jsinterop.annotations.JsIgnore;
import jsinterop.annotations.JsType;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;

/**
 * API exporter for evaluator app
 */
@JsType
public final class EvaluatorExportedApi implements ExportedApi {

	private final EvaluatorActivity evaluatorActivity;

	private GgbAPIW ggbAPI;
	private ScriptManagerW scriptManager;

	/**
	 * @param evaluatorActivity
	 *            evaluator activity
	 */
	@JsIgnore
	public EvaluatorExportedApi(EvaluatorActivity evaluatorActivity) {
		this.evaluatorActivity = evaluatorActivity;
	}

	@JsIgnore
	@Override
	public void setGgbAPI(GgbAPIW ggbAPI) {
		this.ggbAPI = ggbAPI;
	}

	@JsIgnore
	@Override
	public void setScriptManager(ScriptManagerW scriptManager) {
		this.scriptManager = scriptManager;
	}

	/**
	 * Remove from DOM and remove global JS variables.
	 */
	public void remove() {
		ggbAPI.removeApplet();
		scriptManager.export(null);
	}

	/**
	 * @return state of the editor
	 */
	public Object getEditorState() {
		JsPropertyMap<Object> jsObject = JsPropertyMap.of();
		ScriptManagerW
				.addToJsObject(jsObject, evaluatorActivity.getEditorAPI().getEvaluatorValue());

		return jsObject;
	}

	/**
	 * Evaluate input in LaTeX format.
	 * @param formula input
	 */
	public void evalLaTeX(String formula) {
		evaluatorActivity.getEditorAPI().evalLaTeX(formula);
	}

	/**
	 * Evaluate input in ascii-math (GeoGebra) format
	 * @param formula input
	 */
	public void evalInput(String formula) {
		evaluatorActivity.getEditorAPI().evalInput(formula);
	}

	/**
	 * Export content as image
	 * @param settings e.g. {transparent: false, type: 'png'}
	 * @param callback callback that receives the image
	 */
	public void exportImage(JsPropertyMap<String> settings,
			MathFieldExporter.ImageConsumer callback) {
		String type = Js.isTruthy(settings) ? settings.get("type") : null;
		evaluatorActivity.exportImage(type, Js.isTruthy(settings.get("transparent")), callback);
	}

	/**
	 * @param state new editor state
	 */
	public void setEditorState(Object state) {
		String stateString = JsEval.isJSString(state) ? Js.asString(state)
				: Global.JSON.stringify(state);
		evaluatorActivity.getEditorAPI().setEditorState(stateString);
	}

	/**
	 * Register a listener.
	 * @param JSFunctionName function name of function instance
	 */
	public void registerClientListener(Object JSFunctionName) {
		ggbAPI.registerClientListener(JSFunctionName);
	}

	/**
	 * Open the onscreen keyboard.
	 */
	public void openKeyboard() {
		evaluatorActivity.getEditor().forceKeyboardVisibility(true);
	}

	/**
	 * Close the onscreen keyboard.
	 */
	public void closeKeyboard() {
		evaluatorActivity.getEditor().forceKeyboardVisibility(false);
	}

}
