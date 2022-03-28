package org.geogebra.web.full.gui.menubar.action;

import org.geogebra.web.full.gui.menubar.MenuAction;
import org.geogebra.web.full.gui.util.ExamSaveDialog;
import org.geogebra.web.full.main.AppWFull;

public class SaveExamAction implements MenuAction<Void> {

	@Override
	public boolean isAvailable(Void item) {
		return true;
	}

	@Override
	public void execute(Void item, AppWFull app) {
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
