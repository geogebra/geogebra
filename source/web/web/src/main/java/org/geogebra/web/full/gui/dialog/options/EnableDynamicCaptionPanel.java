/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.web.full.gui.dialog.options;

import org.geogebra.common.gui.dialog.options.model.EnableDynamicCaptionModel;
import org.geogebra.common.main.App;
import org.geogebra.web.html5.gui.inputfield.AutoCompleteTextFieldW;

class EnableDynamicCaptionPanel extends CheckboxPanel {
	private final AutoCompleteTextFieldW textField;

	public EnableDynamicCaptionPanel(App app,
			AutoCompleteTextFieldW textField) {
		super(app.getLocalization(),
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
		setCaptionTextFieldEnabled(!getCheckbox().isSelected());
	}

	private void setCaptionTextFieldEnabled(boolean enable) {
		textField.setEnabled(enable);
	}

}
