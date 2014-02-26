package geogebra.html5.gui;

import geogebra.web.gui.app.GeoGebraAppFrame;

import com.google.gwt.user.client.ui.HeaderPanel;

public abstract class MyHeaderPanel extends HeaderPanel{

	private GeoGebraAppFrame frame;
	public void setFrame(GeoGebraAppFrame frame){
		this.frame = frame;
	}
	
	public void close() {
	    if(frame != null){
	    	frame.hideBrowser(this);
	    }
	    
    }

}
