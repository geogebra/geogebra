package org.geogebra.web.full.gui.menubar.action;

import org.geogebra.web.full.gui.exam.ExamLogAndExitDialog;
import org.geogebra.web.full.gui.menubar.DefaultMenuAction;
import org.geogebra.web.full.main.AppWFull;

/**
 * Shows exam log.
 */
public class ShowExamLogAction extends DefaultMenuAction<Void> {

	@Override
	public void execute(Void item, AppWFull app) {
		new ExamLogAndExitDialog(app, true, null, null).show();
	}
}
