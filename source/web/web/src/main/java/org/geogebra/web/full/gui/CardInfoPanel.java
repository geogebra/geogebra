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

import org.geogebra.web.html5.gui.BaseWidgetFactory;
import org.gwtproject.user.client.ui.FlowPanel;
import org.gwtproject.user.client.ui.Label;
import org.gwtproject.user.client.ui.Widget;

/**
 * Two row info panel for material and preview cards
 *
 * @author laszlo
 */
public class CardInfoPanel extends FlowPanel {
	private Label idLabel;
	private Label titleLabel = null;

	/**
	 *
	 * @param id text
	 * @param titleWidget widget for the second row.
	 */
	public CardInfoPanel(String id, Widget titleWidget) {
		setStyleName("cardInfoPanel");
		addIdLabel(id);
		add(titleWidget);
	}

	/**
	 *
	 * @param heading text.
	 * @param title text.
	 */
	public CardInfoPanel(String heading, String title) {
		setStyleName("cardInfoPanel");
		addIdLabel(heading);
		addTitleWidget(title);
	}

	public CardInfoPanel() {
		this("", "");
	}

	private void addIdLabel(String id) {
		idLabel = BaseWidgetFactory.INSTANCE.newPrimaryText(id, "cardTitle");
		add(idLabel);
	}

	private void addTitleWidget(String title) {
		titleLabel = BaseWidgetFactory.INSTANCE.newSecondaryText(title, "cardAuthor");
		add(titleLabel);
	}

	/**
	 *
	 * @return the card id text.
	 */
	public String getCardId() {
		return idLabel.getText();
	}

	/**
	 * Sets the card title text.
	 * @param id to set.
	 */
	public void setCardId(String id) {
		this.idLabel.setText(id);
	}

	/**
	 * Sets the card title text.
	 * @param title to set.
	 */
	public void setCardTitle(String title) {
		if (idLabel != null) {
			idLabel.setText(title);
		}
	}

	/**
	 * @return the card subtitle if it is a label.
	 */
	public String getCardTitle() {
		return idLabel != null ? idLabel.getText() : "";
	}
}
