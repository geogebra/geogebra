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

import java.util.List;

import org.geogebra.common.properties.PropertyResource;
import org.geogebra.common.properties.PropertyView;
import org.geogebra.common.properties.impl.graphics.LabelStylePropertyCollection;
import org.geogebra.web.full.euclidian.quickstylebar.PropertiesIconAdapter;
import org.geogebra.web.full.gui.toolbar.mow.toolbox.components.IconButton;
import org.geogebra.web.html5.gui.BaseWidgetFactory;
import org.geogebra.web.html5.gui.view.ImageIconSpec;
import org.geogebra.web.html5.main.AppW;
import org.gwtproject.user.client.ui.FlowPanel;

public class LabelStylePanel extends FlowPanel {
	private final AppW appW;

	/**
	 * Creates a panel with label style property
	 * @param labelStylePropertyCollection {@link LabelStylePropertyCollection}
	 * @param appW see {@link AppW}
	 */
	public LabelStylePanel(PropertyView.MultiSelectionIconRow labelStylePropertyCollection,
			AppW appW) {
		this.appW = appW;
		addStyleName("labelStyle");
		buildGUI(labelStylePropertyCollection);
	}

	private void buildGUI(PropertyView.MultiSelectionIconRow labelStylePropertyCollection) {
		add(BaseWidgetFactory.INSTANCE.newPrimaryText(labelStylePropertyCollection.getLabel()));
		List<PropertyResource> icons = labelStylePropertyCollection.getIcons();
		for (int i = 0; i < icons.size(); i++) {
			IconButton button = new IconButton(appW, null,
					new ImageIconSpec(PropertiesIconAdapter.getIcon(icons.get(i))),
					labelStylePropertyCollection.getTooltipLabel(i));
			button.setActive(labelStylePropertyCollection.areIconsSelected().get(i));
			int finalI = i;
			button.addFastClickHandler(source -> {
				button.setActive(!button.isActive());
				labelStylePropertyCollection.setIconSelected(
						finalI, !labelStylePropertyCollection.areIconsSelected().get(finalI));
			});
			add(button);
		}
	}
}
