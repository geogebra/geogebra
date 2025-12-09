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

package org.geogebra.web.full.gui.components;

import static org.geogebra.common.properties.PropertyView.*;

import java.util.List;

import org.geogebra.common.euclidian.event.PointerEventType;
import org.geogebra.common.gui.SetLabels;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.html5.gui.BaseWidgetFactory;
import org.geogebra.web.html5.gui.util.AriaHelper;
import org.geogebra.web.html5.gui.util.ClickStartHandler;
import org.geogebra.web.html5.gui.util.Dom;
import org.geogebra.web.html5.main.AppW;
import org.gwtproject.user.client.ui.FlowPanel;
import org.gwtproject.user.client.ui.Label;
import org.gwtproject.user.client.ui.SimplePanel;

import elemental2.dom.KeyboardEvent;

public class ComponentDropDown extends FlowPanel implements SetLabels,
		ConfigurationUpdateDelegate, VisibilityUpdateDelegate {
	private final AppW app;
	private Label label;
	private final String labelKey;
	private Label selectedOption;
	private boolean isDisabled = false;
	private DropDownComboBoxController controller;
	private boolean fullWidth = false;
	private Dropdown propertyView;

	/**
	 * Material drop-down component.
	 * @param app see {@link AppW}
	 * @param label of drop-down
	 * @param items popup elements
	 */
	private ComponentDropDown(AppW app, String label, List<String> items) {
		this.app = app;
		labelKey = label;
		addStyleName("dropDown");
		setAccessibilityProperties();

		buildGUI(label);
		addClickHandler();

		initController(items);

		Dom.addEventListener(this.getElement(), "keydown", event -> {
			KeyboardEvent e = (KeyboardEvent) event;
			if ("Enter".equals(e.code) || "Space".equals(e.code)) {
				if (!isDisabled) {
					controller.toggleAsDropDown(fullWidth);
				}
			}
		});
	}

	/**
	 * @param app see {@link AppW}
	 * @param label label of drop-down
	 * @param items popup elements
	 * @param defaultIdx selected index by default
	 */
	public ComponentDropDown(AppW app, String label, List<String> items, int defaultIdx) {
		this(app, label, items);
		controller.setSelectedOption(defaultIdx);
		updateSelectionText();
	}

	/**
	 * @param app see {@link AppW}
	 * @param property see {@link org.geogebra.common.properties.PropertyView.Dropdown}
	 */
	public ComponentDropDown(AppW app, Dropdown property) {
		this(app, null, property);
	}

	/**
	 * @param app see {@link AppW}
	 * @param label label of drop-down
	 * @param property see {@link org.geogebra.common.properties.PropertyView.Dropdown}
	 */
	public ComponentDropDown(AppW app, String label, Dropdown property) {
		this(app, label, property.getItems());
		propertyView = property;
		Integer index = property.getSelectedItemIndex();
		if (index == null) {
			index = 0;
		}
		controller.setSelectedOption(index);

		updateSelectionText();
		property.setConfigurationUpdateDelegate(this);
		property.setVisibilityUpdateDelegate(this);
	}

	private void initController(List<String> items) {
		controller = new DropDownComboBoxController(app, this,
				() -> items, labelKey, () -> {
			removeStyleName("active");
			AriaHelper.setAriaExpanded(this, false);
		});
		controller.addChangeHandler(() -> {
			propertyView.setSelectedItemIndex(controller.getSelectedIndex());
			updateSelectionText();
		});
		controller.setFocusAnchor(getElement());
		updateSelectionText();
	}

	private void buildGUI(String labelStr) {
		FlowPanel optionHolder = new FlowPanel();
		optionHolder.addStyleName("optionLabelHolder");

		if (labelStr != null && !labelStr.isEmpty()) {
			label = BaseWidgetFactory.INSTANCE.newSecondaryText(
					app.getLocalization().getMenu(labelStr), "label");
			optionHolder.add(label);
		}

		selectedOption = BaseWidgetFactory.INSTANCE.newPrimaryText("", "selectedOption");
		optionHolder.add(selectedOption);
		add(optionHolder);
		add(createArrowIcon());
	}

	static SimplePanel createArrowIcon() {
		SimplePanel arrowIcon = new SimplePanel();
		arrowIcon.addStyleName("arrow");
		arrowIcon.getElement().setInnerHTML(MaterialDesignResources.INSTANCE
				.arrow_drop_down().getSVG());
		AriaHelper.setAriaHidden(arrowIcon);
		arrowIcon.getElement().getFirstChildElement()
				.setAttribute("focusable", "false");
		return arrowIcon;
	}

	// Drop-down handlers

	private void addClickHandler() {
		ClickStartHandler.init(this, new ClickStartHandler(true, true) {

			@Override
			public void onClickStart(int x, int y, PointerEventType type) {
				if (!isDisabled) {
					controller.toggleAsDropDown(fullWidth);
				}
			}
		});
	}

	/**
	 * Add a change handler.
	 * @param changeHandler change handler
	 */
	public void addChangeHandler(Runnable changeHandler) {
		controller.addChangeHandler(changeHandler);
	}

	// Status helpers

	/**
	 * Disable drop-down component.
	 * @param disabled true, if drop-down should be disabled
	 */
	public void setDisabled(boolean disabled) {
		isDisabled = disabled;
		Dom.toggleClass(this, "disabled", disabled);
		AriaHelper.setAriaDisabled(this, disabled);
	}

	// Helpers

	public int getSelectedIndex() {
		return controller.getSelectedIndex();
	}

	/**
	 * @param dropdownIndex selected index
	 */
	public void setSelectedIndex(int dropdownIndex) {
		controller.setSelectedOption(dropdownIndex);
		updateSelectionText();
	}

	/**
	 * @return text of selected item
	 */
	public String getSelectedText() {
		return controller.getSelectedText();
	}

	/**
	 * This should be called automatically when an item is selected (by user or programmatically).
	 */
	private void updateSelectionText() {
		selectedOption.setText(controller.getSelectedText());
		AriaHelper.setLabel(this, app.getLocalization().getMenu(labelKey) + " "
			+ controller.getSelectedText());
	}

	/**
	 * Reset dropdown to the model (property) value.
	 */
	public void resetFromModel() {
		controller.resetFromModel(propertyView);
		updateSelectionText();
	}

	public void setFullWidth(boolean isFullWidth) {
		fullWidth = isFullWidth;
	}

	/**
	 * @param property update property
	 */
	public void setProperty(Dropdown property) {
		this.propertyView = property;
	}

	@Override
	public void setLabels() {
		if (label != null) {
			label.setText(app.getLocalization().getMenu(labelKey));
		}
		controller.setLabels();
		updateSelectionText();
	}

	private void setAccessibilityProperties() {
		AriaHelper.setRole(this, "button");
		AriaHelper.setTabIndex(this, 0);
		AriaHelper.setAriaHaspopup(this, "listbox");
		AriaHelper.setAriaExpanded(this, false);
	}

	@Override
	public void configurationUpdated() {
		controller.resetFromModel(propertyView);
		Integer index = propertyView.getSelectedItemIndex();
		if (index != null) {
			setSelectedIndex(index);
		}
	}

	@Override
	public void visibilityUpdated() {
		setVisible(propertyView.isVisible());
	}
}
