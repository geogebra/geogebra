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

package org.geogebra.web.full.gui.components.dropdown.grid;

import org.geogebra.common.gui.SetLabels;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.html5.gui.BaseWidgetFactory;
import org.geogebra.web.html5.gui.util.Dom;
import org.geogebra.web.html5.main.AppW;
import org.gwtproject.resources.client.ImageResource;
import org.gwtproject.user.client.ui.FlowPanel;
import org.gwtproject.user.client.ui.Image;
import org.gwtproject.user.client.ui.Label;
import org.gwtproject.user.client.ui.SimplePanel;

public class GridCard extends FlowPanel implements SetLabels {
	private final AppW appW;
	private final ImageResource imageResource;
	private final String titleTransKey;
	private Label cardTitle;

	/**
	 * Constructor building a grid card
	 * @param appW - application
	 * @param imageResource - preview image of card
	 * @param titleTransKey - title of card
	 */
	public GridCard(AppW appW, ImageResource imageResource, String titleTransKey) {
		this.appW = appW;
		this.imageResource = imageResource;
		this.titleTransKey = titleTransKey;
		buildGui();
	}

	private void buildGui() {
		addStyleName("gridCard");

		FlowPanel imagePanel = new FlowPanel();
		Image image = new Image();
		image.setResource(imageResource);
		imagePanel.setStyleName("cardImagePanel");
		imagePanel.add(image);

		FlowPanel checkMarkPanel = new FlowPanel();
		checkMarkPanel.addStyleName("checkMarkPanel");
		SimplePanel checkMark = new SimplePanel();
		checkMark.getElement().setInnerHTML(MaterialDesignResources
				.INSTANCE.check_white().getSVG());
		checkMark.addStyleName("checkmark");
		checkMarkPanel.add(checkMark);
		imagePanel.add(checkMarkPanel);

		add(imagePanel);
		cardTitle = BaseWidgetFactory.INSTANCE.newPrimaryText(
				appW.getLocalization().getMenu(titleTransKey), "cardTitle");
		add(cardTitle);
	}

	/**
	 * Toggle selection.
	 * @param selected whether the card should be selected
	 */
	public void setSelected(boolean selected) {
		Dom.toggleClass(this, "selected", selected);
	}

	public boolean isSelected() {
		return getStyleName().contains("selected");
	}

	@Override
	public void setLabels() {
		cardTitle.setText(appW.getLocalization().getMenu(titleTransKey));
	}
}
