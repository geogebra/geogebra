package org.geogebra.web.full.gui.dialog.options;

import org.geogebra.common.gui.dialog.options.model.EnableDynamicCaptionModel;
import org.geogebra.common.main.App;
import org.geogebra.web.html5.gui.inputfield.AutoCompleteTextFieldW;

class EnableDynamicCaptionPanel extends CheckboxPanel {
	private final AutoCompleteTextFieldW textField;

	public EnableDynamicCaptionPanel(App app,
			AutoCompleteTextFieldW textField) {
		super("UseTextAsCaption", app.getLocalization(),
				new EnableDynamicCaptionModel(null, app));
		this.textField = textField;
	}

	@Override
	public void updateCheckbox(boolean value) {
		super.updateCheckbox(value);
		setCaptionTextFieldEnabled(!value);
	}

	@Override
	public void onChecked() {
		setCaptionTextFieldEnabled(!getCheckbox().getValue());
	}

	private void setCaptionTextFieldEnabled(boolean enable) {
		textField.setEnabled(enable);
	}

}
