package org.geogebra.web.full.gui.dialog;

import org.geogebra.web.full.gui.pagecontrolpanel.PagePreviewCard;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.main.PageListControllerInterface;
import org.geogebra.web.shared.components.DialogData;

public class PreviewCardRenameDialog extends MaterialRenameDialog {

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
		super(app, data, card);
		pageController = app.getPageController();
		this.card = card;
	}

	@Override
	protected boolean isTextInvalid() {
		// empty input is valid.
		return isInputChanged();
	}

	/**
	 * Selects all text in the input field.
	 */
	public void selectAll() {
		getInputField().getTextComponent().selectAll();
	}

	@Override
	protected void renameCard() {
		pageController.rename(card, getInputText());
	}
}
