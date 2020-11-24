package org.geogebra.web.full.gui.menubar.action;

import static org.geogebra.web.full.gui.menubar.action.OpenFileActionMebis.isLoggedOut;

import org.geogebra.web.full.gui.menubar.DefaultMenuAction;
import org.geogebra.web.full.main.AppWFull;

/**
 * Opens save dialog.
 */
public class SaveAction extends DefaultMenuAction<Void> {

	@Override
	public void execute(Void item, AppWFull app) {
		if (isLoggedOut(app)) {
			app.getActivity().markSaveOpen();
		}

		app.getGuiManager().save();
	}
}
