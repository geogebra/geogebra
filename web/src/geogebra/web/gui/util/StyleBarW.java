package geogebra.web.gui.util;

import geogebra.html5.awt.GDimensionW;
import geogebra.html5.css.GuiResources;
import geogebra.html5.gui.FastClickHandler;
import geogebra.html5.gui.StandardButton;
import geogebra.html5.gui.view.Views;
import geogebra.web.gui.images.AppResources;
import geogebra.web.main.AppW;

import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.user.client.ui.HorizontalPanel;

/**
 * @author G. Sturr
 * 
 */
public abstract class StyleBarW extends HorizontalPanel {

	/**
	 * Constructor
	 */
	public StyleBarW() {
		setStyleName("StyleBar");
		this.addDomHandler(new MouseMoveHandler(){

			@Override
            public void onMouseMove(MouseMoveEvent event) {
	            event.stopPropagation();
            }}, MouseMoveEvent.getType());
	}

	
	protected void addSeparator(){
		VerticalSeparator s = new VerticalSeparator(10,25);
		add(s);
	}


	public abstract void setOpen(boolean showStyleBar);
	
	protected void getViewButton(final AppW app, final int viewID){
		ImageOrText[] data = new ImageOrText[Views.ids.length + 1];
		final int[] viewIDs = new int[Views.ids.length];
		int k = 0;
		for(int i = 0; i < Views.ids.length; i++){
			if(app.supportsView(Views.ids[i])){
				data[k] = new ImageOrText(app.getPlain(Views.keys[i]));
				data[k].url = Views.icons[i].getSafeUri().asString();
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

		final PopupMenuButton pb = new PopupMenuButton(app, data, Views.ids.length, 1, new GDimensionW(-1,-1), geogebra.common.gui.util.SelectionTable.MODE_TEXT);
		ImageOrText views = new ImageOrText();
		views.url = AppResources.INSTANCE.dots().getSafeUri().asString();
		pb.setFixedIcon(views);
		pb.addPopupHandler(new PopupMenuHandler(){

			@Override
            public void fireActionPerformed(Object actionButton) {
				int i = pb.getSelectedIndex();
	            
	            app.getGuiManager().setShowView(!app.getGuiManager().showView(viewIDs[i]), viewIDs[i]);
	            app.updateMenubar();
	            	
            }});
		add(pb);
		
		StandardButton close = new StandardButton(GuiResources.INSTANCE.dockbar_close());
		close.addFastClickHandler(new FastClickHandler(){

			@Override
            public void onClick() {
				app.getGuiManager().setShowView(false, viewID);
	            
            }
			
		});
		add(close);
	}
}
