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
	private Label titleRow;

	/**
	 *
	 * @param title text
	 * @param subtitleRow widget for the second row.
	 */
	public CardInfoPanel(String title, Widget subtitleRow) {
		addTitleRow(title);
		addSubtitleRow(subtitleRow);
	}

	public CardInfoPanel() {
		this("", "subtitle");
	}

	private void addTitleRow(String title) {
		titleRow = new Label(title);
		titleRow.setStyleName("cardTitle");
		add(titleRow);
	}

	private void addSubtitleRow(Widget subtitleRow) {
		subtitleRow.setStyleName("cardAuthor");
		add(subtitleRow);
	}

	/**
	 *
	 * @param title text.
	 * @param subtitle text.
	 */
	public CardInfoPanel(String title, String subtitle) {
		setStyleName("cardInfoPanel");
		addTitleRow(title);
		addSubtitleRow(new Label(subtitle));
	}

	/**
	 *
	 * @return the card title text.
	 */
	public String getCardTitle() {
		return titleRow.getText();
	}

	/**
	 * Sets the card title text.
	 * @param title to set.
	 */
	public void setCardTitle(String title) {
		titleRow.setText(title);
	}
}
