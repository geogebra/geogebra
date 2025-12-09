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

package org.geogebra.web.full.gui.exam;

import org.geogebra.web.html5.gui.BaseWidgetFactory;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.shared.components.dialog.ComponentDialog;
import org.geogebra.web.shared.components.dialog.DialogData;
import org.gwtproject.user.client.ui.Label;

/**
 * @author csilla
 * 
 *         exit exam confirmation dialog
 */
public class ExamExitConfirmDialog extends ComponentDialog {

	/**
	 * @param app
	 *            application
	 * @param data dialog transkeys
	 */
	public ExamExitConfirmDialog(AppW app, DialogData data) {
		super(app, data, false, true);
		addStyleName("examExitConfDialog");
		buildContent();
	}

	private void buildContent() {
		Label confirmText = BaseWidgetFactory.INSTANCE.newSecondaryText(
				app.getLocalization().getMenu("exam_exit_confirmation"), "exitConfText");
		addDialogContent(confirmText);
	}
}