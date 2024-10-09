package org.geogebra.web.full.gui.components.dropdown.grid;

import org.geogebra.web.html5.gui.util.FastClickHandler;
import org.geogebra.web.html5.gui.view.button.StandardButton;
import org.geogebra.web.html5.main.AppW;
import org.gwtproject.resources.client.ImageResource;
import org.gwtproject.user.client.ui.SimplePanel;
import org.gwtproject.user.client.ui.Widget;

/**
 * Dropdown which opens a grid of images and text.
 */
public class GridDropdown extends SimplePanel implements FastClickHandler {
	private final AppW app;
	private StandardButton button;
	private final GridPopup popup;

	/**
	 * Create a new GridDropdown.
	 *
	 * @param app app
	 */
	public GridDropdown(AppW app) {
		this.app = app;
		createButton();
		popup = new GridPopup(app, getUpdateParentCB(), 3);
	}

	private void createButton() {
		button = new StandardButton("");
		button.addStyleName("dropdownButton");
		button.addFastClickHandler(this);
		add(button);
	}

	/**
	 * Enable or disable the dropdown button.
	 *
	 * @param enabled true to enable
	 */
	public void setEnabled(boolean enabled) {
		button.setEnabled(enabled);
	}

	private Runnable getUpdateParentCB() {
		return () -> {
			String title = "";
			if (popup.getSelectedIndex() >= 0) {
				title = app.getLocalization().getMenu(popup.getSelectedItemText());
			}
			button.setText(title);
		};
	}

	/**
	 * Adds an item to the dropdown.
	 *
	 * @param titleTransKey title of the cell
	 * @param resource image to display
	 */
	public void addItem(String titleTransKey, ImageResource resource) {
		popup.addItem(titleTransKey, resource);
	}

	@Override
	public void clear() {
		popup.clear();
	}

	@Override
	public void onClick(Widget event) {
		showGridPopup();
	}

	public void showGridPopup() {
		popup.showGridPopup(button);
	}

	/**
	 * Set the listener of this GridDropdown.
	 *
	 * @param listener the listener
	 */
	public void setListener(GridPopup.GridDropdownListener listener) {
		popup.setListener(listener);
	}

	public void setSelectedIndex(int index) {
		popup.setSelectedIndex(index);
	}
}
