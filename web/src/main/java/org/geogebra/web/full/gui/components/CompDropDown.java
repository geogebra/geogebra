package org.geogebra.web.full.gui.components;

import java.util.List;

import org.geogebra.common.gui.SetLabels;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.html5.gui.util.Dom;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;

public class CompDropDown extends FlowPanel implements SetLabels, CompDropDownComboBoxI {
	private final AppW app;
	private Label label;
	private String labelKey;
	private Label selectedOption;
	private boolean isDisabled = false;
	private Runnable changeHandler;
	private DropDownComboBoxController controller;

	/**
	 * Material rop-down component
	 * @param app - see {@link AppW}
	 * @param label - label of drop-down
	 * @param items - popup elements
	 */
	public CompDropDown(AppW app, String label, List<String> items) {
		this.app = app;
		labelKey = label;
		addStyleName("dropDown");

		buildGUI(label);
		controller = new DropDownComboBoxController(app, this, selectedOption, items);
	}

	private void buildGUI(String labelStr) {
		FlowPanel optionHolder = new FlowPanel();
		optionHolder.addStyleName("optionLabelHolder");

		if (labelStr != null && !labelStr.isEmpty()) {
			label = new Label(app.getLocalization().getMenu(labelStr));
			label.addStyleName("label");
			optionHolder.add(label);
		}

		selectedOption = new Label();
		selectedOption.addStyleName("selectedOption");
		optionHolder.add(selectedOption);
		add(optionHolder);

		SimplePanel arrowIcon = new SimplePanel();
		arrowIcon.addStyleName("arrow");
		arrowIcon.getElement().setInnerHTML(MaterialDesignResources.INSTANCE
				.arrow_drop_down().getSVG());
		add(arrowIcon);
	}

	@Override
	public void toggleExpanded() {
		if (controller.isOpened()) {
			controller.closePopup();
		} else {
			controller.showAsDropDown(getStyleElement().getClientWidth());
		}
	}

	public int getSelectedIndex() {
		return controller.getSelectedIndex();
	}

	/**
	 * Disable drop-down component
	 * @param disabled - true, if drop-down should be disabled
	 */
	public void setDisabled(boolean disabled) {
		isDisabled = disabled;
		Dom.toggleClass(this, "disabled", disabled);
	}

	@Override
	public void setLabels() {
		if (label != null) {
			label.setText(app.getLocalization().getMenu(labelKey));
		}
		controller.setLabels();
	}

	@Override
	public void updateSelectionText(String text) {
		selectedOption.setText(text);
	}

	public void setChangeHandler(Runnable changeHandler) {
		this.changeHandler = changeHandler;
	}
}
