package org.geogebra.web.full.main.activity;

import org.geogebra.common.main.AppConfigDefault;
import org.geogebra.web.full.evaluator.EvaluatorEditor;
import org.geogebra.web.html5.main.AppW;

/**
 * Evaluator Activity.
 */
public class EvaluatorActivity extends BaseActivity {

	public EvaluatorActivity() {
		super(new AppConfigDefault());
	}

	@Override
	public void start(AppW appW) {
		super.start(appW);
		EvaluatorEditor editor = new EvaluatorEditor(appW);
	 	appW.getAppletFrame().add(editor);
	}
}
