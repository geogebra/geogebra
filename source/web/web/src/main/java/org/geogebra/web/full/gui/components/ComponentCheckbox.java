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

import java.util.function.Consumer;

import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.main.Localization;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.html5.gui.BaseWidgetFactory;
import org.geogebra.web.html5.gui.util.AriaHelper;
import org.geogebra.web.html5.gui.util.Dom;
import org.gwtproject.user.client.ui.FlowPanel;
import org.gwtproject.user.client.ui.Label;
import org.gwtproject.user.client.ui.SimplePanel;

import elemental2.dom.KeyboardEvent;

/**
 * material design checkbox component
 */
public class ComponentCheckbox extends FlowPanel implements SetLabels,
		ConfigurationUpdateDelegate, VisibilityUpdateDelegate {
	private final Localization loc;
	private boolean isSelected;
	private FlowPanel checkbox;
	private Label checkboxLbl;
	private final String checkboxTxt;
	private final Consumer<Boolean> callback;
	private boolean stopPropagation = false;
	private Checkbox checkBoxProperty;

	/**
	 * @param loc {@link Localization}
	 * @param selected whether the checkbox should be selected by default
	 * @param checkboxText label of checkbox
	 * @param callback click handler
	 */
	public ComponentCheckbox(Localization loc, boolean selected, String checkboxText,
			Consumer<Boolean> callback) {
		this.loc = loc;
		isSelected = selected;
		this.checkboxTxt = checkboxText;
		this.callback = callback;

		addStyleName("checkboxPanel");
		buildComponent();
		addClickAndKeyHandler();

		setSelected(selected);
		setLabels();
		addAccessibilityInfo();
	}

	/**
	 * @param loc {@link Localization}
	 * @param selected whether it should be selected by default
	 * @param checkboxText label of checkbox
	 */
	public ComponentCheckbox(Localization loc, boolean selected, String checkboxText) {
		this(loc, selected, checkboxText, null);
	}

	/**
	 * @param loc {@link Localization}
	 * @param checkBoxProperty see {@link org.geogebra.common.properties.PropertyView.Checkbox}
	 * @param checkboxText label of checkbox
	 * @param callback click handler
	 * @param stopPropagation whether it should stop propagation on click
	 */
	public ComponentCheckbox(Localization loc, Checkbox checkBoxProperty, String checkboxText,
			Consumer<Boolean> callback, boolean stopPropagation) {
		this(loc, checkBoxProperty.isSelected(), checkboxText, callback);
		this.checkBoxProperty = checkBoxProperty;
		this.stopPropagation = stopPropagation;
		setDisabled(!checkBoxProperty.isEnabled());
		checkBoxProperty.setConfigurationUpdateDelegate(this);
		checkBoxProperty.setVisibilityUpdateDelegate(this);
	}

	private void buildComponent() {
		checkbox = new FlowPanel();
		checkbox.addStyleName("checkbox");

		addCheckbox();
		addLabel();
	}

	private void addCheckbox() {
		SimplePanel background = new SimplePanel();
		background.addStyleName("background");
		SimplePanel checkMark = new SimplePanel();
		checkMark.getElement().setInnerHTML(MaterialDesignResources
				.INSTANCE.check_white().getSVG());
		checkMark.addStyleName("checkmark");
		checkbox.add(background);
		checkbox.add(checkMark);

		FlowPanel checkboxBg = new FlowPanel();
		checkboxBg.addStyleName("hoverBg");
		checkboxBg.addStyleName("ripple");
		checkbox.add(checkboxBg);
		add(checkbox);
	}

	private void addLabel() {
		if (!checkboxTxt.isEmpty()) {
			addStyleName("withLabel");
			checkboxLbl = BaseWidgetFactory.INSTANCE.newPrimaryText("", "checkboxLbl");
			add(checkboxLbl);
		}
	}

	private void addClickAndKeyHandler() {
		Dom.addEventListener(this.getElement(), "click", event -> {
			runAction();
			if (stopPropagation) {
				event.stopPropagation();
			}
		});

		Dom.addEventListener(this.getElement(), "keydown", event -> {
			KeyboardEvent e = (KeyboardEvent) event;
			if ("Space".equals(e.code)) {
				runAction();
			}
		});
	}

	private void runAction() {
		if (!isDisabled()) {
			setSelected(!isSelected());
			if (callback != null) {
				callback.accept(isSelected());
			}
		}
	}

	private void addAccessibilityInfo() {
		AriaHelper.setLabel(this, loc.getMenu(checkboxTxt));
		AriaHelper.setTabIndex(this, 0);
		AriaHelper.setRole(this, "checkbox");
	}

	/**
	 * @return true if checkbox is selected
	 */
	public boolean isSelected() {
		return isSelected;
	}

	/**
	 * @param selected whether the checkbox is checked or not
	 */
	public void setSelected(boolean selected) {
		isSelected = selected;
		updateCheckboxStyle();
		AriaHelper.setChecked(this, selected);
	}

	/**
	 * Enable/disable check box
	 * @param disabled whether it should be disabled or not
	 */
	public void setDisabled(boolean disabled) {
		Dom.toggleClass(this, "disabled", disabled);
	}

	private boolean isDisabled() {
		return getStyleName().contains("disabled");
	}

	/**
	 * update style of checkbox depending on its status (selected or not selected)
	 */
	public void updateCheckboxStyle() {
		Dom.toggleClass(checkbox, "selected", isSelected());
	}

	@Override
	public void setLabels() {
		if (checkboxLbl != null) {
			checkboxLbl.setText(loc.getMenu(checkboxTxt));
			AriaHelper.setLabel(this, loc.getMenu(checkboxTxt));
		}
	}

	@Override
	public void configurationUpdated() {
		setSelected(checkBoxProperty.isSelected());
		setDisabled(!checkBoxProperty.isEnabled());
	}

	@Override
	public void visibilityUpdated() {
		setVisible(checkBoxProperty.isVisible());
	}
}
