/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.web.full.gui.menubar.action;

import static elemental2.dom.DomGlobal.location;

import java.util.Locale;
import java.util.function.Consumer;

import org.geogebra.common.exam.ExamController;
import org.geogebra.common.exam.ExamType;
import org.geogebra.common.ownership.GlobalScope;
import org.geogebra.gwtutil.SafeExamBrowser;
import org.geogebra.web.full.gui.exam.ExamSEBDialog;
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
		app.closePopups();
		app.getSaveController().showDialogIfNeeded((s) -> showDialog(app, false), false);
	}

	/**
	 * Starts exam directly with {@link ExamStartDialog} in case exam mode is started through
	 * link with examMode parameter
	 * @param app application
	 */
	public void startExamDirectly(AppWFull app) {
		showDialog(app, true);
	}

	private void showDialog(AppWFull app, boolean startExamDirectly) {
		if (app.getLAF().isOfflineExamSupported() || SafeExamBrowser.get() != null
			|| startExamDirectly) {
			showExamDialog(app, (examType) -> startExam(app, examType));
		} else {
			if (ExamStartDialog.mayChooseType(app)) {
				createExamDialog(app, examType -> showSEBDialog(app, examType)).show();
			} else {
				ExamType forcedExamType = app.getForcedExamType();
				showSEBDialog(app, forcedExamType == null ? ExamType.GENERIC : forcedExamType);
			}
		}
	}

	private void showSEBDialog(AppWFull app, ExamType examType) {
		DialogData data = new DialogData("exam_menu_entry", "Cancel", "ExamSEBDialog.LaunchSEB");
		ExamSEBDialog sebDialog = new ExamSEBDialog(app, data);
		String examMode = examType == ExamType.GENERIC ? app.getConfig().getAppCode()
				: examType.name().toLowerCase(Locale.ROOT);
		sebDialog.setOnPositiveAction(() -> location.replace(app.getAppletParameters()
				.getParamExamLaunchURL().replace("$mode", examMode)));
		sebDialog.show();
	}

	/**
	 * Shows the exam welcome message and prepares Exam
	 * (goes fullscreen)
	 */
	private void showExamDialog(AppWFull app, Consumer<ExamType> callback) {
		app.fileNew();
		app.clearSubAppCons();
		app.getLAF().toggleFullscreen(true);
		ExamStartDialog examStartDialog = createExamDialog(app, callback);
		examStartDialog.setOnNegativeAction(() -> {
			examController.cancelExam();
			app.getLAF().toggleFullscreen(false);
		});
		examController.prepareExam();
		app.deleteAutosavedFile();
		examStartDialog.show();
	}

	private ExamStartDialog createExamDialog(AppWFull app, Consumer<ExamType> callback) {
		String cancel = app.isLockedExam() ? null : "Cancel";
		DialogData data = new DialogData("exam_menu_enter", cancel,
				"exam_start_button");
		ExamStartDialog examStartDialog = new ExamStartDialog(app, data);
		examStartDialog.setOnPositiveAction(() ->
				callback.accept(examStartDialog.getSelectedRegion()));
		return examStartDialog;
	}

	private void startExam(AppWFull app, ExamType region) {
		ExamClassicStartDialog.blockEscTab(app);
		app.startExam(region, null);
	}
}
