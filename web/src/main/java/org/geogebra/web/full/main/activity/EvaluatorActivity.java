package org.geogebra.web.full.main.activity;

import com.google.gwt.user.client.ui.Label;
import org.geogebra.common.main.AppConfigDefault;
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

		// Set up the Editor and the Keyboard here
		Label label = new Label();
		label.setText("Hello World!");
		appW.getAppletFrame().add(label);
	}
}
