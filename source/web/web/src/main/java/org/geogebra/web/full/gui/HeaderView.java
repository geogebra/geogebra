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

package org.geogebra.web.full.gui;

import org.geogebra.web.full.gui.toolbar.mow.toolbox.components.IconButton;
import org.geogebra.web.html5.css.GuiResourcesSimple;
import org.geogebra.web.html5.gui.BaseWidgetFactory;
import org.geogebra.web.html5.gui.view.ImageIconSpec;
import org.geogebra.web.html5.main.AppW;
import org.gwtproject.user.client.ui.FlowPanel;
import org.gwtproject.user.client.ui.Label;

/**
 * Header view containing a back button and a label.
 */
public class HeaderView extends FlowPanel {
	private IconButton backButton;
	private Label caption;

	/**
	 * Create a HeaderView.
	 * @param appW {@link AppW}
	 */
	public HeaderView(AppW appW) {
		addStyleName("headerView");
		createView(appW);
	}

	private void createView(AppW appW) {
		createButton(appW);
		createCaption();
	}

	private void createButton(AppW appW) {
		backButton = new IconButton(appW, "Back", new ImageIconSpec(GuiResourcesSimple
				.INSTANCE.arrow_back()));
		backButton.addStyleName("headerBackButton");

		add(backButton);
	}

	private void createCaption() {
		caption = BaseWidgetFactory.INSTANCE.newPrimaryText("", "headerCaption");
		add(caption);
	}

	/**
	 * Get the back button of the header
	 * 
	 * @return back button
	 */
	public IconButton getBackButton() {
		return backButton;
	}

	/**
	 * Set the caption for the view.
	 * 
	 * @param text
	 *            caption
	 */
	public void setCaption(String text) {
		caption.setText(text);
	}

	/**
	 * Adjust the CSS class for small / big screen
	 * 
	 * @param smallScreen
	 *            whether to use smallscreen design
	 */
	public void resizeTo(boolean smallScreen) {
		setStyleName("smallHeaderView", smallScreen);
	}

	/**
	 * Sets the header elevation.
	 *
	 * @param elevated true to show shadow
	 */
	public void setElevated(boolean elevated) {
		setStyleName("droppedHeaderView", !elevated);
	}

	/**
	 * Sets the header to compact style.
	 *
	 * @param compact compact
	 */
	public void setCompact(boolean compact) {
		setStyleName("compactHeaderView", compact);
	}
}
