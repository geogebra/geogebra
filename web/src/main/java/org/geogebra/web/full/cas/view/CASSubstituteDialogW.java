package org.geogebra.web.full.cas.view;

import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.shared.components.dialog.ComponentDialog;
import org.geogebra.web.shared.components.dialog.DialogData;

public class CASSubstituteDialogW extends ComponentDialog {
	/**
	/**
	 * base dialog constructor
	 * @param app - see {@link AppW}
	 * @param dialogData - contains trans keys for title and buttons
	 */
	public CASSubstituteDialogW(AppW app,
			DialogData dialogData) {
		super(app, dialogData, false, true);
	}
}
