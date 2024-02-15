package org.geogebra.web.full.gui.util;

import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.shared.components.dialog.DialogData;

public class SaveUnsavedChangesDialog extends SaveFileDialog {

	public SaveUnsavedChangesDialog(AppW app,
			DialogData dialogData, boolean autoHide) {
		super(app, dialogData, autoHide);
	}

	@Override
	protected boolean shouldInputPanelBeVisible() {
		return false;
	}
}
