package geogebra.html5.event;

import geogebra.html5.gui.inputfield.AutoCompleteTextFieldW;

import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.FocusEvent;

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
		Object source = event.getSource();
		AutoCompleteTextFieldW.showSymbolButtonIfExists(source, true);
		wrapFocusGained();
	}

	public void onBlur(BlurEvent event) {
		Object source = event.getSource();
		AutoCompleteTextFieldW.showSymbolButtonIfExists(source, false);
		wrapFocusLost();
	}
}
