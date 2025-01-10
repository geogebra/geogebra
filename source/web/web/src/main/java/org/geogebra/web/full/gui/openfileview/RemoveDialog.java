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