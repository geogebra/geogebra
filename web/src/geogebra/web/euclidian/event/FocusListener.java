package geogebra.web.euclidian.event;

import geogebra.common.main.App;
import geogebra.web.gui.inputfield.AutoCompleteTextFieldW;

import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.Panel;

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
