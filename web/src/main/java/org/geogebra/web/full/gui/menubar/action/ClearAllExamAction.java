package org.geogebra.web.full.gui.menubar.action;

import org.geogebra.common.gui.view.algebra.contextmenu.MenuAction;
import org.geogebra.web.full.gui.util.ExamSaveDialog;
import org.geogebra.web.full.main.AppWFull;

public class ClearAllExamAction implements MenuAction<AppWFull> {

	@Override
	public boolean isAvailable(AppWFull item) {
		return true;
	}

	@Override
	public void execute(AppWFull app) {
		if (app.isSaved()) {
			app.fileNew();
		} else {
			new ExamSaveDialog(app, app::fileNew).show();
		}
	}
}

