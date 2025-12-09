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

package org.geogebra.web.full.gui.components.radiobutton;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.main.Localization;
import org.gwtproject.user.client.ui.FlowPanel;

public class RadioButtonPanel<T> extends FlowPanel implements SetLabels {
	private List<ComponentRadioButton<T>> radioButtonList = new ArrayList<>();

	/**
	 * constructor for panel holding multiple radio buttons
	 * @param loc - localization
	 * @param data - radio button list data
	 */
	public RadioButtonPanel(Localization loc, List<RadioButtonData<T>> data, T defaultValue,
			Consumer<T> callback) {
		addStyleName("radioButtonPanel");
		for (int i = 0; i < data.size(); i++) {
			RadioButtonData<T> curData = data.get(i);
			ComponentRadioButton<T> radioBtn = new ComponentRadioButton<>(loc, curData,
					i, data.size());
			radioBtn.setSelected(Objects.equals(defaultValue, curData.getValue()));
			radioBtn.setCallback(() -> {
				for (ComponentRadioButton<T> radioButton : radioButtonList) {
					radioButton.setSelected(radioBtn == radioButton);
				}
				if (callback != null && radioBtn.isSelected()) {
					callback.accept(curData.getValue());
				}
			});
			radioButtonList.add(radioBtn);
			add(radioBtn);
		}
	}

	@Override
	public void setLabels() {
		for (ComponentRadioButton<T> radioButton : radioButtonList) {
			radioButton.setLabels();
		}
	}

	/**
	 * @param value - selected value
	 */
	public void setValue(T value) {
		for (ComponentRadioButton<T> btn : radioButtonList) {
			btn.setSelected(Objects.equals(value, btn.getValue()));
		}
	}

	/**
	 * @return selected value
	 */
	public T getValue() {
		for (ComponentRadioButton<T> btn: radioButtonList) {
			if (btn.isSelected()) {
				return btn.getValue();
			}
		}
		// should not be possible to have none selected, avoid NPE anyway
		return radioButtonList.get(0).getValue();
	}

	/**
	 * @param idx - index of radio button in the panel
	 * @param disabled - true if should disable the idx-th radio button
	 */
	public void disableNthRadioButton(int idx, boolean disabled) {
		if (radioButtonList.size() <= idx) {
			return;
		}
		radioButtonList.get(idx).setDisabled(disabled);
	}
}
