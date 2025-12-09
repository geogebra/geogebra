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

import org.geogebra.web.full.gui.openfileview.MaterialCard;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.shared.components.dialog.DialogData;

public class MaterialRenameDialog extends CardRenameDialog {

	private final MaterialCard card;

	/**
	 * @param app app
	 * @param data dialog transkeys
	 */
	public MaterialRenameDialog(AppW app,
			DialogData data, MaterialCard card) {
		super(app, data);
		this.card = card;
		setText(getCardTitle());
	}

	@Override
	protected void renameCard(String text) {
		card.rename(text);
	}

	@Override
	protected String getCardTitle() {
		return card.getCardTitle();
	}
}
