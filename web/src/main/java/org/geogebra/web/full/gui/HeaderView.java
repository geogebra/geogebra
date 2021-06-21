package org.geogebra.web.full.gui;

import org.geogebra.web.html5.css.GuiResourcesSimple;
import org.geogebra.web.html5.gui.view.button.StandardButton;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;

/**
 * Header view containing a back button and a label.
 * 
 * @author balazs
 */
public class HeaderView extends FlowPanel {

	private static final String HEADER_VIEW_STYLE_NAME = "headerView";
	private static final String SMALL_HEADER_STYLE_NAME = "smallHeaderView";
	private static final String BACK_BUTTON_STYLE_NAME = "headerBackButton";
	private static final String CAPTION_STYLE_NAME = "headerCaption";
	private static final String DROPPED_HEADER_STYLE_NAME = "droppedHeaderView";
	private static final String COMPACT_HEADER_STYLE_NAME = "compactHeaderView";

	private StandardButton backButton;
	private Label caption;

	/**
	 * Create a HeaderView.
	 */
	public HeaderView() {
		addStyleName(HEADER_VIEW_STYLE_NAME);
		createView();
	}

	private void createView() {
		createButton();
		createCaption();
	}

	private void createButton() {
		backButton = new StandardButton(
				GuiResourcesSimple.INSTANCE.arrow_back(), null, 24);
		backButton.setStyleName(BACK_BUTTON_STYLE_NAME);

		add(backButton);
	}

	private void createCaption() {
		caption = new Label();
		caption.setStyleName(CAPTION_STYLE_NAME);

		add(caption);
	}

	/**
	 * Get the back button of the header
	 * 
	 * @return back button
	 */
	public StandardButton getBackButton() {
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
		setStyleName(SMALL_HEADER_STYLE_NAME, smallScreen);
	}

	/**
	 * Sets the header elevation.
	 *
	 * @param elevated true to show shadow
	 */
	public void setElevated(boolean elevated) {
		setStyleName(DROPPED_HEADER_STYLE_NAME, !elevated);
	}

	/**
	 * Sets the header to compact style.
	 *
	 * @param compact compact
	 */
	public void setCompact(boolean compact) {
		setStyleName(COMPACT_HEADER_STYLE_NAME, compact);
	}
}
