package org.geogebra.desktop.gui.dialog;

import org.geogebra.common.gui.dialog.options.model.EnableDynamicCaptionModel;
import org.geogebra.desktop.gui.inputfield.AutoCompleteTextFieldD;
import org.geogebra.desktop.main.AppD;

public class EnableDynamicCaptionPanel extends CheckboxPanel {
	private final AutoCompleteTextFieldD textField;
	private final ComboPanel combo;

	public EnableDynamicCaptionPanel(AppD app, AutoCompleteTextFieldD textField,
			ComboPanel  combo,
			UpdateTabs tabs) {
		super(app, "UseTextAsCaption", tabs,
				new EnableDynamicCaptionModel(null, app));
		this.textField = textField;
		this.combo = combo;
		app.setFlowLayoutOrientation(this);
	}

	@Override
	public void updateCheckbox(boolean value) {
		super.updateCheckbox(value);
		combo.setVisible(value);
		setCaptionTextFieldEnabled(!value);
	}

	@Override
	public void apply(boolean value) {
		super.apply(value);
		combo.setVisible(value);
		setCaptionTextFieldEnabled(!value);
	}

	private void setCaptionTextFieldEnabled(boolean enable) {
		textField.setEnabled(enable);
	}

	public boolean isSelected() {
		return getCheckbox().isSelected();
	}
}
