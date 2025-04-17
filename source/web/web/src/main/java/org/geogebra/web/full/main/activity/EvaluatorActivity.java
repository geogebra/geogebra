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
