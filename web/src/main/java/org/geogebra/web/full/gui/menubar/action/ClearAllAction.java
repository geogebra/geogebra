package org.geogebra.web.full.gui.menubar.action;

import org.geogebra.common.util.AsyncOperation;
import org.geogebra.web.full.gui.menubar.DefaultMenuAction;
import org.geogebra.web.full.main.AppWFull;
import org.geogebra.web.html5.main.AppW;

/**
 * Clears construction.
 */
public class ClearAllAction extends DefaultMenuAction<Void> implements AsyncOperation<Boolean> {

	private boolean askForSave;
	private AppW app;

	/**
	 * @param askForSave whether asks for save
	 */
	public ClearAllAction(boolean askForSave) {
		this.askForSave = askForSave;
	}

	@Override
	public void execute(Void item, AppWFull app) {
		this.app = app;
		if (askForSave) {
			app.getSaveController().showDialogIfNeeded(this);
		} else {
			callback(true);
		}
	}

	@Override
	public void callback(Boolean obj) {
		// ignore obj: don't save means we want new construction
		app.setWaitCursor();
		app.fileNew();
		app.setDefaultCursor();

		if (!app.isUnbundledOrWhiteboard()) {
			app.showPerspectivesPopup();
		}
		if (app.getPageController() != null) {
			app.getPageController().resetPageControl();
		}
	}
}
