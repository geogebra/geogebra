package org.geogebra.web.full.gui.inputfield;

import org.geogebra.common.main.App;
import org.geogebra.web.html5.gui.GPopupPanel;
import org.geogebra.web.html5.gui.view.autocompletion.CompletionsPopup;
import org.geogebra.web.html5.gui.view.autocompletion.GSuggestBox;
import org.geogebra.web.html5.gui.view.autocompletion.GSuggestBox.DefaultSuggestionDisplay;

import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SuggestOracle;
import com.google.gwt.user.client.ui.SuggestOracle.Suggestion;
import com.google.gwt.user.client.ui.Widget;

//import org.geogebra.web.cas.latex.EquationEditor;

public final class ScrollableSuggestionDisplay extends
        DefaultSuggestionDisplay {

	// as I could not find a better way to know scroll position change,
	// I checked this from web-styles.css (5px padding plus 16px font size)
	// maybe em-size is 19px, 120% of 16px font size... but smaller is OK
	// TODO: improve it, if we can... this still seems smaller (but OK)
	public final static int LINE_HEIGHT = 29;
	private HasSuggestions editor;
	private ScrollPanel scrollable;

	/**
	 * @param ed
	 *            editor
	 * @param panel
	 *            panel
	 * @param app
	 *            application
	 */
	public ScrollableSuggestionDisplay(HasSuggestions ed, Panel panel,
			App app) {
		super(panel, app);
		this.editor = ed;
	}

	@Override
	protected void moveSelectionDown() {
		super.moveSelectionDown();
		if (scrollable != null) {
			scrollable.setVerticalScrollPosition(scrollable
			        .getVerticalScrollPosition() + LINE_HEIGHT);
		}
	}

	@Override
	protected void moveSelectionUp() {
		super.moveSelectionUp();
		if (scrollable != null) {
			scrollable.setVerticalScrollPosition(scrollable
			        .getVerticalScrollPosition() - LINE_HEIGHT);
		}
	}

	@Override
	protected GPopupPanel createPopup(Panel panel, App app) {
		GPopupPanel su = super.createPopup(panel, app);
		su.addStyleName("ggb-AlgebraViewSuggestionPopup");
		if (app.isUnbundled()) {
			su.addStyleName("matDesign");
		}
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

	/**
	 * Update the height.
	 */
	public void updateHeight() {
		if (editor == null) {
			return;
		}
		scrollable
				.getElement()
				.getStyle()
				.setProperty("maxHeight",
						editor.getMaxSuggestionsHeight() + "px");

	}

	/**
	 * @param res
	 *            oracle response
	 * @param pop
	 *            popup
	 * @param xcb
	 *            callback
	 */
	public void accessShowSuggestions(SuggestOracle.Response res,
			CompletionsPopup pop, GSuggestBox.SuggestionCallback xcb) {
		showSuggestions(null, res.getSuggestions(), pop.isDisplayStringHTML(),
				true, xcb);
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

	/**
	 * Focus suggestion element
	 */
	public void focus() {
		getSuggestionMenu().getElement().focus();
	}

}