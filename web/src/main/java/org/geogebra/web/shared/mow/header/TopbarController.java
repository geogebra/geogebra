package org.geogebra.web.shared.mow.header;

import org.geogebra.web.full.main.AppWFull;
import org.geogebra.web.html5.main.AppW;

public class TopbarController {
	private final AppW appW;

	/**
	 * Controller
	 * @param appW - application
	 */
	public TopbarController(AppW appW) {
		this.appW = appW;
	}

	/**
	 * on menu pressed
	 */
	public void onMenuToggle() {
		appW.hideKeyboard();
		appW.toggleMenu();
	}

	/**
	 * on undo pressed
	 */
	public void onUndo() {
		appW.getGuiManager().undo();
		(((AppWFull) appW).getAppletFrame()).deselectDragBtn();
	}

	/**
	 * on redo pressed
	 */
	public void onRedo() {
		appW.getGuiManager().redo();
		(((AppWFull) appW).getAppletFrame()).deselectDragBtn();
	}
}
