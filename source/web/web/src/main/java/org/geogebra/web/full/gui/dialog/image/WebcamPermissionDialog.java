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

package org.geogebra.web.full.gui.dialog.image;

import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.kernel.ModeSetter;
import org.geogebra.web.html5.gui.BaseWidgetFactory;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.shared.components.dialog.ComponentDialog;
import org.geogebra.web.shared.components.dialog.DialogData;
import org.gwtproject.user.client.ui.Label;

/**
 * dialog to ask user for webcam permission and show error messages
 */
public class WebcamPermissionDialog extends ComponentDialog {

	/**
	 * @param app application
	 * @param data dialog transkeys
	 * @param msgTranskey message displayed in the dialog
	 */
	public WebcamPermissionDialog(AppW app, DialogData data,
			String msgTranskey) {
		super(app, data, false, false);
		addStyleName("mowPermissionDialog");
		String localizedMsg = app.getLocalization().getMenu(msgTranskey);
		if (localizedMsg.length() < 80) {
			addStyleName("narrowDialog");
		}
		buildContent(localizedMsg);
		setOnPositiveAction(() -> app.getGuiManager().setMode(app.isWhiteboardActive()
				? EuclidianConstants.MODE_SELECT_MOW : EuclidianConstants.MODE_MOVE,
				ModeSetter.TOOLBAR));
	}

	private void buildContent(String localizedMsg) {
		Label message = BaseWidgetFactory.INSTANCE.newSecondaryText(localizedMsg);
		addDialogContent(message);
	}
}