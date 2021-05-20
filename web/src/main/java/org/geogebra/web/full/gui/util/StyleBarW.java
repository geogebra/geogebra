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
import org.geogebra.web.html5.gui.util.AriaMenuItem;
import org.geogebra.web.html5.gui.util.ViewsChangedListener;
import org.geogebra.web.html5.gui.view.button.StandardButton;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.user.client.ui.HorizontalPanel;

/**
 * @author G. Sturr
 * 
 */
public abstract class StyleBarW extends HorizontalPanel implements
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
	 * adds a {@link MyCJButton button} to show properties dialog
	 */
	protected void addMenuButton() {
		if (!app.letShowPropertiesDialog()) {
			return;
		}
		if (menuButton == null) {
			if (app.isUnbundledOrWhiteboard()) {
				menuButton = new StandardButton(
						GuiResources.INSTANCE.stylebar_more());
				menuButton.addStyleName("MyCanvasButton-borderless");
			} else {
				menuButton = new StandardButton(
						MaterialDesignResources.INSTANCE.gear(), null, 24);
				menuButton.setStyleName("MyCanvasButton");
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
			String html = MainMenu.getMenuBarHtml(
					GuiResourcesSimple.INSTANCE.close(),
					app.getLocalization().getMenu("Close")
			);
			popup.addItem(new AriaMenuItem(html, true, () -> {
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
				String html = MainMenu.getMenuBarHtml(
						view.getIcon(),
						app.getLocalization().getMenu(view.getKey())
				);

				popup.addItem(new AriaMenuItem(html, true, () -> {
					app.hideKeyboard();
					app.updateMenubar();
					app.getGuiManager().setShowView(true, view.getID());
					app.fireViewsChangedEvent();
				}));
			}
		}

		viewButton = new ContextMenuPopup(app, popup);
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
