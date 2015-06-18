package org.geogebra.web.web.gui.view.algebra;

import org.geogebra.web.html5.gui.view.autocompletion.CompletionsPopup;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.SuggestBox.DefaultSuggestionDisplay;
import com.google.gwt.user.client.ui.SuggestOracle;
import com.google.gwt.user.client.ui.SuggestOracle.Suggestion;
import com.google.gwt.user.client.ui.Widget;

public final class ScrollableSuggestionDisplay extends
        DefaultSuggestionDisplay {

	// as I could not find a better way to know scroll position change,
	// I checked this from web-styles.css (5px padding plus 16px font size)
	// maybe em-size is 19px, 120% of 16px font size... but smaller is OK
	// TODO: improve it, if we can... this still seems smaller (but OK)
	public static int lineWidth = 29;

	protected ScrollPanel scrollable;

	@Override
	protected void moveSelectionDown() {
		super.moveSelectionDown();
		if (scrollable != null)
			scrollable.setVerticalScrollPosition(scrollable
			        .getVerticalScrollPosition() + lineWidth);
	}

	@Override
	protected void moveSelectionUp() {
		super.moveSelectionUp();
		if (scrollable != null)
			scrollable.setVerticalScrollPosition(scrollable
			        .getVerticalScrollPosition() - lineWidth);
	}

	@Override
	protected PopupPanel createPopup() {
		PopupPanel su = super.createPopup();
		su.addStyleName("ggb-AlgebraViewSuggestionPopup");
		return su;
	}

	@Override
	protected Widget decorateSuggestionList(Widget suggestionList) {
		scrollable = new ScrollPanel(suggestionList);

		// heuristic
		updateHeight();
		// it's a good question what this number might be, but on
		// big screen (Window.getClientHeight() / 2) is not a problem
		// and on small screens it may also be necessary

		// in the future we might want to add max-width and remove
		// both overflow-x and overflow-y from
		// ggb-AlgebraViewSuggestionList,
		// at the same time as implementing this behaviour in a different
		// way, but only if there will be a bug report about a too long
		// GeoGebra command syntax help suggestion string
		scrollable.addStyleName("ggb-AlgebraViewSuggestionList");

		return scrollable;
	}

	public void updateHeight() {
		scrollable
				.getElement()
				.getStyle()
				.setProperty("maxHeight", (Window.getClientHeight() / 2) + "px");

	}

	public void accessShowSuggestions(SuggestOracle.Response res,
	        CompletionsPopup pop, SuggestBox.SuggestionCallback xcb) {
		showSuggestions(null, res.getSuggestions(),
		        pop.isDisplayStringHTML(), true, xcb);

		// not working!
		// getPopupPanel().setHeight("50%");
	}

	public Suggestion accessCurrentSelection() {
		return getCurrentSelection();
	}

	public void accessMoveSelectionDown() {
		this.moveSelectionDown();
	}

	public void accessMoveSelectionUp() {
		this.moveSelectionUp();
	}
}