package org.geogebra.web.full.gui.exam;

import org.geogebra.web.html5.gui.BaseWidgetFactory;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.shared.components.dialog.ComponentDialog;
import org.geogebra.web.shared.components.dialog.DialogData;
import org.gwtproject.user.client.ui.Label;

/**
 * @author csilla
 * 
 *         exit exam confirmation dialog
 */
public class ExamExitConfirmDialog extends ComponentDialog {

	/**
	 * @param app
	 *            application
	 * @param data dialog transkeys
	 */
	public ExamExitConfirmDialog(AppW app, DialogData data) {
		super(app, data, false, true);
		addStyleName("examExitConfDialog");
		buildContent();
	}

	private void buildContent() {
		Label confirmText = BaseWidgetFactory.INSTANCE.newSecondaryText(
				app.getLocalization().getMenu("exam_exit_confirmation"), "exitConfText");
		addDialogContent(confirmText);
	}
}