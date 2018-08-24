package org.geogebra.web.html5.event;

import org.geogebra.common.euclidian.event.FocusListener;

import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;

/**
 * @author judit
 * 
 */
public class FocusListenerW extends FocusListener implements FocusHandler,
        BlurHandler {

	public FocusListenerW(Object listener) {
		setListenerClass(listener);
	}

	/** dummy method */
	public void init() {
		// avoid warnings
	}

	@Override
	public void onFocus(FocusEvent event) {
		wrapFocusGained();
	}

	@Override
	public void onBlur(BlurEvent event) {
		wrapFocusLost();
	}
}
