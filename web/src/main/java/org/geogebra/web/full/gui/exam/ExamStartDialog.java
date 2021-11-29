package org.geogebra.web.full.gui.exam;

import org.geogebra.web.full.main.AppWFull;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.shared.components.dialog.ComponentDialog;
import org.geogebra.web.shared.components.dialog.DialogData;

import com.google.gwt.user.client.ui.Label;

/**
 * Dialog to enter in graphing or cas calc exam mode
 */
public class ExamStartDialog extends ComponentDialog {

	/**
	 * @param app application
	 * @param data dialog transkeys
	 */
	public ExamStartDialog(AppWFull app, DialogData data) {
		super(app, data, false, true);
		addStyleName("examStartDialog");
		buildContent();
	}

	private void buildContent() {
		Label startText = new Label(app.getLocalization().getMenu("exam_start_dialog_text"));
		startText.addStyleName("examStartText");
		addDialogContent(startText);
	}

	@Override
	public void onEscape() {
		if (!((AppW) app).getAppletParameters().getParamLockExam()) {
			hide();
		}
	}
}