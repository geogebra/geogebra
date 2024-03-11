package org.geogebra.web.full.gui.menubar.action;

import org.geogebra.web.full.gui.exam.ExamLogAndExitDialog;
import org.geogebra.web.full.gui.menubar.DefaultMenuAction;
import org.geogebra.web.full.main.AppWFull;

/**
 * Shows exam log.
 */
public class ShowExamLogAction extends DefaultMenuAction<AppWFull> {

	@Override
	public void execute(AppWFull app) {
		new ExamLogAndExitDialog(app, true, null).show();
	}
}
