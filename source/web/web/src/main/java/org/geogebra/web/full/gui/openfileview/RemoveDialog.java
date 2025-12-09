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

package org.geogebra.web.full.gui.openfileview;

import org.geogebra.web.html5.gui.BaseWidgetFactory;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.shared.components.dialog.ComponentDialog;
import org.geogebra.web.shared.components.dialog.DialogData;
import org.gwtproject.user.client.ui.Label;

/**
 * Confirmation dialog for removing cards.
 */
public class RemoveDialog extends ComponentDialog {
	private MaterialCardI card;

	/**
	 * @param app
	 *            application
	 * @param data
	 *            dialog translation keys
	 * @param card
	 *            card
	 */
	public RemoveDialog(AppW app, DialogData data, MaterialCardI card) {
		super(app, data, true, true);
		this.card = card;
		addStyleName("removeMaterial");
		addStyleName("mebis");
		buildContent();
	}

	private void buildContent() {
		Label confirmDelete = BaseWidgetFactory.INSTANCE.newSecondaryText(
				app.getLocalization().getPlain("ConfirmDeleteA",
						card.getCardTitle()), "message");
		addDialogContent(confirmDelete);
	}
}