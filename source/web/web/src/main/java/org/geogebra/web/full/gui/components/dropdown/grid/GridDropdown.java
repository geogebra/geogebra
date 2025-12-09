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

import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.background.BackgroundType;
import org.geogebra.web.html5.gui.GPopupPanel;
import org.geogebra.web.html5.gui.util.FastClickHandler;
import org.geogebra.web.html5.gui.view.button.StandardButton;
import org.geogebra.web.html5.main.AppW;
import org.gwtproject.user.client.ui.SimplePanel;
import org.gwtproject.user.client.ui.Widget;

/**
 * Dropdown which opens a grid of images and text.
 */
public class GridDropdown extends SimplePanel implements FastClickHandler {
	private final AppW app;
	private final EuclidianView view;
	private StandardButton button;
	private GPopupPanel popup;
	private GridCardPanel cardPanel;

	/**
	 * Create a new GridDropdown.
	 * @param app - application
	 * @param view - euclidian view
	 */
	public GridDropdown(AppW app, EuclidianView view) {
		this.app = app;
		this.view = view;
		createButton();
		createPopup();
	}

	private void createButton() {
		button = new StandardButton("");
		button.addStyleName("dropdownButton");
		button.addFastClickHandler(this);
		add(button);
	}

	private void createPopup() {
		popup = new GPopupPanel(app.getAppletFrame(), app);
		popup.setAutoHideEnabled(true);
		popup.addStyleName("gridPopup");
		cardPanel = new GridCardPanel(app, view.getSettings().getBackgroundType());
		popup.add(cardPanel);
	}

	/**
	 * Enable or disable the dropdown button.
	 * @param enabled true to enable
	 */
	public void setEnabled(boolean enabled) {
		button.setEnabled(enabled);
	}

	@Override
	public void clear() {
		popup.clear();
	}

	@Override
	public void onClick(Widget event) {
		showGridPopup();
	}

	/**
	 * Show popup close to the button.
	 */
	public void showGridPopup() {
		popup.showRelativeTo(button);
	}

	/**
	 * Set the listener of this GridDropdown.
	 * @param listener the listener
	 */
	public void setListener(GridDropdownListener listener) {
		cardPanel.setListener(listener);
	}

	/**
	 * Set selected index and update ui
	 * @param index - selected background
	 */
	public void setSelectedIndex(int index) {
		popup.hide();
		BackgroundType selectedType = BackgroundType.rulingOptions.get(index);
		String title = app.getLocalization().getMenu(GridDataProvider
				.getTransKeyForRulingType(selectedType));
		button.setText(title);
		cardPanel.setSelectedIndex(index);
	}
}
