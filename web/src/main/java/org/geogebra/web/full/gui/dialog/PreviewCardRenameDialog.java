package org.geogebra.web.full.gui.dialog;

import org.geogebra.web.full.gui.pagecontrolpanel.PagePreviewCard;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.shared.components.DialogData;

public class PreviewCardRenameDialog extends MaterialRenameDialog {

	/**
	 * Constructor
	 *
	 * @param app app
	 * @param data dialog transkeys
	 * @param card
	 */
	public PreviewCardRenameDialog(AppW app,
			DialogData data,
			PagePreviewCard card) {
		super(app, data, card);
		setInitialText(card.getSubtitle());
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
}
