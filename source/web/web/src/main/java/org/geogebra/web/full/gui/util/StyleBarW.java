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

package org.geogebra.web.full.gui.util;

import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.main.App;
import org.geogebra.common.main.OptionType;
import org.geogebra.web.full.css.GuiResources;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.full.euclidian.ContextMenuPopup;
import org.geogebra.web.full.gui.GuiManagerW;
import org.geogebra.web.full.gui.menubar.MainMenu;
import org.geogebra.web.full.gui.properties.PropertiesViewW;
import org.geogebra.web.full.gui.view.Views;
import org.geogebra.web.full.gui.view.Views.ViewType;
import org.geogebra.web.full.javax.swing.GPopupMenuW;
import org.geogebra.web.html5.css.GuiResourcesSimple;
import org.geogebra.web.html5.gui.util.ViewsChangedListener;
import org.geogebra.web.html5.gui.view.button.StandardButton;
import org.geogebra.web.html5.main.AppW;
import org.gwtproject.user.client.ui.FlowPanel;

/**
 * @author G. Sturr
 * 
 */
public abstract class StyleBarW extends FlowPanel implements
        ViewsChangedListener, SetLabels {

	private ContextMenuPopup viewButton;
	private StandardButton menuButton;
	/**
	 * application
	 */
	public AppW app;
	/**
	 * id of view
	 */
	protected int viewID;
	/**
	 * option type
	 */
	protected OptionType optionType;

	/**
	 * @param app
	 *            {@link AppW}
	 * @param viewID
	 *            {@code int}
	 */
	public StyleBarW(AppW app, int viewID) {
		this.app = app;
		this.viewID = viewID;
		this.app.addViewsChangedListener(this);
		addStyleName("styleBarW");
	}

	/**
	 * @param showStyleBar
	 *            true if open stylebar
	 */
	public abstract void setOpen(boolean showStyleBar);

	/**
	 * adds a {@link VerticalSeparator}
	 */
	protected void addSeparator() {
		VerticalSeparator s = new VerticalSeparator(32);
		add(s);
	}

	/**
	 * adds a {@link StandardButton button} to show properties dialog
	 */
	protected void addMenuButton() {
		if (!app.letShowPropertiesDialog()) {
			return;
		}
		if (menuButton == null) {
			if (app.isUnbundledOrWhiteboard()) {
				menuButton = new StandardButton(
						GuiResources.INSTANCE.stylebar_more());
				menuButton.addStyleName("IconButton-borderless");
			} else {
				menuButton = new StandardButton(
						MaterialDesignResources.INSTANCE.gear(), null, 24);
				menuButton.setStyleName("IconButton");
			}

			menuButton.addFastClickHandler(source -> {
				// close keyboard first to avoid perspective mess
				app.hideKeyboard();
				if (app.getGuiManager().showView(App.VIEW_PROPERTIES)) {
					PropertiesViewW pW = (PropertiesViewW) ((GuiManagerW) app
							.getGuiManager()).getCurrentPropertiesView();

					if (optionType == pW.getOptionType()) {
						app.getGuiManager().setShowView(false,
								App.VIEW_PROPERTIES);
						return;
					}
				}
				if ((!app.getSelectionManager().getSelectedGeos().isEmpty()
						&& optionType != OptionType.ALGEBRA)
						|| optionType == null) {
					app.getDialogManager()
							.showPropertiesDialog(OptionType.OBJECTS, null);
				} else {
					app.getDialogManager().showPropertiesDialog(optionType,
							null);
				}
			});
		}
			
		add(menuButton);
	}
	
	/**
	 * @return view button
	 */
	protected ContextMenuPopup getViewButton() {
		return this.viewButton;
	}
	
	/**
	 * adds a {@link PopupMenuButtonW button} to show a popup, where the user can
	 * either close this view or open another one.
	 */
	protected void addViewButton() {
		GPopupMenuW popup = new GPopupMenuW(app);
		popup.getPopupMenu().addStyleName("viewsContextMenu");

		final int numberOfOpenViews = app.getGuiManager().getLayout()
					.getDockManager().getNumberOfOpenViews();

		if (numberOfOpenViews > 1) {
			// show close button if there are more than 1 views open
			popup.addItem(MainMenu.getMenuBarItem(GuiResourcesSimple.INSTANCE.close(),
					app.getLocalization().getMenu("Close"), () -> {
				app.hideKeyboard();
				app.updateMenubar();
				app.getGuiManager().setShowView(false, viewID);
				app.fireViewsChangedEvent();
			}));
			popup.addSeparator();
		}

		for (ViewType view : Views.getAll()) {
			if (app.supportsView(view.getID())
					&& !app.getGuiManager().showView(view.getID())) {
				popup.addItem(MainMenu.getMenuBarItem(view.getIcon(),
						app.getLocalization().getMenu(view.getKey()), () -> {
					app.hideKeyboard();
					app.updateMenubar();
					app.getGuiManager().setShowView(true, view.getID());
					app.fireViewsChangedEvent();
				}));
			}
		}

		viewButton = new ContextMenuPopup(app, popup);
		viewButton.addStyleName("matDynStyleContextButton");
		add(viewButton);
	}

	@Override
	public void onViewsChanged() {
		if (viewButton != null) {
			remove(viewButton);
			addViewButton();
		}
	}

	@Override
	public void setLabels() {
		if (this.viewButton == null) {
			return;
		}
		remove(viewButton);
		// FIXME ONLY UPDATE TEXT
		addViewButton();
	}
}
