package org.geogebra.web.full.main.activity;

import org.geogebra.common.main.settings.AppConfigEvaluator;
import org.geogebra.web.full.evaluator.EvaluatorEditor;
import org.geogebra.web.html5.gui.GeoGebraFrameW;
import org.geogebra.web.html5.main.AppW;

/**
 * Evaluator Activity.
 */
public class EvaluatorActivity extends BaseActivity {

	public EvaluatorActivity() {
		super(new AppConfigEvaluator());
	}

	@Override
	public void start(AppW appW) {
		super.start(appW);
		EvaluatorEditor editor = new EvaluatorEditor(appW);
		GeoGebraFrameW frame = appW.getAppletFrame();
		frame.clear();
		frame.add(editor);
		editor.requestFocus();
	}
}
