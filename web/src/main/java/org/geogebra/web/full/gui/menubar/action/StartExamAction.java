package org.geogebra.web.full.gui.menubar.action;

import org.geogebra.common.util.AsyncOperation;
import org.geogebra.web.full.gui.exam.ExamStartDialog;
import org.geogebra.web.full.gui.menubar.DefaultMenuAction;
import org.geogebra.web.full.main.AppWFull;

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
		return new AsyncOperation<Boolean>() {

			@Override
			public void callback(Boolean active) {
				app.fileNew();
				app.getLAF().toggleFullscreen(true);
				ExamStartDialog examStartDialog = new ExamStartDialog(app);
				examStartDialog.show();
				examStartDialog.center();
			}
		};
	}
}
