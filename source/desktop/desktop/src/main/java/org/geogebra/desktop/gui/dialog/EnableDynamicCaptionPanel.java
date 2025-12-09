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

package org.geogebra.desktop.gui.dialog;

import org.geogebra.common.gui.dialog.options.model.EnableDynamicCaptionModel;
import org.geogebra.desktop.gui.inputfield.AutoCompleteTextFieldD;
import org.geogebra.desktop.main.AppD;

public class EnableDynamicCaptionPanel extends CheckboxPanel {
	private final AutoCompleteTextFieldD textField;
	private final ComboPanel combo;

	/**
	 * @param app application
	 * @param textField text input field
	 * @param combo text selection combo-box
	 * @param tabs parent tabbed view
	 */
	public EnableDynamicCaptionPanel(AppD app, AutoCompleteTextFieldD textField,
			ComboPanel combo, UpdateTabs tabs, EnableDynamicCaptionModel model) {
		super(app, tabs, model);
		this.textField = textField;
		this.combo = combo;
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
