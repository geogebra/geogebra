package org.geogebra.web.web.gui.util;

import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.main.App;
import org.geogebra.common.main.OptionType;
import org.geogebra.web.html5.gui.util.ViewsChangedListener;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.web.css.GuiResources;
import org.geogebra.web.web.gui.app.GGWToolBar;
import org.geogebra.web.web.gui.images.AppResources;
import org.geogebra.web.web.gui.laf.GLookAndFeel;
import org.geogebra.web.web.gui.view.Views;
import org.geogebra.web.web.gui.view.Views.ViewType;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.PopupPanel;

/**
 * @author G. Sturr
 * 
 */
public abstract class StyleBarW extends HorizontalPanel implements
        ViewsChangedListener, SetLabels {

	protected final int ICON_HEIGHT = 24;

	PopupMenuButton viewButton;
	MyCJButton menuButton;
	public AppW app;
	int viewID;
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

	public abstract void setOpen(boolean showStyleBar);

	/**
	 * adds a {@link VerticalSeparator}
	 */
	protected void addSeparator(){
		VerticalSeparator s = new VerticalSeparator(32);
		add(s);
	}

	/**
	 * adds a {@link MyCJButton button} to show properties dialog
	 */
	protected void addMenuButton(){
		if(menuButton == null){
			menuButton = new MyCJButton();
	
			ImageOrText icon = new ImageOrText();
			icon.setUrl(GuiResources.INSTANCE.menu_icon_options().getSafeUri().asString());
			menuButton.setIcon(icon);
	
			menuButton.addClickHandler(new ClickHandler() {
				public void onClick(ClickEvent event) {
					if(app.getGuiManager().showView(App.VIEW_PROPERTIES)){
						app.getGuiManager().setShowView(false, App.VIEW_PROPERTIES);
					} else if(!app.getSelectionManager().getSelectedGeos().isEmpty() || optionType == null){
						app.getDialogManager().showPropertiesDialog(OptionType.OBJECTS, null);
					} else{
						app.getDialogManager().showPropertiesDialog(optionType, null);
					}
	            }
			});
		}
		add(menuButton);
	}
	protected PopupMenuButton getViewButton(){
		return this.viewButton;
	}
	/**
	 * adds a {@link PopupMenuButton button} to show a popup, where the user can
	 * either close this view or open another one.
	 */
	protected void addViewButton(){
		int numOfViews = Views.numOfViews();
		ImageOrText[] data = new ImageOrText[numOfViews + 1];
		final int[] viewIDs = new int[numOfViews + 1];

		int k = 0;
		FlowPanel separator = null;
		final int numberOfOpenViews = app.getGuiManager().getLayout()
					.getDockManager().getNumberOfOpenViews();
		
		if (numberOfOpenViews > 1) {
			// show close button if there are more than 1 views open
			data[0] = new ImageOrText(app.getMenu("Close"));
			data[0].setUrl(GuiResources.INSTANCE.dockbar_close().getSafeUri()
			        .asString());

			// placeholder for the separator (needs to be != null)
			data[1] = new ImageOrText("");
			k = 2;
			separator = new FlowPanel();
			separator.addStyleName("Separator");
		}

		for (ViewType view : Views.getAll()) {
			if (app.supportsView(view.getID())
					&& !app.getGuiManager().showView(view.getID())) {
				data[k] = new ImageOrText(app.getPlain(view.getKey()));
				data[k].setUrl(GGWToolBar.safeURI(view.getIcon()));
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

		viewButton = new PopupMenuButton(app, data, k, 1,
		        org.geogebra.common.gui.util.SelectionTable.MODE_TEXT);
		viewButton.addStyleDependentName("borderless");
		viewButton.addStyleDependentName("rightaligned");
		ImageOrText views = new ImageOrText();
		views.setUrl(AppResources.INSTANCE.dots().getSafeUri().asString());
		viewButton.setFixedIcon(views);
	
		if (separator != null) {
			viewButton.getMyTable().setWidget(1, 0, separator);
		}
			
		viewButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if (!viewButton.getMyPopup().isVisible()) {
					ImageOrText icon = new ImageOrText();
					icon.setUrl(AppResources.INSTANCE.dots().getSafeUri()
					        .asString());
					viewButton.setFixedIcon(icon);
				} else {
					ImageOrText icon = new ImageOrText();
					icon.setUrl(AppResources.INSTANCE.dots_active()
					        .getSafeUri().asString());
					viewButton.setFixedIcon(icon);
				}
			}
		});

	    viewButton.getMyPopup().addCloseHandler(new CloseHandler<PopupPanel>() {
			public void onClose(CloseEvent<PopupPanel> event) {
				ImageOrText icon = new ImageOrText();
				icon.setUrl(AppResources.INSTANCE.dots().getSafeUri().asString());
				viewButton.setFixedIcon(icon);
			}
		});

	    viewButton.addPopupHandler(new PopupMenuHandler(){
			@Override
            public void fireActionPerformed(PopupMenuButton actionButton) {
				int i = viewButton.getSelectedIndex();

				// the first item is the close button
				int closeButtonIndex = 0;
				int separatorIndex = 1;
				if (numberOfOpenViews <= 1) {
					// close button and separator don't exist
					closeButtonIndex = -1; 
					separatorIndex = -1;
				}
				if (i == closeButtonIndex){
					app.getGuiManager().setShowView(false, viewID);
				} else if (i != separatorIndex) { // ignore separator
					app.getGuiManager().setShowView(true, viewIDs[i]);
				}

				app.updateMenubar();
	            app.fireViewsChangedEvent();
            }});
		add(viewButton);
	}
	
	private int getNumberOfOpenViews() {
		int numberOfOpenViews = 0;
		for (ViewType e : Views.getAll()) {
			if (app.supportsView(e.getID())
					&& app.getGuiManager().showView(e.getID())) {
				numberOfOpenViews++;
			}
		}
		return numberOfOpenViews;
	}

	@Override
	public void onViewsChanged() {
	    if(viewButton != null){
	    	remove(viewButton);
	    	addViewButton();
	    }
	}

	public void setLabels() {
		if (this.viewButton == null)
			return;
		remove(viewButton);
		// FIXME ONLY UPDATE TEXT
		addViewButton();
	}
}
