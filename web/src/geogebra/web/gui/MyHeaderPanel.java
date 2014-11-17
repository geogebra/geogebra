package geogebra.web.gui;

import com.google.gwt.user.client.ui.HeaderPanel;

public abstract class MyHeaderPanel extends HeaderPanel{

	private HeaderPanelDeck frame;
	public void setFrame(HeaderPanelDeck frame){
		this.frame = frame;
	}
	
	public void close() {
	    if(frame != null){
	    	frame.hideBrowser(this);
	    }
	    
    }

}
