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

import org.geogebra.web.full.gui.pagecontrolpanel.PageListController;
import org.geogebra.web.full.gui.pagecontrolpanel.PagePreviewCard;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.main.PageListControllerInterface;
import org.geogebra.web.shared.components.dialog.DialogData;

public class PreviewCardRenameDialog extends CardRenameDialog {
	private final PageListControllerInterface pageController;
	private final PagePreviewCard card;

	/**
	 * @param app app
	 * @param data dialog transkeys
	 * @param card to rename.
	 */
	public PreviewCardRenameDialog(AppW app, DialogData data, PagePreviewCard card) {
		super(app, data);
		pageController = app.getPageController();
		this.card = card;
		setText(getCardTitle());
	}

	@Override
	protected boolean isTextLengthInvalid() {
		// empty input is valid.
		return false;
	}

	@Override
	protected String getCardTitle() {
		return card.getCardTitle();
	}

	@Override
	protected void renameCard(String text) {
		((PageListController) pageController).rename(card, text);
	}
}
