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

import java.util.ArrayList;
import java.util.List;

import org.geogebra.web.html5.gui.util.AriaHelper;
import org.geogebra.web.html5.gui.util.Dom;
import org.geogebra.web.html5.gui.view.button.StandardButton;
import org.gwtproject.user.client.ui.FlowPanel;
import org.gwtproject.user.client.ui.Widget;

import elemental2.dom.KeyboardEvent;

public class ComponentConnectedButtonGroup extends FlowPanel
		implements ConfigurationUpdateDelegate, VisibilityUpdateDelegate {
	private final ConnectedButtonGroup connectedButtonGroupProperty;
	private StandardButton selectedButton;
	private final List<StandardButton> buttonList = new ArrayList<>();

	/**
	 * Creates a connected button group based on {@link ConnectedButtonGroup}.
	 * @param connectedButtonGroupProperty {@link ConnectedButtonGroup}
	 * @param widgets list of focusable widgets
	 */
	public ComponentConnectedButtonGroup(ConnectedButtonGroup connectedButtonGroupProperty,
			List<Widget> widgets) {
		this.connectedButtonGroupProperty = connectedButtonGroupProperty;
		addStyleName("connectedButtonGroup");
		buildConnectedButtonGroup();
		Integer currentIndex = connectedButtonGroupProperty.getSelectedButtonIndex();
		if (currentIndex != null) {
			setSelectedButton(currentIndex);
		}

		connectedButtonGroupProperty.setConfigurationUpdateDelegate(this);
		connectedButtonGroupProperty.setVisibilityUpdateDelegate(this);
		AriaHelper.setRole(this, "radiogroup");

		widgets.addAll(buttonList);
		Dom.addEventListener(getElement(),  "keydown", event -> {
			KeyboardEvent e = (KeyboardEvent) event;
			if ("ArrowLeft".equals(e.code) || "ArrowRight".equals(e.code)) {
				moveTabSelection("ArrowLeft".equals(e.code) ? -1 : 1);
				event.stopPropagation();
			}
		});
	}

	private void buildConnectedButtonGroup() {
		List<String> buttonLabels = connectedButtonGroupProperty.getButtonLabels();
		for (int index = 0; index < connectedButtonGroupProperty.count(); index++) {
			StandardButton button = new StandardButton(buttonLabels.get(index));
			addAccessibilityInfo(button, buttonLabels.get(index));
			int finalIndex = index;
			button.addFastClickHandler(source -> setSelectedButton(finalIndex));
			Dom.addEventListener(button.getElement(), "keydown", event -> {
				KeyboardEvent e = (KeyboardEvent) event;
				if ("Enter".equals(e.code) || "Space".equals(e.code)) {
					setSelectedButton(finalIndex);
				}
			});
			button.addStyleName("connectedButton");
			button.addStyleName("keyboardFocus");
			button.addStyleName("ripple");
			add(button);
			buttonList.add(button);
		}
	}

	private void setSelectedButton(int newIndex) {
		updateSelectedButton(buttonList.get(newIndex));
		connectedButtonGroupProperty.setSelectedButtonIndex(newIndex);
	}

	private void updateSelectedButton(StandardButton buttonToSelect) {
		selectButton(selectedButton, false);
		selectButton(buttonToSelect, true);
		selectedButton = buttonToSelect;
	}

	private void selectButton(StandardButton button, boolean selected) {
		if (button != null) {
			Dom.toggleClass(button, "selected", selected);
			AriaHelper.setChecked(button, selected);
			AriaHelper.setTabIndex(button, selected ? 1 : -1);
		}
	}

	private void addAccessibilityInfo(StandardButton button, String label) {
		AriaHelper.setRole(button, "radio");
		AriaHelper.setLabel(button, label);
	}

	private void moveTabSelection(int increment) {
		Integer index = connectedButtonGroupProperty.getSelectedButtonIndex();
		if (index != null) {
			int newIndex = (index + buttonList.size() + increment) % buttonList.size();
			buttonList.get(newIndex).getElement().focus();
			setSelectedButton(newIndex);
		}
	}

	@Override
	public void configurationUpdated() {
		Dom.toggleClass(this, "disabled", !connectedButtonGroupProperty.isEnabled());
	}

	@Override
	public void visibilityUpdated() {
		setVisible(connectedButtonGroupProperty.isVisible());
	}
}
