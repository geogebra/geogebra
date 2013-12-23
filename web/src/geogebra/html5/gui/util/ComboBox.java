package geogebra.html5.gui.util;

import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.SuggestBox;

public class ComboBox extends SuggestBox {
	public ComboBox() {
		super(new MultiWordSuggestOracle());
		getValueBox().addFocusHandler(new FocusHandler() {

			public void onFocus(FocusEvent event) {
	            showSuggestionList();
	        }

		});
		
		getValueBox().addBlurHandler(new BlurHandler(){

			public void onBlur(BlurEvent event) {
				hideSuggestionList();
	
            }});
		
	}
	
	public void clear() {
		((MultiWordSuggestOracle) getSuggestOracle()).clear();
	}
	
	public void addItem(final String item) {
		((MultiWordSuggestOracle) getSuggestOracle()).add(item);
	}
}
