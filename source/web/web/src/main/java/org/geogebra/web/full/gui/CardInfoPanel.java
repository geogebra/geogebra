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
