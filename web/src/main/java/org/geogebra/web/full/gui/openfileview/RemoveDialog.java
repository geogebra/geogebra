package org.geogebra.web.full.gui.openfileview;

import org.geogebra.common.main.App;
import org.geogebra.web.full.gui.dialog.OptionDialog;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;

public class RemoveDialog extends OptionDialog {

	private MaterialCard card;
	private Label confirmDelete;

	public RemoveDialog(Panel root, App app,
			MaterialCard controller) {
		super(root, app);
		FlowPanel main = new FlowPanel();
		confirmDelete = new Label();
		main.add(confirmDelete);
		main.add(getButtonPanel());
		add(main);
		enablePrimaryButton(true);
		this.card = controller;
		setLabels();
	}

	private void setLabels() {
		this.updateButtonLabels("Delete");
		confirmDelete.setText(app.getLocalization().getPlain("ConfirmDeleteA",
				card.getMaterial().getTitle()));
	}

	@Override
	protected void processInput() {
		card.onConfirmDelete();
		hide();
	}

}
