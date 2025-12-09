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

package org.geogebra.web.full.gui.dialog;

import org.geogebra.web.full.main.AppWFull;
import org.geogebra.web.html5.gui.BaseWidgetFactory;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.shared.components.dialog.ComponentDialog;
import org.geogebra.web.shared.components.dialog.DialogData;
import org.gwtproject.user.client.ui.Label;

/**
 *  A dialog to ask the user to recover the autoSaved file.
 */
public class RecoverAutoSavedDialog extends ComponentDialog {

	/**
	 * only used from {@link AppWFull} with menu
	 * @param app {@link AppW}
	 * @param data dialog transkeys
	 */
	public RecoverAutoSavedDialog(AppWFull app, DialogData data) {
		super(app, data, false, true);
		addStyleName("RecoverAutoSavedDialog");
		buildContent();
	}

	private void buildContent() {
		Label infoText = BaseWidgetFactory.INSTANCE.newSecondaryText(
				app.getLocalization().getMenu("UnsavedChangesFound"), "infoText");
		addDialogContent(infoText);
	}
}