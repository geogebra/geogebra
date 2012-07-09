package geogebra.web.euclidian.event;

import geogebra.common.euclidian.event.ActionListener;
import geogebra.common.euclidian.event.ActionListenerI;

import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventListener;

public class ActionListenerW extends ActionListener implements EventListener{

	public ActionListenerW(ActionListenerI listener) {
		setListenerClass(listener);
	}
	
	public void onBrowserEvent(Event e) {
	    geogebra.web.euclidian.event.ActionEventW event = geogebra.web.euclidian.event.ActionEventW.wrapEvent(e);
	    
    }
	
	

}
