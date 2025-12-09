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

import org.geogebra.common.main.settings.config.AppConfigEvaluator;
import org.geogebra.common.plugin.evaluator.EvaluatorAPI;
import org.geogebra.web.editor.MathFieldExporter;
import org.geogebra.web.full.evaluator.EvaluatorEditor;
import org.geogebra.web.full.gui.components.MathFieldEditor;
import org.geogebra.web.html5.gui.GeoGebraFrameW;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.main.ExportedApi;
import org.geogebra.web.html5.util.AppletParameters;

/**
 * Evaluator Activity.
 */
public class EvaluatorActivity extends BaseActivity {

	private EvaluatorEditor editor;

	/**
	 * Activity for evaluator app
	 */
	public EvaluatorActivity() {
		super(new AppConfigEvaluator());
	}

	@Override
	public void start(AppW appW) {
		super.start(appW);
		editor = new EvaluatorEditor(appW);
		AppletParameters appletParameters = appW.getAppletParameters();
		if (!appletParameters.hasAttribute("showKeyboardOnFocus")) {
			appletParameters.setAttribute("showKeyboardOnFocus", "true");
		}
		GeoGebraFrameW frame = appW.getAppletFrame();
		frame.clear();
		frame.add(editor);

		if (!appletParameters.preventFocus()) {
			editor.requestFocus();
		}
	}

	@Override
	public ExportedApi getExportedApi() {
		// not started yet -> pass the whole activity to geteditor later
		return new EvaluatorExportedApi(this);
	}

	/**
	 * @return editor API
	 */
	public EvaluatorAPI getEditorAPI() {
		return editor.getAPI();
	}

	/**
	 * @param type image type
	 * @param transparent whether to use transparent background
	 * @param callback callback, receives the image
	 */
	public void exportImage(String type, boolean transparent,
			MathFieldExporter.ImageConsumer callback) {
		editor.exportImage(type, transparent, callback);
	}

	public MathFieldEditor getEditor() {
		return editor.getMathFieldEditor();
	}
}
