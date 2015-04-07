package org.geogebra.web.html5.gui.view.autocompletion;

import org.geogebra.web.html5.gui.inputfield.HasSymbolPopup;
import org.geogebra.web.html5.gui.textbox.GTextBox;

import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.SuggestOracle;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author bencze
 * Decorator class for SuggestBox
 */
public class ScrollableSuggestBox extends SuggestBox {

	public static final class CustomSuggestionDisplay extends
	        DefaultSuggestionDisplay {

		@Override
		protected Widget decorateSuggestionList(Widget suggestionList) {
			ScrollPanel panel = new ScrollPanel(suggestionList);
			return panel;
		}
	}

	private static GTextBox tb;

	/**
	 * Constructor for ScrollableSuggestBox
	 * @param oracle supplies suggestions based upon the current contents of the text widget
	 */
	public ScrollableSuggestBox(SuggestOracle oracle, HasSymbolPopup hsp) {
		super(oracle, tb = new GTextBox(), new CustomSuggestionDisplay());
		
		 // suggestion from here to disable autocomplete
		 // https://code.google.com/p/google-web-toolkit/issues/detail?id=6065
		 // 
		 // #3878
		tb.getElement().setAttribute("autocomplete", "off");
		tb.getElement().setAttribute("autocapitalize", "off");
		tb.setPopupCallback(hsp);
	}

	/**
	 * Hides the suggestion list.
	 */
	public void hideSuggestions() {
		getDefaultSuggestionDisplay().hideSuggestions();
    }

	public boolean isSuggestionListVisible() {
		return getDefaultSuggestionDisplay().isSuggestionListShowing();
	}
	
	private DefaultSuggestionDisplay getDefaultSuggestionDisplay() {
		return (DefaultSuggestionDisplay) getSuggestionDisplay();
	}

	public void setShowSymbolElement(Element element) {
		// TODO Auto-generated method stub

	}
}
