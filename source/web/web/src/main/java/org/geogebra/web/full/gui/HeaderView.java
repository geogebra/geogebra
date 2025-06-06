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
		backButton = new IconButton(appW, new ImageIconSpec(GuiResourcesSimple
				.INSTANCE.arrow_back()), "Back");
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
