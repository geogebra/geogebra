package org.geogebra.web.html5.gui.view.autocompletion;

import org.geogebra.web.html5.gui.inputfield.HasSymbolPopup;
import org.geogebra.web.html5.gui.textbox.GTextBox;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.PopupPanel;
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

		@Override
		protected PopupPanel createPopup() {
			PopupPanel su = super.createPopup();
			su.addStyleName("ggb-AlgebraViewSuggestionPopup");
			return su;
		}
	}


	/**
	 * Constructor for ScrollableSuggestBox
	 * @param oracle supplies suggestions based upon the current contents of the text widget
	 */
	public ScrollableSuggestBox(SuggestOracle oracle, HasSymbolPopup hsp) {
		super(oracle, new GTextBox(false, hsp),
				new CustomSuggestionDisplay());
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
