package geogebra.web.euclidian.event;

import geogebra.common.main.AbstractApplication;

import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author judit
 *
 */
public class FocusListener extends geogebra.common.euclidian.event.FocusListener
        implements com.google.gwt.event.dom.client.FocusHandler,
        com.google.gwt.event.dom.client.BlurHandler {

	public FocusListener(Object listener) {
		setListenerClass(listener);
	}

	public void onFocus(FocusEvent event) {
		wrapFocusGained();
		//AbstractApplication.debug("onFocus"); // TODO
	}

	public void onBlur(BlurEvent event) {
		wrapFocusLost();
		//AbstractApplication.debug("onBlur"); // TODO

	}
}
