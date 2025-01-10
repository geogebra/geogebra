package org.geogebra.web.full.gui.menubar.action;

import org.geogebra.common.exam.ExamController;
import org.geogebra.common.ownership.GlobalScope;
import org.geogebra.common.util.AsyncOperation;
import org.geogebra.web.full.gui.exam.ExamStartDialog;
import org.geogebra.web.full.gui.exam.classic.ExamClassicStartDialog;
import org.geogebra.web.full.gui.menubar.DefaultMenuAction;
import org.geogebra.web.full.main.AppWFull;
import org.geogebra.web.shared.components.dialog.DialogData;

/**
 * Starts exam.
 */
public class StartExamAction extends DefaultMenuAction<AppWFull> {

	private final ExamController examController = GlobalScope.examController;

	@Override
	public void execute(AppWFull app) {
		app.getSaveController().showDialogIfNeeded(createExamCallback(app), false);
	}

	/**
	 * @return callback that shows the exam welcome message and prepares Exam
	 * (goes fullscreen)
	 */
	private AsyncOperation<Boolean> createExamCallback(AppWFull app) {
		return startExam -> {
			app.fileNew();
			app.clearSubAppCons();
			app.getLAF().toggleFullscreen(true);
			String cancel = app.isLockedExam() ? null : "Cancel";
			DialogData data = new DialogData("exam_menu_enter", cancel,
					"exam_start_button");
			ExamStartDialog examStartDialog = new ExamStartDialog(app, data);
			examStartDialog.setOnNegativeAction(() -> {
				examController.cancelExam();
				app.getLAF().toggleFullscreen(false);
			});
			examStartDialog.setOnPositiveAction(() -> startExam(app, examStartDialog));
			examController.prepareExam();
			examStartDialog.show();
		};
	}

	private void startExam(AppWFull app, ExamStartDialog examStartDialog) {
		ExamClassicStartDialog.blockEscTab(app);
		app.startExam(examStartDialog.getSelectedRegion(), null);
	}
}
