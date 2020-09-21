package org.geogebra.web.full.gui;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

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
		titleLabel = new Label(title);
		addTitleWidget(titleLabel);
	}

	public CardInfoPanel() {
		this("", "");
	}

	private void addIdLabel(String id) {
		idLabel = new Label(id);
		idLabel.setStyleName("cardTitle");
		add(idLabel);
	}

	private void addTitleWidget(Widget titleRow) {
		titleRow.setStyleName("cardAuthor");
		add(titleRow);
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
		if (titleLabel != null) {
			titleLabel.setText(title);
		}
	}

	/**
	 * @return the card subtitle if it is a label.
	 */
	public String getCardTitle() {
		return titleLabel != null ? titleLabel.getText() : "";
	}
}
