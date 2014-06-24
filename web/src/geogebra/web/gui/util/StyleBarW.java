package geogebra.web.gui.util;

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
	
	
}
