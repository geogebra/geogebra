package org.geogebra.web.full.gui.menubar.action;

import static elemental2.dom.DomGlobal.location;

import java.util.function.Consumer;

import org.geogebra.common.exam.ExamController;
import org.geogebra.common.exam.ExamType;
import org.geogebra.common.ownership.GlobalScope;
import org.geogebra.gwtutil.SafeExamBrowser;
import org.geogebra.web.full.gui.exam.ExamStartDialog;
import org.geogebra.web.full.gui.exam.classic.ExamClassicStartDialog;
import org.geogebra.web.full.gui.menubar.DefaultMenuAction;
import org.geogebra.web.full.main.AppWFull;
import org.geogebra.web.shared.components.dialog.ComponentDialog;
import org.geogebra.web.shared.components.dialog.DialogData;

/**
 * Starts exam.
 */
public class StartExamAction extends DefaultMenuAction<AppWFull> {

	private final ExamController examController = GlobalScope.examController;

	@Override
	public void execute(AppWFull app) {
		app.closePopups();
		app.getSaveController().showDialogIfNeeded((s) -> showDialog(app), false);
	}

	private void showDialog(AppWFull app) {
		if (app.getLAF().isOfflineExamSupported() || SafeExamBrowser.get() != null) {
			showExamDialog(app, (examType) -> startExam(app, examType));
		} else {
			if (ExamStartDialog.mayChooseType(app)) {
				showExamDialog(app, examType -> showSEBDialog(app, examType));
			} else {
				showSEBDialog(app, ExamType.GENERIC);
			}
		}
	}

	private void showSEBDialog(AppWFull app, ExamType examType) {
		ComponentDialog runSEB = new ComponentDialog(app,
				new DialogData("exam_menu_entry", "Cancel", "LaunchSEB"), true, true);
		// dialog content out of scope (APPS-6525)
		String examMode = examType == ExamType.GENERIC ? app.getConfig().getAppCode()
				: examType.name().toLowerCase();
		runSEB.setOnPositiveAction(() -> location.replace(app.getAppletParameters()
				.getParamExamLaunchURL().replace("$mode", examMode)));
		runSEB.show();
	}

	/**
	 * Shows the exam welcome message and prepares Exam
	 * (goes fullscreen)
	 */
	private void showExamDialog(AppWFull app, Consumer<ExamType> callback) {
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
		examStartDialog.setOnPositiveAction(() ->
				callback.accept(examStartDialog.getSelectedRegion()));
		examController.prepareExam();
		examStartDialog.show();
	}

	private void startExam(AppWFull app, ExamType region) {
		ExamClassicStartDialog.blockEscTab(app);
		app.startExam(region, null);
	}
}
