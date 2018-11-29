package org.geogebra.web.full.gui.openfileview;

import org.geogebra.common.main.App;
import org.geogebra.web.full.gui.dialog.OptionDialog;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;

/**
 * Confirmation dialog for removing cards.
 */
public class RemoveDialog extends OptionDialog {

	private MaterialCard card;
	private Label confirmDelete;

	/**
	 * @param root
	 *            root panel to position this
	 * @param app
	 *            application
	 * @param card
	 *            card
	 */
	public RemoveDialog(Panel root, App app,
			MaterialCard card) {
		super(root, app);
		FlowPanel main = new FlowPanel();
		confirmDelete = new Label();
		confirmDelete.setStyleName("message");
		main.add(confirmDelete);
		main.add(getButtonPanel());
		add(main);
		setPrimaryButtonEnabled(true);
		this.card = card;
		setLabels();
		addStyleName("mebis");
	}

	private void setLabels() {
		this.updateButtonLabels("Delete");
		confirmDelete.setText(app.getLocalization().getPlain("ConfirmDeleteA",
				card.getMaterialTitle()));
	}

	@Override
	protected void processInput() {
		card.onConfirmDelete();
		hide();
	}

}
