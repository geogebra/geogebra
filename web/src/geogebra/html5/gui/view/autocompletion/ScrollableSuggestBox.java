package geogebra.html5.gui.view.autocompletion;

import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.SuggestOracle;
import com.google.gwt.user.client.ui.TextBox;
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

	private static TextBox tb;

	/**
	 * Constructor for ScrollableSuggestBox
	 * @param oracle supplies suggestions based upon the current contents of the text widget
	 */
	public ScrollableSuggestBox(SuggestOracle oracle) {
		super(oracle, tb = new TextBox(), new CustomSuggestionDisplay());
		
		 // suggestion from here to disable autocomplete
		 // https://code.google.com/p/google-web-toolkit/issues/detail?id=6065
		 // 
		 // #3878
		tb.getElement().setAttribute("autocomplete", "off");
	}

}
