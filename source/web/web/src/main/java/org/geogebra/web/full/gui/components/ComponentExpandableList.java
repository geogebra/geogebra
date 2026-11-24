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

import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.properties.PropertyView;
import org.geogebra.common.properties.PropertyView.ConfigurationUpdateDelegate;
import org.geogebra.keyboard.web.KeyboardResources;
import org.geogebra.web.html5.gui.BaseWidgetFactory;
import org.geogebra.web.html5.gui.util.AriaHelper;
import org.geogebra.web.html5.gui.util.Dom;
import org.geogebra.web.html5.main.AppW;
import org.gwtproject.dom.client.BrowserEvents;
import org.gwtproject.user.client.ui.FlowPanel;
import org.gwtproject.user.client.ui.Label;
import org.gwtproject.user.client.ui.SimplePanel;
import org.gwtproject.user.client.ui.Widget;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import elemental2.dom.Event;
import elemental2.dom.KeyboardEvent;

public final class ComponentExpandableList extends FlowPanel
		implements SetLabels, ConfigurationUpdateDelegate {
	private final AppW appW;
	private final PropertyView.ExpandableList propertyView;
	private Label title;
	private @Nullable ComponentCheckbox checkbox;
	private FlowPanel contentPanel;

	/**
	 * Creates expandable list component.
	 * @param appW see {@link AppW}
	 * @param propertyView shared expansion and checkbox state
	 */
	public ComponentExpandableList(
			@NonNull AppW appW, PropertyView.@NonNull ExpandableList propertyView) {
		this.appW = appW;
		this.propertyView = propertyView;
		propertyView.setConfigurationUpdateDelegate(this);

		addStyleName("expandableList");
		buildExpandableList();
		AriaHelper.setRole(this, "button");
		AriaHelper.setTabIndex(this, 0);
		AriaHelper.setAriaExpanded(this, false);
		configurationUpdated();
	}

	private void buildExpandableList() {
		FlowPanel header = new FlowPanel();
		header.addStyleName("header");
		AriaHelper.setTabIndex(header, 0);

		addArrowTo(header);
		title = BaseWidgetFactory.INSTANCE.newPrimaryText(propertyView.getTitle(), "title");
		header.add(title);
		PropertyView.Checkbox checkboxPropertyView = propertyView.getCheckbox();
		if (checkboxPropertyView != null) {
			checkbox = new ComponentCheckbox(
					appW.getLocalization(),
					checkboxPropertyView.isSelected(),
					"",
					checkboxPropertyView::setSelected);
			Dom.addEventListener(checkbox.getElement(), "click", Event::stopPropagation);
			AriaHelper.setLabel(checkbox, propertyView.getTitle());
			AriaHelper.setLabel(this, propertyView.getTitle());
			header.add(checkbox);
		}

		contentPanel = new FlowPanel();
		contentPanel.addStyleName("containerPanel");

		add(header);
		add(contentPanel);
		addListeners(header);
	}

	private void addArrowTo(FlowPanel header) {
		SimplePanel arrow = new SimplePanel();
		arrow.addStyleName("headerArrow");
		arrow
				.getElement()
				.setInnerHTML(KeyboardResources.INSTANCE.keyboard_arrowRight_black().getSVG());
		AriaHelper.setAriaHidden(arrow);
		header.add(arrow);
	}

	private void addListeners(FlowPanel header) {
		Dom.addEventListener(header.getElement(), "click", (e) -> propertyView.onExpandPressed());

		Dom.addEventListener(header.getElement(), BrowserEvents.KEYDOWN, event -> {
			KeyboardEvent e = (KeyboardEvent) event;
			if ("Enter".equals(e.code) || "Space".equals(e.code)) {
				if (Dom.getActiveElement() == header.getElement()) {
					propertyView.onExpandPressed();
					e.preventDefault(); // prevent scroll of panel on SPACE
				}
			}
		});
	}

	/**
	 * Fills the content with given UI element.
	 * @param widget UI element
	 */
	public void addToContent(Widget widget) {
		contentPanel.add(widget);
	}

	@Override
	public void configurationUpdated() {
		boolean expanded = propertyView.isExpanded();
		Dom.toggleClass(this, "extended", expanded);
		AriaHelper.setAriaExpanded(this, expanded);
		AriaHelper.setTabIndex(contentPanel, expanded ? 0 : -1);
		PropertyView.Checkbox checkboxPropertyView = propertyView.getCheckbox();
		if (checkboxPropertyView != null) {
			checkbox.setSelected(checkboxPropertyView.isSelected());
			checkbox.setDisabled(!checkboxPropertyView.isEnabled());
			checkbox.setVisible(checkboxPropertyView.isVisible());
		}
	}

	@Override
	public void setLabels() {
		String translatedTitle = propertyView.getTitle();
		title.setText(translatedTitle);
		AriaHelper.setLabel(this, translatedTitle);
	}

	/** @return checkbox in the header, or null if the list has no checkbox */
	public @Nullable ComponentCheckbox getCheckbox() {
		return checkbox;
	}
}
