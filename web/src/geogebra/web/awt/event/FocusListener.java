package geogebra.web.awt.event;

import geogebra.common.main.AbstractApplication;

import com.google.gwt.event.dom.client.FocusEvent;


/**
 * @author judit
 *
 */
public class FocusListener extends geogebra.common.awt.event.FocusListener implements com.google.gwt.event.dom.client.FocusHandler{

	public FocusListener(Object listener){
		setListenerClass(listener);
	}
	
	public void onFocus(FocusEvent event) {
		AbstractApplication.debug("Implementation needed really"); // TODO
	    
    }



}
