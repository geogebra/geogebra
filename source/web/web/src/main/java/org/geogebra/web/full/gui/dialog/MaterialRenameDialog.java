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
