package geogebra.web.gui.util;

import geogebra.html5.awt.GDimensionW;
import geogebra.html5.css.GuiResources;
import geogebra.html5.gui.view.Views;
import geogebra.web.gui.images.AppResources;
import geogebra.web.main.AppW;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.PopupPanel;

/**
 * @author G. Sturr
 * 
 */
public abstract class StyleBarW extends HorizontalPanel implements ViewsChangedListener {

	protected PopupMenuButton viewButton;
	protected AppW app;
	protected int viewID;

	/**
	 * Constructor
	 */
	public StyleBarW() {
		setStyleName("StyleBar");
		this.addDomHandler(new MouseMoveHandler(){
			@Override
            public void onMouseMove(MouseMoveEvent event) {
	            event.stopPropagation();
            }
		}, MouseMoveEvent.getType());
	}

	
	protected void addSeparator(){
		VerticalSeparator s = new VerticalSeparator(10,25);
		add(s);
	}


	public abstract void setOpen(boolean showStyleBar);
	
	protected void getViewButton(){
		ImageOrText[] data = new ImageOrText[Views.ids.length + 1];
		final int[] viewIDs = new int[Views.ids.length+1];

		data[0] = new ImageOrText(app.getMenu("Close"));
		data[0].url = GuiResources.INSTANCE.dockbar_close().getSafeUri().asString();

		int k = 1;
		for(int i = 0; i < Views.ids.length; i++){
			if(app.supportsView(Views.ids[i]) && !app.getGuiManager().showView(Views.ids[i])){
				data[k] = new ImageOrText(app.getPlain(Views.keys[i]));
				data[k].url = GuiResources.INSTANCE.dockbar_open().getSafeUri().asString();
				viewIDs[k] = Views.ids[i];
				k++;
			}
		}

		if(k != data.length){
			// make sure that data contains no entries that are null
			ImageOrText[] temp = data;
			data = new ImageOrText[k];
			for(int i = 0; i < k; i++){
				data[i] = temp[i];
			}
		}

		viewButton = new PopupMenuButton(app, data, k+1, 1, new GDimensionW(-1,-1), geogebra.common.gui.util.SelectionTable.MODE_TEXT);
		ImageOrText views = new ImageOrText();
		views.url = AppResources.INSTANCE.dots().getSafeUri().asString();
		viewButton.setFixedIcon(views);
		
		geogebra.web.gui.util.SelectionTable table = viewButton.getMyTable();
	    for(int i =  table.getRowCount()-1; i > 1; i--){
	    	table.setWidget(i, 0, table.getWidget(i-1, 0));
	    }

	    FlowPanel separator = new FlowPanel();
	    separator.addStyleName("Separator");
	    table.setWidget(1, 0, separator);

	    viewButton.addClickHandler(new ClickHandler() {
	    	@Override
			public void onClick(ClickEvent event) {
				ImageOrText icon = new ImageOrText();
				icon.url = AppResources.INSTANCE.dots_active().getSafeUri().asString();
				viewButton.setFixedIcon(icon);
			}
		});

	    viewButton.getMyPopup().addCloseHandler(new CloseHandler<PopupPanel>() {
			public void onClose(CloseEvent<PopupPanel> event) {
				ImageOrText icon = new ImageOrText();
				icon.url = AppResources.INSTANCE.dots().getSafeUri().asString();
				viewButton.setFixedIcon(icon);
			}
		});

	    viewButton.addPopupHandler(new PopupMenuHandler(){
			@Override
            public void fireActionPerformed(Object actionButton) {
				int i = viewButton.getSelectedIndex();

				// the first item is the close button
				if(i==0){
					app.getGuiManager().setShowView(false, viewID);
				} else if(i != 1) { // ignore separator
					app.getGuiManager().setShowView(true, viewIDs[i-1]);
				}

				app.updateMenubar();
	            app.fireViewsChangedEvent();
            }});
		add(viewButton);
	}

	@Override
	public void onViewsChanged() {
	    if(viewButton != null){
	    	remove(viewButton);
	    	getViewButton();
	    }
	}
}
