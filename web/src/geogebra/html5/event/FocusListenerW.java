package geogebra.html5.event;

import geogebra.common.euclidian.event.FocusListener;
import geogebra.html5.gui.inputfield.AutoCompleteTextFieldW;

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
