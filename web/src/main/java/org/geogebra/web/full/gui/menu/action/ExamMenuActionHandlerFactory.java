package org.geogebra.web.full.gui.menu.action;

import org.geogebra.common.gui.menu.Action;
import org.geogebra.web.full.gui.menubar.action.ClearAllAction;
import org.geogebra.web.full.gui.menubar.action.ExitExamAction;
import org.geogebra.web.full.gui.menubar.action.ShowExamLogAction;
import org.geogebra.web.full.main.AppWFull;

/**
 * Builds MenuActionHandler for exam mode.
 */
public class ExamMenuActionHandlerFactory implements MenuActionHandlerFactory {

	private AppWFull app;

	public ExamMenuActionHandlerFactory(AppWFull app) {
		this.app = app;
	}

	@Override
	public MenuActionHandler create() {
		DefaultMenuActionHandler actionHandler = new DefaultMenuActionHandler(app);
		actionHandler.setMenuAction(Action.CLEAR_CONSTRUCTION, new ClearAllAction(false));
		actionHandler.setMenuAction(Action.SHOW_EXAM_LOG, new ShowExamLogAction());
		actionHandler.setMenuAction(Action.EXIT_EXAM_MODE, new ExitExamAction());
		return actionHandler;
	}
}
