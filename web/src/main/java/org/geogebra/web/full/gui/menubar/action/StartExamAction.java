package org.geogebra.web.full.gui.menubar.action;

import org.geogebra.common.util.AsyncOperation;
import org.geogebra.web.full.gui.exam.ExamStartDialog;
import org.geogebra.web.full.gui.exam.classic.ExamClassicStartDialog;
import org.geogebra.web.full.gui.menubar.DefaultMenuAction;
import org.geogebra.web.full.main.AppWFull;
import org.geogebra.web.shared.components.dialog.DialogData;

/**
 * Starts exam.
 */
public class StartExamAction extends DefaultMenuAction<Void> {

	private AppWFull app;

	/**
	 * @param app app
	 */
	public StartExamAction(AppWFull app) {
		this.app = app;
	}

	@Override
	public void execute(Void item, AppWFull app) {
		app.getSaveController().showDialogIfNeeded(createExamCallback(), false);
	}

	/**
	 * @return callback that shows the exam welcome message and prepares Exam
	 * (goes fullscreen)
	 */
	private AsyncOperation<Boolean> createExamCallback() {
		return startExam -> {
			app.fileNew();
			app.clearSubAppCons();
			app.getLAF().toggleFullscreen(true);
			String cancel = app.getAppletParameters().getParamLockExam() ? null : "Cancel";
			DialogData data = new DialogData("exam_menu_enter", cancel,
					"exam_start_button");
			ExamStartDialog examStartDialog = new ExamStartDialog(app, data);
			examStartDialog.setOnNegativeAction(() -> app.getLAF().toggleFullscreen(false));
			examStartDialog.setOnPositiveAction(() -> {
				ExamClassicStartDialog.blockEscTab(app);
				app.setNewExam(examStartDialog.getSelectedRegion());
				app.startExam();
			});
			examStartDialog.show();
		};
	}
}
