package org.geogebra.web.full.gui.menubar.action;

import org.geogebra.common.util.AsyncOperation;
import org.geogebra.web.full.gui.exam.ExamStartDialog;
import org.geogebra.web.full.gui.menubar.DefaultMenuAction;
import org.geogebra.web.full.main.AppWFull;
import org.geogebra.web.shared.components.DialogData;

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
		app.getSaveController().showDialogIfNeeded(createExamCallback());
	}

	/**
	 * @return callback that shows the exam welcome message and prepares Exam
	 * (goes fullscreen)
	 */
	private AsyncOperation<Boolean> createExamCallback() {
		return startExam -> {
			app.fileNew();
			app.getLAF().toggleFullscreen(true);
			DialogData data = new DialogData("exam_menu_enter", "Cancel",
					"exam_start_button");
			ExamStartDialog examStartDialog = new ExamStartDialog(app, data);
			examStartDialog.setOnNegativeAction(() -> {
				app.getLAF().toggleFullscreen(false);
			});
			examStartDialog.setOnPositiveAction(() -> {
				app.setNewExam();
				app.startExam();
			});
			examStartDialog.show();
		};
	}
}
