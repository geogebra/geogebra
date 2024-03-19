package org.geogebra.web.full.gui.menubar.action;

import org.geogebra.common.gui.view.algebra.contextmenu.MenuAction;
import org.geogebra.web.full.gui.util.ExamSaveDialog;
import org.geogebra.web.full.main.AppWFull;

public class SaveExamAction implements MenuAction<AppWFull> {

	@Override
	public boolean isAvailable(AppWFull item) {
		return true;
	}

	@Override
	public void execute(AppWFull app) {
		showExamSaveDialog(app);
	}

	/**
	 * Shows the save dialog during exam.
	 * @param app application
	 */
	public static void showExamSaveDialog(AppWFull app) {
		new ExamSaveDialog(app, null).show();
	}
}
