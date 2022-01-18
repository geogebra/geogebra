package org.geogebra.web.full.gui.components.radiobutton;

import java.util.ArrayList;

import org.geogebra.common.main.Localization;

import com.google.gwt.user.client.ui.FlowPanel;

public class RadioButtonPanel extends FlowPanel {
	private ArrayList<ComponentRadioButton> radioButtonList = new ArrayList<>();

	public RadioButtonPanel(Localization loc, ArrayList<RadioButtonData> data) {
		addStyleName("radioButtonPanel");
		for (RadioButtonData buttonData : data) {
			ComponentRadioButton radioBtn = new ComponentRadioButton(loc, buttonData,
					() -> {
						for (ComponentRadioButton radioButton : radioButtonList) {
							radioButton.setSelected(false);
						}
					});
			radioButtonList.add(radioBtn);
			add(radioBtn);
		}
	}
}
