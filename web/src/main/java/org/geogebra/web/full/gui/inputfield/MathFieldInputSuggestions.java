package org.geogebra.web.full.gui.inputfield;

import java.util.List;

import javax.annotation.Nonnull;

import org.geogebra.web.html5.gui.inputfield.AutoCompleteW;
import org.geogebra.web.html5.gui.inputfield.InputSuggestions;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.user.client.ui.SuggestOracle;

public class MathFieldInputSuggestions extends InputSuggestions
		implements HasSuggestions {

	private static final int MINIMUM_HEIGHT = 29;

	private ScrollableSuggestionDisplay sug;
	StringBuilder curWord;
	protected AutoCompletePopup autoCompletePopup;
	private @Nonnull AutoCompleteW component;

	/**
	 * @param app
	 *            application
	 * @param component
	 *            text input
	 * @param forCAS
	 *            whether to use this for CAS
	 */
	public MathFieldInputSuggestions(AppW app, AutoCompleteW component,
			boolean forCAS) {
		super(app, forCAS);
		this.component = component;
		curWord = new StringBuilder();
		autoCompletePopup = new AutoCompletePopup(app);
		sug = new ScrollableSuggestionDisplay(this, app.getPanel(), app);
	}

	/**
	 * @param searchRight
	 *            TODO whether to check chars to the right?
	 */
	public void updateCurrentWord(boolean searchRight) {
		curWord = new StringBuilder(component.getCommand());
	}

	/**
	 * @param sugg
	 *            suggestion
	 */
	public void autocompleteAndHide(SuggestOracle.Suggestion sugg) {
		component.insertString(sugg.getReplacementString());
		sug.hideSuggestions();
	}

	/**
	 * Show suggestions.
	 */
	public void popupSuggestions() {
		// sub, or query is the same as the current word,
		// so moved from method parameter to automatism
		// updateCurrentWord(true);// although true would be nicer here
		updateCurrentWord(false); // compatibility should be preserved
		if (curWord != null && curWord.length() > 0
				&& !"sqrt".equals(curWord.toString())) {
			// for length check we also need flattenKorean
			if (!needsAutocomplete(this.curWord)) {
				// if there is only one letter typed,
				// for any reason, this method should
				// hide the suggestions instead!
				autoCompletePopup.hide();
			} else {
				autoCompletePopup.fillAndShow(curWord.toString());
			}
		} else {
			autoCompletePopup.hide();
		}
	}

	public boolean isSuggesting() {
		return sug.isSuggestionListShowing();
	}

	/**
	 * @return whether enter should be consumed by suggestions
	 */
	public boolean needsEnterForSuggestion() {
		if (sug.isSuggestionListShowing()) {
			autocompleteAndHide(sug.accessCurrentSelection());
			return true;
		}
		return false;
	}

	public void onKeyDown() {
		sug.moveSelectionDown();
	}

	public void onKeyUp() {
		sug.moveSelectionUp();
	}

	/**
	 * @return completions for current word
	 */
	public List<String> resetCompletions() {
		updateCurrentWord(false);
		return resetCompletions(curWord);
	}

	@Override
	public double getMaxSuggestionsHeight() {
		AppW app = component.getApplication();

		double spaceBelow = app.getHeight() + app.getAbsTop()
				- component.getAbsoluteTop()
				- component.toWidget().getOffsetHeight()
				- app.getAppletFrame().getKeyboardHeight();

		return Math.max(MINIMUM_HEIGHT, Math.min(app.getHeight() / 2, spaceBelow));
	}
}
