package org.geogebra.web.html5.gui.view.autocompletion;

import org.geogebra.common.main.App;
import org.geogebra.web.html5.gui.GPopupPanel;
import org.geogebra.web.html5.gui.textbox.GTextBox;
import org.geogebra.web.html5.main.AppW;
import org.gwtproject.user.client.ui.Panel;
import org.gwtproject.user.client.ui.ScrollPanel;
import org.gwtproject.user.client.ui.SuggestOracle;
import org.gwtproject.user.client.ui.Widget;

/**
 * @author bencze
 * Decorator class for SuggestBox
 */
public class ScrollableSuggestBox extends GSuggestBox {

	public static final class CustomSuggestionDisplay extends
	        DefaultSuggestionDisplay {

		public CustomSuggestionDisplay(Panel panel, App app) {
			super(panel, app);
		}

		@Override
		protected Widget decorateSuggestionList(Widget suggestionList) {
			ScrollPanel panel = new ScrollPanel(suggestionList);
			return panel;
		}

		@Override
		protected GPopupPanel createPopup(Panel panel, App app) {
			GPopupPanel su = super.createPopup(panel, app);
			su.addStyleName("ggb-AlgebraViewSuggestionPopup");
			return su;
		}
	}

	/**
	 * Constructor for ScrollableSuggestBox
	 * @param oracle supplies suggestions based upon the current contents of the text widget
	 */
	public ScrollableSuggestBox(SuggestOracle oracle, Panel panel, App app) {
		super(oracle, new GTextBox(false, ((AppW) app).getGlobalHandlers()),
				new CustomSuggestionDisplay(panel, app));
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

}
