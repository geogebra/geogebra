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

import static org.geogebra.common.properties.PropertyView.*;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.euclidian.EuclidianViewInterfaceCommon;
import org.geogebra.common.gui.dialog.handler.ColorChangeHandler;
import org.geogebra.common.main.OptionType;
import org.geogebra.common.properties.PropertyView;
import org.geogebra.common.properties.impl.graphics.GridStyleIconProperty;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.full.gui.components.dropdown.grid.GridDialog;
import org.geogebra.web.full.gui.dialog.DialogManagerW;
import org.geogebra.web.full.gui.menubar.MainMenu;
import org.geogebra.web.full.gui.properties.ui.panel.IconButtonPanel;
import org.geogebra.web.full.javax.swing.GCheckmarkMenuItem;
import org.geogebra.web.html5.gui.menu.AriaMenuItem;
import org.geogebra.web.html5.gui.view.IconSpec;
import org.geogebra.web.html5.gui.view.button.StandardButton;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.main.topbar.TopBarIcon;
import org.geogebra.web.shared.components.dialog.DialogData;

/**
 * euclidian view/graphics view context menu
 */
public class ContextMenuGraphicsWindowW extends ContextMenuGeoElementW {

	/**
	 * @param app application
	 */
	protected ContextMenuGraphicsWindowW(AppW app) {
		super(app, new ContextMenuItemFactory());
	}

	/**
	 * @param app application
	 * @param showPaste whether to show the paste button (false for the graphics settings button)
	 */
	public ContextMenuGraphicsWindowW(AppW app, boolean showPaste) {
		this(app);

		EuclidianViewInterfaceCommon ev = app.getActiveEuclidianView();
		OptionType optionType = ev.getEuclidianViewNo() == 1
				? OptionType.EUCLIDIAN : OptionType.EUCLIDIAN2;

		if (!app.isWhiteboardActive()) {
			addAxesMenuItem();
			addGridMenuItem();
			addGridTypeItem();
			addSettingsButton(optionType);
		} else {
			if (showPaste) {
				addPasteItem();
				wrappedPopup.addSeparator();
			}
			addRulingMenuItem();
			addBackgroundMenuItem();
			addMiProperties(optionType);
		}
	}

	/**
	 * add axes menu item with check mark
	 */
	protected void addAxesMenuItem() {
		boolean checked = app.getActiveEuclidianView().getShowXaxis()
				&& app.getActiveEuclidianView().getShowYaxis();
		final GCheckmarkMenuItem showAxes = new GCheckmarkMenuItem(loc.getMenu("ShowAxes"),
				checked, app.getGuiManager()::showAxesCmd);
		wrappedPopup.addItem(showAxes);
	}

	private void addGridMenuItem() {
		boolean checked = app.getActiveEuclidianView().getShowGrid();
		final GCheckmarkMenuItem showGrid = new GCheckmarkMenuItem(loc.getMenu("ShowGrid"),
				checked, app.getGuiManager()::showGridCmd);
		wrappedPopup.addItem(showGrid);
	}

	private void addGridTypeItem() {
		SingleSelectionIconRow gridTypeProperty = (SingleSelectionIconRow) PropertyView
				.of(new GridStyleIconProperty(loc, app.getActiveEuclidianView().getSettings()));

		if (gridTypeProperty == null) {
			return;
		}

		IconButtonPanel gridTypePanel = new IconButtonPanel((AppW) app, gridTypeProperty, false,
				wrappedPopup::hide);
		gridTypePanel.setDisabled(!app.getActiveEuclidianView().getShowGrid());
		AriaMenuItem gridTypeItem = new AriaMenuItem(gridTypePanel.getWidget(0), () -> {});
		gridTypeItem.addStyleName("iconButtonPanel");
		wrappedPopup.addItem(gridTypeItem);
	}

	protected void addSettingsButton(OptionType optionType) {
		StandardButton settingsButton = new StandardButton(loc.getMenu("General.OpenSettings"));
		settingsButton.addFastClickHandler(source -> {
			showOptionsDialog(optionType);
			wrappedPopup.hide();
		});
		settingsButton.addStyleName("materialOutlinedButton");
		AriaMenuItem settingsItem = new AriaMenuItem(settingsButton, () -> {});
		settingsItem.addStyleName("settingsItem");
		wrappedPopup.addItem(settingsItem);
	}

	private void addRulingMenuItem() {
		AriaMenuItem rulingMenuItem =
				MainMenu.getMenuBarItem(
						MaterialDesignResources.INSTANCE.minor_gridlines(),
						loc.getMenu("Ruling"),
				() -> {
					DialogData data = new DialogData("Ruling", "Cancel", "Save");
					GridDialog gridDialog = new GridDialog((AppW) app, data);
					gridDialog.show();
				});

		wrappedPopup.addItem(rulingMenuItem);
	}

	private void addBackgroundMenuItem() {
		AriaMenuItem miBackgroundCol =
				MainMenu.getMenuBarItem(((AppW) app).getTopBarIconResource().getImageResource(
						TopBarIcon.COLOR), loc.getMenu("BackgroundColor"), this::openColorChooser);
		wrappedPopup.addItem(miBackgroundCol);
	}

	/**
	 * open color chooser dialog to select graphics background
	 */
	protected void openColorChooser() {
		((DialogManagerW) app.getDialogManager()).showColorChooserDialog(
				app.getSettings().getEuclidian(1).getBackground(),
				new ColorChangeHandler() {

					@Override
					public void onForegroundSelected() {
						// do nothing
					}

					@Override
					public void onColorChange(GColor color) {
						// change graphics background color
						app.getSettings().getEuclidian(1).setBackground(color);
					}

					@Override
					public void onClearBackground() {
						// do nothing
					}

					@Override
					public void onBarSelected() {
						// do nothing
					}

					@Override
					public void onBackgroundSelected() {
						// do nothing
					}

					@Override
					public void onAlphaChange() {
						// do nothing
					}
				});
	}

	/**
	 * @param type of option
	 */
	protected void addMiProperties(final OptionType type) {
		IconSpec gearIcon = ((AppW) app).getTopBarIconResource().getImageResource(
				TopBarIcon.SETTINGS);

		AriaMenuItem miProperties =
				MainMenu.getMenuBarItem(gearIcon, loc.getMenu("Settings"),
				() -> showOptionsDialog(type));
		miProperties.setEnabled(true); // TMP AG
		wrappedPopup.addItem(miProperties);
	}

	/**
	 * @param type of option
	 */
	protected void showOptionsDialog(OptionType type) {
		if (app.getGuiManager() != null) {
			app.getDialogManager().showPropertiesDialog(type, null);
		}
	}
}
