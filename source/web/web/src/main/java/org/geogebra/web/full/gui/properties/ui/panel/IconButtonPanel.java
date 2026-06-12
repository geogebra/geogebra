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

package org.geogebra.web.full.gui.properties.ui.panel;

import static org.geogebra.common.properties.PropertyView.ConfigurationUpdateDelegate;
import static org.geogebra.common.properties.PropertyView.SingleSelectionIconRow;
import static org.geogebra.common.properties.PropertyView.VisibilityUpdateDelegate;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.properties.PropertyResource;
import org.geogebra.web.full.gui.toolbar.mow.toolbox.components.IconButton;
import org.geogebra.web.full.main.AppWFull;
import org.geogebra.web.html5.gui.BaseWidgetFactory;
import org.geogebra.web.html5.gui.accessibility.HasFocus;
import org.geogebra.web.html5.gui.util.AriaHelper;
import org.geogebra.web.html5.gui.util.Dom;
import org.geogebra.web.html5.main.AppW;
import org.gwtproject.user.client.ui.FlowPanel;
import org.gwtproject.user.client.ui.Label;

import elemental2.dom.KeyboardEvent;

public class IconButtonPanel extends FlowPanel implements SetLabels, ConfigurationUpdateDelegate,
		VisibilityUpdateDelegate, HasFocus {
	private final AppW appW;
	private Label label;
	private final String labelKey;
	private List<List<IconButton>> iconButtonList;
	private Runnable callback;
	private final List<SingleSelectionIconRow> propertyList;

	/**
	 * Created an icon button panel
	 * @param appW application
	 * @param property {@link org.geogebra.common.properties.PropertyView.SingleSelectionIconRow}
	 * @param addTitle whether title should be added or not
	 */
	public IconButtonPanel(AppW appW, SingleSelectionIconRow property, boolean addTitle) {
		this.appW = appW;
		propertyList = List.of(property);
		labelKey = property.getLabel();
		buildGUI(addTitle);
		property.setConfigurationUpdateDelegate(this);
		property.setVisibilityUpdateDelegate(this);
	}

	/**
	 * Created an icon button panel
	 * @param appW application
	 * @param labelKey title
	 * @param properties list of {@link SingleSelectionIconRow}
	 */
	public IconButtonPanel(AppW appW, String labelKey, List<SingleSelectionIconRow> properties) {
		this.appW = appW;
		propertyList = properties;
		this.labelKey = labelKey;
		buildGUI(true);
		for (SingleSelectionIconRow property : properties) {
			property.setConfigurationUpdateDelegate(this);
			property.setVisibilityUpdateDelegate(this);
		}
	}

	/**
	 * Created an icon button panel
	 * @param appW application
	 * @param property {@link org.geogebra.common.properties.PropertyView.SingleSelectionIconRow}
	 * @param addTitle whether title should be added or not
	 * @param callback callback
	 */
	public IconButtonPanel(AppW appW, SingleSelectionIconRow property,
			boolean addTitle, Runnable callback) {
		this(appW, property, addTitle);
		this.callback = callback;
	}

	private void buildGUI(boolean addTitle) {
		addStyleName("iconButtonPanel");
		String localizedLabel = appW.getLocalization().getMenu(labelKey);
		if (addTitle) {
			label = new Label(localizedLabel);
			add(label);
		}

		iconButtonList = new ArrayList<>();
		FlowPanel iconButtonListPanel = new FlowPanel();
		iconButtonListPanel.addStyleName("iconButtonListPanel");
		for (SingleSelectionIconRow property : propertyList) {
			List<IconButton> buttons = new ArrayList<>();
			FlowPanel iconPanel = new FlowPanel();
			iconPanel.addStyleName("iconPanel");
			AriaHelper.setTitle(iconPanel, localizedLabel);
			AriaHelper.setRole(iconPanel, "radiogroup");

			PropertyResource[] icons = property.getIcons().toArray(new PropertyResource[0]);
			String[] labels = property.getToolTipLabels();
			int idx = 0;
			Integer selectedIdx = property.getSelectedIconIndex();
			if (selectedIdx == null) {
				selectedIdx = 0;
			}

			for (PropertyResource icon : icons) {
				String label = labels != null && labels[idx] != null ? labels[idx] : "";
				IconButton btn = new IconButton(appW, null,
						((AppWFull) appW).getPropertiesIconResource().getImageResource(icon),
						label);
				updateButton(property, btn, idx, selectedIdx);
				iconPanel.add(btn);
				buttons.add(btn);
				final int index = idx;
				addRadioKeyHandler(buttons, btn, index);
				btn.addClickHandler(appW.getGlobalHandlers(),
						(w) -> {
							property.setSelectedIconIndex(index);
							buttons.forEach(iconButton -> iconButton.setActive(false));
							btn.setActive(true);
							if (callback != null) {
								callback.run();
							}
						});
				idx++;
			}

			iconButtonListPanel.add(iconPanel);
			iconButtonList.add(buttons);
			if (propertyList.indexOf(property) != propertyList.size() - 1) {
				iconButtonListPanel.add(BaseWidgetFactory.INSTANCE.newDivider(true));
			}
		}
		add(iconButtonListPanel);
	}

	/**
	 * Enabled/disable buttons
	 * @param index index of icon button panel (if multiple)
	 * @param disabled whether buttons should be enabled or disabled
	 */
	public void setDisabled(int index, boolean disabled) {
		iconButtonList.get(index).forEach(button -> button.setDisabled(disabled));
	}

	/**
	 * Deselect all buttons, but button at index for given property, if available.
	 * @param propertyIndex index of property
	 * @param selectedIndex index of button in property button list
	 */
	public void deselectAllBut(int propertyIndex, int selectedIndex) {
		iconButtonList.get(propertyIndex).forEach(button -> button.setActive(false));
		if (selectedIndex > -1 && selectedIndex < iconButtonList.size()) {
			iconButtonList.get(propertyIndex).get(selectedIndex).setActive(true);
		}
	}

	@Override
	public void setLabels() {
		if (label != null) {
			label.setText(appW.getLocalization().getMenu(labelKey));
		}
		iconButtonList.forEach(buttonList -> buttonList.forEach(IconButton::setLabels));
	}

	@Override
	public void configurationUpdated() {
		for (int i = 0; i < propertyList.size(); i++) {
			SingleSelectionIconRow property = propertyList.get(i);
			updateButtons(i, property);
		}
	}

	@Override
	public void visibilityUpdated() {
		for (SingleSelectionIconRow property : propertyList) {
			setVisible(property.isVisible());
		}
	}

	@Override
	public void focus() {
		for (List<IconButton> buttons : iconButtonList) {
			for (IconButton button : buttons) {
				if (button.getElement().getTabIndex() == 0) {
					button.getElement().focus();
					button.addStyleName("keyboardFocus");
					return;
				}
			}
		}
	}

	private void setChecked(IconButton iconButton, boolean checked) {
		AriaHelper.setRole(iconButton, "radio");
		iconButton.setActive(checked);
		AriaHelper.setChecked(iconButton, checked);
	}

	private void addRadioKeyHandler(List<IconButton> buttons, IconButton button, int index) {
		Dom.addEventListener(button.getElement(), "keydown", event -> {
			KeyboardEvent keyEvent = (KeyboardEvent) event;
			switch (keyEvent.code) {
			case "ArrowRight":
			case "ArrowDown":
				int nextButtonIndex = (index + 1) % buttons.size();
				buttons.get(nextButtonIndex).getElement().focus();
				break;
			case "ArrowLeft":
			case "ArrowUp":
				int previousButtonIndex = (index - 1 + buttons.size()) % buttons.size();
				buttons.get(previousButtonIndex).getElement().focus();
				break;
			default:
				break;
			}
		});
	}

	private void updateButtons(int propertyIndex, SingleSelectionIconRow property) {
		List<IconButton> iconButtons = iconButtonList.get(propertyIndex);
		Integer selectedIndex = property.getSelectedIconIndex();
		int focusIndex = selectedIndex == null ? 0 : selectedIndex;
		for (int i = 0; i < iconButtons.size(); i++) {
			updateButton(property, iconButtons.get(i), i, focusIndex);
		}
	}

	private void updateButton(SingleSelectionIconRow property, IconButton iconButton,
			int buttonIndex, int focusIndex) {
		iconButton.setDisabled(!property.isEnabled());
		setChecked(iconButton, focusIndex == buttonIndex);
		iconButton.setTabIndex(focusIndex == buttonIndex ? 0 : -1);
	}
}
