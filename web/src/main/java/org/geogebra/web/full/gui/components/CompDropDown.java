package org.geogebra.web.full.gui.components;

import org.geogebra.web.html5.main.AppW;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;

public class CompDropDown extends FlowPanel {
	private final AppW app;
	private Label selectedOption;

	public CompDropDown(AppW app, String label) {
		this.app = app;
		addStyleName("dropDown");
		buildGUI(label);
		setSelectedOption(0);
	}

	private void buildGUI(String labelStr) {
		FlowPanel optionHolder = new FlowPanel();
		optionHolder.addStyleName("optionLabelHolder");

		Label label = new Label(app.getLocalization().getMenu(labelStr));
		label.addStyleName("label");
		optionHolder.add(label);

		selectedOption = new Label();
		selectedOption.addStyleName("selectedOption");
		optionHolder.add(selectedOption);
		add(optionHolder);

		SimplePanel arrowIcon = new SimplePanel();
		arrowIcon.addStyleName("arrow");
		arrowIcon.getElement().setInnerHTML("<svg xmlns=\"http://www.w3.org/2000/svg\" "
				+ "width=\"24\" height=\"24\" viewBox=\"0 0 24 24\"><path d=\"M7 10l5 5 5-5z\"/>"
				+ "<path fill=\"none\" d=\"M0 0h24v24H0z\"/></svg>");
		add(arrowIcon);
	}

	public void setSelectedOption(int idx) {
		selectedOption.setText("Selected option");
	}
}
