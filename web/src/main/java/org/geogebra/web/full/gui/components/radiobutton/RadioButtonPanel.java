package org.geogebra.web.full.gui.components.radiobutton;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.main.Localization;

import com.google.gwt.user.client.ui.FlowPanel;

public class RadioButtonPanel extends FlowPanel implements SetLabels {
	private List<ComponentRadioButton> radioButtonList = new ArrayList<>();

	/**
	 * constructor for panel holding multiple radio buttons
	 * @param loc - localization
	 * @param data - radio button list data
	 */
	public RadioButtonPanel(Localization loc, List<RadioButtonData> data) {
		addStyleName("radioButtonPanel");
		for (RadioButtonData buttonData : data) {
			ComponentRadioButton radioBtn = new ComponentRadioButton(loc, buttonData,
					() -> {
						for (ComponentRadioButton radioButton : radioButtonList) {
							radioButton.setSelected(false);
						}
						if (buttonData.getCallback() != null) {
							buttonData.getCallback().run();
						}
					});
			radioButtonList.add(radioBtn);
			add(radioBtn);
		}
	}

	@Override
	public void setLabels() {
		for (ComponentRadioButton radioButton : radioButtonList) {
			radioButton.setLabels();
		}
	}

	/**
	 * @param idx - index of radio button in the panel
	 * @param selected - true if should set idx-th radio button to selected
	 */
	public void setValueOfNthRadioButton(int idx, boolean selected) {
		if (radioButtonList.size() <= idx) {
			return;
		}
		radioButtonList.get(idx).setSelected(selected);
	}
}
