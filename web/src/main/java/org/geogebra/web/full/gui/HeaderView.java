package org.geogebra.web.full.gui;

import org.geogebra.common.main.App;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.html5.gui.view.button.StandardButton;
import org.geogebra.web.html5.util.Dom;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;

/**
 * Header view containing a back button and a label.
 * 
 * @author balazs
 */
public class HeaderView extends FlowPanel {

	private StandardButton backButton;
	private Label caption;

	/**
	 * Create a HeaderView.
	 * 
	 * @param app
	 *            application
	 */
	public HeaderView(App app) {
		createView(app);
	}

	private void createView(App app) {
		addStyleName("headerView");
		createButton(app);
		createCaption();
	}

	private void createButton(App app) {
		backButton = new StandardButton(
				MaterialDesignResources.INSTANCE.mow_back_arrow(), null, 24,
				app);
		backButton.setStyleName("headerBackButton");

		add(backButton);
	}

	private void createCaption() {
		caption = new Label();
		caption.setStyleName("headerCaption");

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
		Dom.toggleClass(this, "smallHeaderView", "headerView", smallScreen);
	}
}
