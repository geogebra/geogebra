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

import org.geogebra.common.properties.PropertyView;
import org.geogebra.common.properties.PropertyView.MultiSelectionIconRow.ToggleableIcon;
import org.geogebra.web.full.gui.toolbar.mow.toolbox.components.IconButton;
import org.geogebra.web.full.main.AppWFull;
import org.geogebra.web.html5.gui.BaseWidgetFactory;
import org.geogebra.web.html5.main.AppW;
import org.gwtproject.user.client.ui.FlowPanel;

public class MultiSelectionIconRowPanel extends FlowPanel {
	private final AppW appW;

	/**
	 * Creates a panel for {@link org.geogebra.common.properties.PropertyView.MultiSelectionIconRow}
	 * @param multiSelectionIconRow {@code PropertyView.MultiSelectionIconRow}
	 * @param appW see {@link AppW}
	 */
	public MultiSelectionIconRowPanel(PropertyView.MultiSelectionIconRow multiSelectionIconRow,
			AppW appW) {
		this.appW = appW;
		addStyleName("labelStyle");
		buildGUI(multiSelectionIconRow);
	}

	private void buildGUI(PropertyView.MultiSelectionIconRow multiSelectionIconRow) {
		add(BaseWidgetFactory.INSTANCE.newPrimaryText(multiSelectionIconRow.getLabel()));
		for (ToggleableIcon toggleableIcon : multiSelectionIconRow.getToggleableIcons()) {
			IconButton button = new IconButton(appW, null, ((AppWFull) appW)
					.getPropertiesIconResource().getImageResource(toggleableIcon.getIcon()),
					toggleableIcon.getTooltipLabel());
			button.setActive(toggleableIcon.isSelected());
			button.addFastClickHandler(source -> {
				button.setActive(!button.isActive());
				toggleableIcon.setSelected(!toggleableIcon.isSelected());
			});
			add(button);
		}
	}
}
