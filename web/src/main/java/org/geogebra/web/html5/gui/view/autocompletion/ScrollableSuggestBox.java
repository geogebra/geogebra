package org.geogebra.web.html5.gui.view.autocompletion;

import org.geogebra.web.html5.gui.GPopupPanel;
import org.geogebra.web.html5.gui.inputfield.HasSymbolPopup;
import org.geogebra.web.html5.gui.textbox.GTextBox;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SuggestOracle;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author bencze
 * Decorator class for SuggestBox
 */
public class ScrollableSuggestBox extends GSuggestBox {

	public static final class CustomSuggestionDisplay extends
	        DefaultSuggestionDisplay {

		public CustomSuggestionDisplay(Panel panel) {
			super(panel);
		}

		@Override
		protected Widget decorateSuggestionList(Widget suggestionList) {
			ScrollPanel panel = new ScrollPanel(suggestionList);
			return panel;
		}

		@Override
		protected GPopupPanel createPopup(Panel panel) {
			GPopupPanel su = super.createPopup(panel);
			su.addStyleName("ggb-AlgebraViewSuggestionPopup");
			return su;
		}
	}


	/**
	 * Constructor for ScrollableSuggestBox
	 * @param oracle supplies suggestions based upon the current contents of the text widget
	 */
	public ScrollableSuggestBox(SuggestOracle oracle, HasSymbolPopup hsp,
			Panel panel) {
		super(oracle, new GTextBox(false, hsp),
				new CustomSuggestionDisplay(panel));
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
