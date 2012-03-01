package geogebra.web.awt.event;

import geogebra.common.main.AbstractApplication;

import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author judit
 *
 */
public class FocusListener extends geogebra.common.awt.event.FocusListener
        implements com.google.gwt.user.client.ui.FocusListener,
        com.google.gwt.event.dom.client.FocusHandler,
        com.google.gwt.event.dom.client.BlurHandler {

	public FocusListener(Object listener) {
		setListenerClass(listener);
	}

	public void onFocus(FocusEvent event) {
		AbstractApplication.debug("Implementation needed really"); // TODO
	}

	public void onBlur(BlurEvent event) {
		AbstractApplication.debug("Implementation needed really"); // TODO

	}

	@Deprecated
	//TODO: replace with onFocus(FocusEvent event) or something else method
	public void onFocus(Widget sender) {
		wrapFocusGained();
	    
    }

	@Deprecated
	//TODO: replace with onBlur(BlurEvent event) or something else method
	public void onLostFocus(Widget sender) {
		wrapFocusLost();
	    
    }

}
