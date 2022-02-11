package org.geogebra.web.full.gui.util;

import org.geogebra.common.move.ggtapi.models.Material;
import org.geogebra.web.full.gui.components.ComponentCheckbox;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.shared.components.dialog.DialogData;

public class SaveDialogMow extends DoYouWantToSaveChangesDialog {
	private ComponentCheckbox templateCheckbox;

	/**
	 * base dialog constructor
	 * @param app - see {@link AppW}
	 * @param dialogData - contains trans keys for title and buttons
	 *
	 */
	public SaveDialogMow(AppW app, DialogData dialogData, boolean addTempCheckBox) {
		super(app, dialogData, false);
		if (addTempCheckBox) {
			addStyleName("templateSave");
		} else {
			templateCheckbox.setVisible(false);
		}
		setOnPositiveAction(() -> {
			if (templateCheckbox.isSelected()) {
				setSaveType(Material.MaterialType.ggsTemplate);
				app.getSaveController().ensureTypeOtherThan(Material.MaterialType.ggs);
			} else {
				setSaveType(Material.MaterialType.ggs);
				app.getSaveController().ensureTypeOtherThan(Material.MaterialType.ggsTemplate);
			}
			app.getSaveController().saveAs(getInputField().getText(),
					getSaveVisibility(), this);
		});
	}

	@Override
	public void buildContent() {
		super.buildContent();
		templateCheckbox = new ComponentCheckbox(app.getLocalization(), false,
				"saveTemplate");
		getContentPanel().add(templateCheckbox);
	}

	@Override
	public void show() {
		super.show();
		Material activeMaterial = app.getActiveMaterial();
		templateCheckbox.setSelected(activeMaterial != null && Material.MaterialType.ggsTemplate
				.equals(activeMaterial.getType()));
	}
}