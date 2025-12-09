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

import org.geogebra.common.util.AsyncOperation;
import org.geogebra.web.full.gui.menubar.DefaultMenuAction;
import org.geogebra.web.full.main.AppWFull;
import org.geogebra.web.html5.gui.util.BrowserStorage;
import org.geogebra.web.html5.main.AppW;

/**
 * Clears construction.
 */
public class ClearAllAction extends DefaultMenuAction<AppWFull> implements AsyncOperation<Boolean> {

	private boolean askForSave;
	private AppW app;

	/**
	 * @param askForSave whether asks for save
	 */
	public ClearAllAction(boolean askForSave) {
		this.askForSave = askForSave;
	}

	@Override
	public void execute(AppWFull app) {
		this.app = app;
		if (askForSave) {
			BrowserStorage.SESSION.setItem("saveAction", "clearAll");
			app.getSaveController().showDialogIfNeeded(this, false);
		} else {
			callback(true);
		}
	}

	@Override
	public void callback(Boolean obj) {
		app.getShareController().setAssign(false);
		app.tryLoadTemplatesOnFileNew();
	}
}
