package org.geogebra.web.full.gui.util;

import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.main.App;
import org.geogebra.common.main.OptionType;
import org.geogebra.web.full.css.GuiResources;
import org.geogebra.web.full.gui.GuiManagerW;
import org.geogebra.web.full.gui.images.AppResources;
import org.geogebra.web.full.gui.laf.GLookAndFeel;
import org.geogebra.web.full.gui.properties.PropertiesViewW;
import org.geogebra.web.full.gui.view.Views;
import org.geogebra.web.full.gui.view.Views.ViewType;
import org.geogebra.web.html5.gui.FastClickHandler;
import org.geogebra.web.html5.gui.GPopupPanel;
import org.geogebra.web.html5.gui.util.ImageOrText;
import org.geogebra.web.html5.gui.util.ImgResourceHelper;
import org.geogebra.web.html5.gui.util.ViewsChangedListener;
import org.geogebra.web.html5.gui.view.button.StandardButton;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author G. Sturr
 * 
 */
public abstract class StyleBarW extends HorizontalPanel implements
        ViewsChangedListener, SetLabels {

	private PopupMenuButtonW viewButton;
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
						GuiResources.INSTANCE.menu_icon_options());
				menuButton.setStyleName("MyCanvasButton");
				menuButton.addStyleName("gereBtn");
			}

			menuButton.addFastClickHandler(new FastClickHandler() {
				@Override
				public void onClick(Widget source) {
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
				}
			});
		}
			
		add(menuButton);
	}
	
	/**
	 * @return view button
	 */
	protected PopupMenuButtonW getViewButton() {
		return this.viewButton;
	}
	
	/**
	 * adds a {@link PopupMenuButtonW button} to show a popup, where the user can
	 * either close this view or open another one.
	 */
	protected void addViewButton() {
		int numOfViews = Views.numOfViews();
		ImageOrText[] data = new ImageOrText[numOfViews + 1];
		final int[] viewIDs = new int[numOfViews + 1];

		int k = 0;
		FlowPanel separator = null;
		final int numberOfOpenViews = app.getGuiManager().getLayout()
					.getDockManager().getNumberOfOpenViews();
		
		if (numberOfOpenViews > 1) {
			// show close button if there are more than 1 views open
			data[0] = new ImageOrText(app.getLocalization().getMenu("Close"));
			data[0].setResource(GuiResources.INSTANCE.dockbar_close());

			// placeholder for the separator (needs to be != null)
			data[1] = new ImageOrText("");
			k = 2;
			separator = new FlowPanel();
			separator.addStyleName("Separator");
		}

		for (ViewType view : Views.getAll()) {
			if (app.supportsView(view.getID())
					&& !app.getGuiManager().showView(view.getID())) {
				data[k] = new ImageOrText(
						app.getLocalization().getMenu(view.getKey()));
				data[k].setUrl(ImgResourceHelper.safeURI(view.getIcon()));
				data[k].setBgSize(GLookAndFeel.VIEW_ICON_SIZE);
				viewIDs[k] = view.getID();
				k++;
			}
		}

		if (k != data.length) {
			// make sure that data contains no entries that are null
			ImageOrText[] temp = data;
			data = new ImageOrText[k];
			for (int i = 0; i < k; i++) {
				data[i] = temp[i];
			}
		}

		viewButton = new PopupMenuButtonW(app, data, k, 1,
				org.geogebra.common.gui.util.SelectionTable.MODE_TEXT, false);
		viewButton.addStyleDependentName("borderless");
		viewButton.addStyleDependentName("rightaligned");
		ImageOrText views = new ImageOrText();
		views.setResource(AppResources.INSTANCE.dots());
		viewButton.setFixedIcon(views);
	
		if (separator != null) {
			viewButton.getMyTable().setWidget(1, 0, separator);
		}
			
		viewButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if (!getViewButton().getMyPopup().isVisible()) {
					ImageOrText icon = new ImageOrText(AppResources.INSTANCE
							.dots());
					getViewButton().setFixedIcon(icon);
				} else {
					ImageOrText icon = new ImageOrText(AppResources.INSTANCE
							.dots_active());
					getViewButton().setFixedIcon(icon);
				}
			}
		});

		viewButton.getMyPopup().addCloseHandler(
				new CloseHandler<GPopupPanel>() {
					@Override
					public void onClose(CloseEvent<GPopupPanel> event) {
				ImageOrText icon = new ImageOrText(AppResources.INSTANCE.dots());
						getViewButton().setFixedIcon(icon);
			}
		});

		viewButton.addPopupHandler(new PopupMenuHandler() {
			@Override
			public void fireActionPerformed(PopupMenuButtonW actionButton) {
				int i = getViewButton().getSelectedIndex();

				// the first item is the close button
				int closeButtonIndex = 0;
				int separatorIndex = 1;
				if (numberOfOpenViews <= 1) {
					// close button and separator don't exist
					closeButtonIndex = -1; 
					separatorIndex = -1;
				}
				app.hideKeyboard();
				if (i == closeButtonIndex) {
					app.getGuiManager().setShowView(false, viewID);
				} else if (i != separatorIndex) { // ignore separator
					app.getGuiManager().setShowView(true, viewIDs[i]);
				}

				app.updateMenubar();
				app.fireViewsChangedEvent();
			}
		});
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
