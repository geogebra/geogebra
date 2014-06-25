package geogebra.web.gui.util;

import geogebra.html5.awt.GDimensionW;
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
	
	protected static PopupMenuButton getViewButton(final AppW app, final int viewID){
		ImageOrText[] data = new ImageOrText[Views.ids.length + 1];
		final int[] viewIDs = new int[Views.ids.length];
		int k = 0;
		for(int i = 0; i < Views.ids.length; i++){
			if(app.supportsView(Views.ids[i])){
				data[k] = new ImageOrText(app.getPlain(Views.keys[i]));
				viewIDs[k] = Views.ids[i];
				k++;
			}
		}
		data[k] = new ImageOrText(app.getMenu("Close"));
		final int closeIndex = k;
		final PopupMenuButton pb = new PopupMenuButton(app, data, closeIndex + 1, 1, new GDimensionW(-1,-1), geogebra.common.gui.util.SelectionTable.MODE_TEXT);
		ImageOrText views = new ImageOrText();
		views.url = AppResources.INSTANCE.view_btn().getSafeUri().asString();
		pb.setFixedIcon(views);
		pb.addPopupHandler(new PopupMenuHandler(){

			@Override
            public void fireActionPerformed(Object actionButton) {
				int i = pb.getSelectedIndex();
	            if(pb.getSelectedIndex() == closeIndex){
	            	app.getGuiManager().setShowView(false, viewID);
	            	return;
	            }
	            app.getGuiManager().setShowView(!app.getGuiManager().showView(viewIDs[i]), viewIDs[i]);
	            
	            	
            }});
		return pb;
	}
}
