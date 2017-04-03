package org.geogebra.keyboard.web;


import org.geogebra.keyboard.base.model.WeightedButton;

import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;

public class ButtonImpl extends FlowPanel {

	public ButtonImpl(WeightedButton wb) {
		add(new Label(wb.getActionName()));
		Style s = getElement().getStyle();
		s.setBackgroundColor("gray");
		s.setPadding(5, Unit.PX);
		s.setMargin(5, Unit.PX);
	}

}
