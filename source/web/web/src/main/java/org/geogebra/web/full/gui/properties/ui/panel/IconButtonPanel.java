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

import static org.geogebra.common.properties.PropertyView.*;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.properties.PropertyResource;
import org.geogebra.web.full.gui.toolbar.mow.toolbox.components.IconButton;
import org.geogebra.web.full.main.AppWFull;
import org.geogebra.web.html5.main.AppW;
import org.gwtproject.user.client.ui.FlowPanel;
import org.gwtproject.user.client.ui.Label;

public class IconButtonPanel extends FlowPanel implements SetLabels, ConfigurationUpdateDelegate,
		VisibilityUpdateDelegate {
	private final AppW appW;
	private Label label;
	private final String labelKey;
	private List<IconButton> iconButtonList;
	private Runnable callback;
	private final SingleSelectionIconRow property;

	/**
	 * Created an icon button panel
	 * @param appW application
	 * @param property {@link org.geogebra.common.properties.PropertyView.SingleSelectionIconRow}
	 * @param addTitle whether title should be added or not
	 */
	public IconButtonPanel(AppW appW, SingleSelectionIconRow property, boolean addTitle) {
		this.appW = appW;
		this.property = property;
		labelKey = property.getLabel();
		buildGUI(addTitle);
		property.setConfigurationUpdateDelegate(this);
		property.setVisibilityUpdateDelegate(this);
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
		if (addTitle) {
			label = new Label(property.getLabel());
			add(label);
		}

		FlowPanel iconPanel = new FlowPanel();
		iconPanel.addStyleName("iconPanel");
		PropertyResource[] icons = property.getIcons().toArray(new PropertyResource[0]);
		String[] labels = property.getToolTipLabels();
		int idx = 0;
		Integer selectedIdx = property.getSelectedIconIndex();
		if (selectedIdx == null) {
			selectedIdx = 0;
		}
		iconButtonList = new ArrayList<>();
		for (PropertyResource icon: icons) {
			String label = labels != null && labels[idx] != null ? labels[idx] : "";
			IconButton btn = new IconButton(appW, null,
					((AppWFull) appW).getPropertiesIconResource().getImageResource(icon), label);
			btn.setActive(selectedIdx == idx);
			iconPanel.add(btn);
			iconButtonList.add(btn);
			final int index = idx;
			btn.addClickHandler(appW.getGlobalHandlers(),
					(w) -> {
						property.setSelectedIconIndex(index);
						iconButtonList.forEach(iconButton -> iconButton.setActive(false));
						btn.setActive(true);
						if (callback != null) {
							callback.run();
						}
					});
			idx++;
		}

		add(iconPanel);
	}

	/**
	 * Enabled/disable buttons
	 * @param disabled whether buttons should be enabled or disabled
	 */
	public void setDisabled(boolean disabled) {
		iconButtonList.forEach(button -> button.setDisabled(disabled));
	}

	@Override
	public void setLabels() {
		if (label != null) {
			label.setText(appW.getLocalization().getMenu(labelKey));
		}
		iconButtonList.forEach(IconButton::setLabels);
	}

	@Override
	public void configurationUpdated() {
		setDisabled(!property.isEnabled());
	}

	@Override
	public void visibilityUpdated() {
		setVisible(property.isVisible());
	}
}
