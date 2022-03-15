package org.geogebra.web.full.gui.openfileview;

import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.shared.components.dialog.ComponentDialog;
import org.geogebra.web.shared.components.dialog.DialogData;

import com.google.gwt.user.client.ui.Label;

/**
 * Confirmation dialog for removing cards.
 */
public class RemoveDialog extends ComponentDialog {
	private MaterialCard card;

	/**
	 * @param app
	 *            application
	 * @param data
	 *            dialog translation keys
	 * @param card
	 *            card
	 */
	public RemoveDialog(AppW app, DialogData data, MaterialCard card) {
		super(app, data, false, true);
		this.card = card;
		addStyleName("removeMaterial");
		addStyleName("mebis");
		buildContent();
	}

	private void buildContent() {
		Label confirmDelete = new Label(app.getLocalization().getPlain("ConfirmDeleteA",
				card.getCardTitle()));
		confirmDelete.setStyleName("message");
		addDialogContent(confirmDelete);
	}
}