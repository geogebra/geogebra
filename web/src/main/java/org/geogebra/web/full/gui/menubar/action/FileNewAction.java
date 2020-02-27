package org.geogebra.web.full.gui.menubar.action;

import org.geogebra.common.util.AsyncOperation;
import org.geogebra.web.full.gui.view.algebra.MenuAction;
import org.geogebra.web.full.main.AppWFull;
import org.geogebra.web.html5.main.AppW;

/**
 * Clears construction and initializes a new one
 */
public class FileNewAction extends MenuAction<Void> implements AsyncOperation<Boolean> {
	private AppW app;

	/**
	 * @param app application
	 */
	public FileNewAction(AppW app) {
		super(app.getVendorSettings().getMenuLocalizationKey("New"),
				((AppWFull) app).getActivity().getResourceIconProvider().newFileMenu());
		this.app = app;
	}

	@Override
	public void callback(Boolean active) {
		// ignore active: don't save means we want new construction

		app.setWaitCursor();
		app.fileNew();
		app.setDefaultCursor();
		showPerspectivesPopup();
	}

	private void showPerspectivesPopup() {
		if (app.isUnbundledOrWhiteboard()) {
			return;
		}

		app.showPerspectivesPopup();
	}

	@Override
	public void execute(Void geo, AppWFull appW) {
		appW.getDialogManager().getSaveDialog().showIfNeeded(this);
	}
}
