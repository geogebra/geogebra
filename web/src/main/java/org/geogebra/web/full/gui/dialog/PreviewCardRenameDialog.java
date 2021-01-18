package org.geogebra.web.full.gui.dialog;

import org.geogebra.web.full.gui.pagecontrolpanel.PageListController;
import org.geogebra.web.full.gui.pagecontrolpanel.PagePreviewCard;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.main.PageListControllerInterface;
import org.geogebra.web.shared.components.DialogData;

public class PreviewCardRenameDialog extends CardRenameDialog {

	private final PageListControllerInterface pageController;
	private final PagePreviewCard card;

	/**
	 * Constructor
	 *
	 * @param app app
	 * @param data dialog transkeys
	 * @param card to rename.
	 */
	public PreviewCardRenameDialog(AppW app,
			DialogData data,
			PagePreviewCard card) {
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
