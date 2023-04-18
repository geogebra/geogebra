package org.geogebra.web.full.main.activity;

import org.geogebra.common.kernel.stepbystep.StepSolverImpl;
import org.geogebra.common.main.settings.config.AppConfigEvaluator;
import org.geogebra.common.plugin.evaluator.EvaluatorAPI;
import org.geogebra.common.util.debug.Log;
import org.geogebra.web.editor.MathFieldExporter;
import org.geogebra.web.full.evaluator.EvaluatorEditor;
import org.geogebra.web.full.gui.components.MathFieldEditor;
import org.geogebra.web.html5.gui.GeoGebraFrameW;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.main.ExportedApi;
import org.geogebra.web.html5.util.AppletParameters;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;

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
		GWT.runAsync(StepSolverImpl.class, new RunAsyncCallback() {
			@Override
			public void onFailure(Throwable reason) {
				Log.error("Loading failed for steps commands");
			}

			@Override
			public void onSuccess() {
				getEditorAPI().setSolver(new StepSolverImpl());
			}
		});

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

	public void exportImage(String type, boolean transparent,
			MathFieldExporter.ImageConsumer callback) {
		editor.exportImage(type, transparent, callback);
	}

	public MathFieldEditor getEditor() {
		return editor.getMathFieldEditor();
	}
}
